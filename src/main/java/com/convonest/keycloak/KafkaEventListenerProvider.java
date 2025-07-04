package com.convonest.keycloak;

import com.convonest.keycloak.model.LoggedOutUserRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.convonest.keycloak.Constants.GOOGLE;
import static com.convonest.keycloak.Constants.MAX_RETRIES;
import static com.convonest.keycloak.Constants.RETRY_DELAY;
import static com.convonest.keycloak.Constants.TARGET_REACT_CLIENT_ID;
import static com.convonest.keycloak.SessionManagementUtil.handleExpiredSession;

public class KafkaEventListenerProvider implements EventListenerProvider {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventListenerProvider.class);

    private KafkaProducer<String, String> stringProducer;
    private final KeycloakSession session;
    private final String onBoardingTopic;

    private KafkaProducer<String, LoggedOutUserRecord> modelProducer;
    private final String loggedOutTopic;

    public KafkaEventListenerProvider(KeycloakSession session) {
        this.session = session;
        // Get the singleton instance from KafkaManager
        this.stringProducer = KafkaManager.createStringProducer();
        this.onBoardingTopic = KafkaManager.getOnBoardingTopic();

        this.modelProducer = KafkaManager.createModelProducer();
        this.loggedOutTopic = KafkaManager.getLoggedOutTopic();
        // Run the configuration logic on startup
        configureGoogleLoginIfNeeded();
        logger.info("KafkaEventListenerProvider initialized with on boarding topic: {} , {}", this.onBoardingTopic, this.loggedOutTopic);
    }

    @Override
    public void onEvent(Event event) {
        logger.info("Received event of type: {}, clientId: {} ,", event.getType(), event.getClientId());

        if (event.getType() == EventType.REGISTER) {
            String emailId = event.getDetails().get("email");

            logger.info("Publishing register message to Kafka topic {}: {}", onBoardingTopic, emailId);

            boolean success = false;
            int attempt = 0;

            while (!success && attempt < MAX_RETRIES) {
                try {
                    // Ensure the producer is valid
                    if (stringProducer == null || isStringProducerClosed()) {
                        logger.warn("Kafka producer is unavailable. Reinitializing...");
                        stringProducer = KafkaManager.createStringProducer();
                    }
                    logger.warn("stringProducer  {}", stringProducer);
                    // Asynchronously send the message
                    stringProducer.send(new ProducerRecord<>(onBoardingTopic, emailId), (metadata, exception) -> {
                        if (exception != null) {
                            logger.error("Error sending message to Kafka topic {}: {}", onBoardingTopic,
                                    exception.getMessage());
                        } else {
                            logger.debug("Message sent to Kafka topic {} with offset {}", metadata.topic(),
                                    metadata.offset());
                        }
                    });

                    success = true; // If no exception, set success to true

                } catch (Exception e) {
                    attempt++;
                    logger.warn("Attempt {}/{}: Failed to send message to Kafka. Retrying in {} ms...", attempt,
                            MAX_RETRIES, RETRY_DELAY, e);
                    try {
                        TimeUnit.MILLISECONDS.sleep(RETRY_DELAY);
                    } catch (InterruptedException ie) {
                        logger.error("Registration process retry interrupted", ie);
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (!success) {
                logger.error("CRITICAL ERROR: Failed to send message to Kafka after {} attempts, EmailId: {}",
                        MAX_RETRIES, emailId);
            }
        } else if (event.getType() == EventType.CLIENT_LOGIN || event.getType() == EventType.LOGIN) {

            // If user is logging in then looking into all old active session for that specific user and removing it
            if (TARGET_REACT_CLIENT_ID.equals(event.getClientId())) {
                logger.info("Client login Data: id: {} , userId:{}  , getSessionId: {}, Map: {}", event.getId(),
                        event.getUserId(), event.getSessionId(), event.getDetails());

                String userId = event.getUserId();
                RealmModel realm = session.getContext().getRealm();
                UserModel user = session.users().getUserById(realm, userId);

                logger.info("userId: {} : {} ", user.getId(), user.getUsername());
                // Fetch all active sessions for this user
                List<UserSessionModel> userSessions = session.sessions().getUserSessionsStream(realm, user).toList();

                logger.info("Client login User logged in: " + userId + " | Invalidating previous sessions...");

                // Loop through all sessions and invalidate them, except the latest one
                for (UserSessionModel userSession : userSessions) {
                    logger.info("Client Session  id: {} ,   sessionId: {} ", userSession.getId(), event.getSessionId());
                    if (!userSession.getId().equals(event.getSessionId())) {
                        logger.info("Revoking old session for user: " + userId + " | Session ID: " + userSession.getId());
                        session.sessions().removeUserSession(realm, userSession);
                        handleExpiredSession(session, realm, userSession, "concurrent_login");
                    }
                }
            } else {
                logger.info("Other client: {}", event.getClientId());
            }
        }
//        else if (event.getType() == EventType.REFRESH_TOKEN_ERROR) {
//
//
//            // If user try to get new token and fails then expires that session
//            if (TARGET_REACT_CLIENT_ID.equals(event.getClientId())) {
//
//                logger.info("Refresh Data: id: {} , userId:{}  , getSessionId: {}", event.getId(),
//                        event.getUserId(), event.getSessionId());
//
//                String userId = event.getUserId();
//                RealmModel realm = session.getContext().getRealm();
//                UserModel user = session.users().getUserById(realm, userId);
//
//                // Fetch all active sessions for this user
//                List<UserSessionModel> userSessions = session.sessions().getUserSessionsStream(realm, user).toList();
//
//                logger.info("refresh User logged in: " + userId + " | Invalidating previous sessions...");
//
//                // Loop through all sessions and invalidate them, except the latest one
//                for (UserSessionModel userSession : userSessions) {
//                    logger.info("Refresh Session  id: {} ,   sessionId: {} ", userSession.getId(),
//                            event.getSessionId());
//                    if (userSession.getId().equals(event.getSessionId())) {
//                        logger.info("Revoking session with failed refresh  user: " + userId + " | Session ID: " + userSession.getId());
//                        session.sessions().removeUserSession(realm, userSession);
//                        handleExpiredSession(session, realm, userSession, "token_expired");
//                    }
//                }
//            }
//        }

    }

    private static boolean isConfigured = false; // Ensure we only configure once


    private void configureGoogleLoginIfNeeded() {
        if (!isConfigured) {


            RealmModel realm = session.realms().getRealmByName("master"); // Change this to your realm name

            if (realm != null) {
                logger.info("🚀 Configuring Google login and onboarding flow...");

                configureGmailFlow(realm);

                isConfigured = true;
                logger.info("✅ Google login and onboarding flow configured successfully.");
            } else {
                logger.warn("⚠️ Realm not found. Google login configuration skipped.");
            }
        }
    }

    private void configureGmailFlow(RealmModel realm) {
        String flowAlias = "Gmail First Broker Login"; // Custom flow name

        // Check if the flow already exists
        AuthenticationFlowModel existingFlow = realm.getFlowByAlias(flowAlias);
        if (existingFlow != null) {
            logger.info("✅ Gmail First Broker Login flow already exists.");
            return;
        }

        logger.info("🔧 Creating Gmail First Broker Login flow with Custom User Check...");

        // ✅ Step 1: Create the Authentication Flow (Save first)
        AuthenticationFlowModel flow = new AuthenticationFlowModel();
        flow.setAlias(flowAlias);
        flow.setProviderId("basic-flow");
        flow.setBuiltIn(false);
        flow.setTopLevel(true);
        flow.setDescription("Google login with custom onboarding and user check");

        realm.addAuthenticationFlow(flow); // 🔥 Persist the flow in Keycloak first
        flow = realm.getFlowByAlias(flowAlias); // ✅ Retrieve saved flow

        if (flow == null) {
            logger.error("❌ Failed to retrieve created authentication flow.");
            return;
        }


        // ✅ Step 2: Custom User Check (Ensures user exists before onboarding)
        AuthenticationExecutionModel userCheckExec = new AuthenticationExecutionModel();
        userCheckExec.setAuthenticator("custom-user-check-authenticator");
        userCheckExec.setRequirement(AuthenticationExecutionModel.Requirement.REQUIRED);
        userCheckExec.setParentFlow(flow.getId());
        userCheckExec.setPriority(10);
        realm.addAuthenticatorExecution(userCheckExec);

        // ✅ Step 3: Custom Onboarding Form (Only runs if user is created)
        AuthenticationExecutionModel onboardingFormExec = new AuthenticationExecutionModel();
        onboardingFormExec.setAuthenticator("custom-onboarding-authenticator");
        onboardingFormExec.setRequirement(AuthenticationExecutionModel.Requirement.REQUIRED);
        onboardingFormExec.setParentFlow(flow.getId());
        onboardingFormExec.setPriority(20);
        realm.addAuthenticatorExecution(onboardingFormExec);


        // ✅ Ensure the flow is assigned to the Google Identity Provider
        AuthenticationFlowModel finalFlow = flow;
        realm.getIdentityProvidersStream().forEach(idp -> {
            if (idp.getAlias().equalsIgnoreCase(GOOGLE)) {
                idp.setFirstBrokerLoginFlowId(finalFlow.getId());
                idp.getConfig().put("disableUserInfo", "true");
                realm.updateIdentityProvider(idp);
                logger.info("✅ Google Identity Provider updated with custom flow.");
            }
        });

        // ✅ Persist realm changes
        realm.setAttribute("updated", String.valueOf(System.currentTimeMillis()));
        logger.info("✅ Gmail First Broker Login flow successfully configured and updated in Keycloak UI.");
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        // Implement AdminEvent logic if needed
    }

    @Override
    public void close() {
        logger.debug("Closing Kafka producer");
//        if (stringProducer != null) {
//            stringProducer.close();
//            stringProducer = null; // Ensure producer is nullified to avoid reuse
//        }
    }

    private boolean isStringProducerClosed() {
        try {
            stringProducer.partitionsFor(onBoardingTopic); // Check if producer can fetch metadata
            return false;
        } catch (Exception e) {
            logger.warn("Kafka producer is closed or unavailable: {}", e.getMessage());
            return true;
        }
    }
}
