package com.convonest.keycloak;

import com.convonest.keycloak.model.LoggedOutUserRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
        logger.info("KafkaEventListenerProvider initialized with on boarding topic: {} , {}", this.onBoardingTopic, this.loggedOutTopic);
    }

    @Override
    public void onEvent(Event event) {
        logger.info("Received event of type: {}, clientId: {} ,", event.getType(), event.getClientId());

        if (event.getType() == EventType.REGISTER) {
            String emailId = event.getDetails().get("email");

            logger.info("Publishing message to Kafka topic {}: {}", onBoardingTopic, emailId);

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
                            logger.warn("Error sending message to Kafka topic {}: {}", onBoardingTopic,
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
                        logger.error("Retry interrupted", ie);
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
                logger.info("Over client: {}", event.getClientId());
            }
        } else if (event.getType() == EventType.REFRESH_TOKEN_ERROR) {


            // If user try to get new token and fails then expires that session
            if (TARGET_REACT_CLIENT_ID.equals(event.getClientId())) {

                logger.info("Refresh Data: id: {} , userId:{}  , getSessionId: ", event.getId(),
                        event.getUserId(), event.getSessionId());

                String userId = event.getUserId();
                RealmModel realm = session.getContext().getRealm();
                UserModel user = session.users().getUserById(realm, userId);

                // Fetch all active sessions for this user
                List<UserSessionModel> userSessions = session.sessions().getUserSessionsStream(realm, user).toList();

                logger.info("refresh User logged in: " + userId + " | Invalidating previous sessions...");

                // Loop through all sessions and invalidate them, except the latest one
                for (UserSessionModel userSession : userSessions) {
                    logger.info("Refresh Session  id: {} ,   sessionId: {} ", userSession.getId(),
                            event.getSessionId());
                    if (userSession.getId().equals(event.getSessionId())) {
                        logger.info("Revoking session with failed refresh  user: " + userId + " | Session ID: " + userSession.getId());
                        session.sessions().removeUserSession(realm, userSession);
                        handleExpiredSession(session, realm, userSession, "token_expired");
                    }
                }
            }
        }

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
