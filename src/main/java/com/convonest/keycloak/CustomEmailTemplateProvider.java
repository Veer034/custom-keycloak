package com.convonest.keycloak;

import org.keycloak.email.EmailException;
import org.keycloak.email.freemarker.FreeMarkerEmailTemplateProvider;
import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    @Override
    public void send(String subjectFormatKey, List<Object> subjectAttributes, String bodyTemplate, Map<String, Object> bodyAttributes) throws EmailException {
        // Extract recipient email from subjectAttributes if present
        String recipientEmail = null;
        if (subjectAttributes != null && !subjectAttributes.isEmpty() && subjectAttributes.get(0) instanceof String) {
            recipientEmail = (String) subjectAttributes.get(0);
            logger.info("Using custom recipient email: {}", recipientEmail);
        }

        try {
            EmailTemplate email = processTemplate(subjectFormatKey, Collections.emptyList(), bodyTemplate, bodyAttributes);
            if (recipientEmail != null) {
                send(email.getSubject(), email.getTextBody(), email.getHtmlBody(), recipientEmail);
            } else {
                send(email.getSubject(), email.getTextBody(), email.getHtmlBody(), null);
            }
        } catch (EmailException e) {
            throw e;
        } catch (Exception e) {
            throw new EmailException("Failed to template email", e);
        }
    }
}