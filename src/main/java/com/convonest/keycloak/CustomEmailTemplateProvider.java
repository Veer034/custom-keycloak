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

import static com.convonest.keycloak.Constants.COMPANY_NAME;
import static com.convonest.keycloak.Constants.IS_ACTIVE;

public class CustomEmailTemplateProvider extends FreeMarkerEmailTemplateProvider {
    private static final Logger logger = LoggerFactory.getLogger(CustomEmailTemplateProvider.class);

    public CustomEmailTemplateProvider(KeycloakSession session) {
        super(session);
    }

    @Override
    public void sendVerifyEmail(String link, long expirationInMinutes) throws EmailException {
        logger.info("📧 Sending verification email. Expiration time: {} minutes", expirationInMinutes);

        String userName = user.getFirstName() != null ? user.getFirstName() : user.getEmail();
        String customSubject = "Welcome to Convonest Analytics Platform! Verify Your Email";

        // Format expiration time intelligently
        String formattedExpiration;
        if (expirationInMinutes >= 60) {
            long hours = expirationInMinutes / 60;
            formattedExpiration = hours + (hours > 1 ? " hours" : " hour");
        } else {
            formattedExpiration = expirationInMinutes + (expirationInMinutes > 1 ? " minutes" : " minute");
        }

        Map<String, Object> attributes = new HashMap<>(this.attributes);
        attributes.put("userName", userName);
        attributes.put("email", user.getEmail());
        attributes.put("linkExpiration", expirationInMinutes);
        attributes.put("linkExpirationFormatted", formattedExpiration);
        attributes.put("link", link);

        send(customSubject, "email-verification.ftl", attributes);
    }

    @Override
    public void sendPasswordReset(String link, long expirationInMinutes) throws EmailException {
        logger.info("📧 Sending password reset email. Link expiration: {} minutes", expirationInMinutes);

        boolean isActive = Boolean.parseBoolean(user.getFirstAttribute(IS_ACTIVE));

        if (!isActive) {
            logger.warn("🚫 User {} is inactive. Password reset email not sent.", user.getEmail());
            throw new EmailException("emailInactiveUser");
        }

        String userName = user.getFirstName() != null ? user.getFirstName() : user.getEmail();
        String companyName = user.getFirstAttribute(COMPANY_NAME);


        String customSubject = "Reset Your Password - " + companyName;

        // Format expiration time intelligently
        String formattedExpiration;
        if (expirationInMinutes >= 60) {
            long hours = expirationInMinutes / 60;
            formattedExpiration = hours + (hours > 1 ? " hours" : " hour");
        } else {
            formattedExpiration = expirationInMinutes + (expirationInMinutes > 1 ? " minutes" : " minute");
        }

        Map<String, Object> attributes = new HashMap<>(this.attributes);
        attributes.put("userName", userName);
        attributes.put("email", user.getEmail());
        attributes.put("linkExpiration", expirationInMinutes);
        attributes.put("linkExpirationFormatted", formattedExpiration);
        attributes.put("link", link);
        attributes.put("realmName", realm.getDisplayName() != null ? realm.getDisplayName() : realm.getName());

        // Create the email template
        EmailTemplate email = processTemplate("passwordResetSubject", Collections.emptyList(), "password-reset.ftl", attributes);


        send(customSubject, email.getTextBody(), email.getHtmlBody(), null);

    }

    @Override
    public void send(String subjectFormatKey, List<Object> subjectAttributes, String bodyTemplate, Map<String, Object> bodyAttributes) throws EmailException {
        logger.info("📤 Sending email with template: {}", bodyTemplate);

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
            logger.error("❌ Failed to send email", e);
            throw e;
        } catch (Exception e) {
            logger.error("❌ Failed to template email", e);
            throw new EmailException("Failed to template email", e);
        }
    }

}