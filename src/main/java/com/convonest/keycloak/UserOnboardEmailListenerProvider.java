package com.convonest.keycloak;

import org.keycloak.email.EmailException;
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class UserOnboardEmailListenerProvider implements EventListenerProvider {

    private static final Logger logger = LoggerFactory.getLogger(UserOnboardEmailListenerProvider.class);
    private static String notificationEmail;

    static {
        notificationEmail = getNotificationEmail();
    }


    private static String getNotificationEmail() {
        Properties props = new Properties();

        notificationEmail = System.getenv("NOTIFICATION_EMAIL");

        if (notificationEmail == null || notificationEmail.isEmpty()) {
            logger.warn("Environment variable NOTIFICATION_EMAIL is missing. Falling back to properties file.");
        }

        try (InputStream input = UserOnboardEmailListenerProvider.class.getClassLoader().getResourceAsStream("keycloak.properties")) {
            if (input != null) {
                props.load(input);
                logger.info("✅ Kafka properties loaded from keycloak.properties.");
            } else {
                logger.warn("❌ keycloak.properties not found. Using only environment variables.");
            }
        } catch (IOException ex) {
            logger.error("❌ Failed to load Kafka properties from keycloak.properties", ex);
        }


        return StringUtil.isNotBlank(notificationEmail) ? notificationEmail : props.getProperty(
                "notification.email", "");
    }

    private KeycloakSession session;

    public UserOnboardEmailListenerProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        logger.info("Received event of type: {}, clientId: {}", event.getType(), event.getClientId());
        if (EventType.REGISTER.equals(event.getType())) {
            handleUserCreation(event.getRealmId(), event.getUserId());
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {

        logger.info("Received admin event of getOperationType: {},  getResourceType: {}",
                adminEvent.getOperationType(),
                adminEvent.getResourceType());
        if (ResourceType.USER.equals(adminEvent.getResourceType()) &&
                OperationType.CREATE.equals(adminEvent.getOperationType())) {
            String userId = extractUserIdFromPath(adminEvent.getResourcePath());
            handleUserCreation(adminEvent.getRealmId(), userId);
        }
    }

    private void handleUserCreation(String realmId, String userId) {
        try {
            RealmModel realm = session.realms().getRealm(realmId);
            UserModel user = session.users().getUserById(realm, userId);

            if (user != null) {
                sendNotificationEmail(realm, user);
            } else {
                logger.warn("UserModel not present for realmId: {}, userId :{} ", realmId, userId);
            }
        } catch (Exception e) {
            logger.error("Error handling user creation", e);
        }
    }

    private void sendNotificationEmail(RealmModel realm, UserModel user) {
        try {
            EmailTemplateProvider emailProvider = session.getProvider(EmailTemplateProvider.class);
            emailProvider.setRealm(realm);
            emailProvider.setUser(user);

            // NOTE:: Force use of custom theme, it should match with resources/theme/{custom}/email
            realm.setEmailTheme("custom");

            String subject = "New User OnBoarding - " + user.getUsername();

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("username", user.getUsername());
            attributes.put("firstName", user.getFirstName() != null ? user.getFirstName() : "N/A");
            attributes.put("lastName", user.getLastName() != null ? user.getLastName() : "N/A");
            attributes.put("email", user.getEmail() != null ? user.getEmail() : "N/A");
            attributes.put(Constants.COMPANY_NAME, user.getFirstAttribute(Constants.COMPANY_NAME) != null ? user.getFirstAttribute(Constants.COMPANY_NAME) : "N/A");
            notificationEmail = StringUtil.isNotBlank(notificationEmail) ? notificationEmail :
                    getNotificationEmail();

            emailProvider.send(subject, Collections.singletonList(notificationEmail), "user-onboard.ftl", attributes);

            logger.info("Notification email sent for new user: {} , to notificationEmailId :{} ", user.getUsername(),
                    notificationEmail);
        } catch (EmailException e) {
            logger.error("Failed to send notification email", e);
        }
    }

    private String extractUserIdFromPath(String resourcePath) {
        String[] parts = resourcePath.split("/");
        return parts[parts.length - 1];
    }

    @Override
    public void close() {
    }
}