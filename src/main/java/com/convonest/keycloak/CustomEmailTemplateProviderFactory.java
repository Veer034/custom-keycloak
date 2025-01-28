package com.convonest.keycloak;

import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.email.freemarker.FreeMarkerEmailTemplateProviderFactory;
import org.keycloak.models.KeycloakSession;

public class CustomEmailTemplateProviderFactory extends FreeMarkerEmailTemplateProviderFactory {

    public static final String ID = "custom-email-template-provider";

    @Override
    public EmailTemplateProvider create(KeycloakSession session) {
        return new CustomEmailTemplateProvider(session);
    }

    @Override
    public String getId() {
        return ID;
    }
}
