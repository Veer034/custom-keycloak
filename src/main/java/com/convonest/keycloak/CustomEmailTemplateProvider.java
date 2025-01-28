package com.convonest.keycloak;


import org.keycloak.email.EmailException;
import org.keycloak.email.freemarker.FreeMarkerEmailTemplateProvider;
import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CustomEmailTemplateProvider extends FreeMarkerEmailTemplateProvider {
    private static final Logger logger = LoggerFactory.getLogger(CustomEmailTemplateProvider.class);

    public CustomEmailTemplateProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    public void sendVerifyEmail(String link, long expirationInMinutes) throws EmailException {
        logger.info("Received expiration time: {} minutes ({} days)",
                expirationInMinutes, expirationInMinutes / 1440);

        String userName = user.getFirstName() != null ? user.getFirstName() : user.getEmail();

        // Custom dynamic subject
        String customSubject = "Welcome to Convonest Analytics Platform! Verify Your Email";


        // Attributes for the email template
        Map<String, Object> attributes = new HashMap<>(this.attributes);
        attributes.put("userName", userName);
        attributes.put("email", user.getEmail());
        addLinkInfoIntoAttributes(link, expirationInMinutes, attributes);

        // Send the email
        send(customSubject, "email-verification.ftl", attributes);
    }
}
