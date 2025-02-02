package com.convonest.keycloak;

import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.email.freemarker.FreeMarkerEmailTemplateProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomEmailTemplateProviderFactory extends FreeMarkerEmailTemplateProviderFactory {

    public static final String ID = "custom-email-template-provider";
    private static final Logger logger = LoggerFactory.getLogger(CustomEmailTemplateProviderFactory.class);

    @Override
    public EmailTemplateProvider create(KeycloakSession session) {
        logger.info("🚀 Creating CustomEmailTemplateProvider instance...");
        return new CustomEmailTemplateProvider(session);
    }


    @Override
    public String getId() {
        return ID;
    }
}
