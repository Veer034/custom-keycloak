package com.convonest.keycloak;

import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class EmailVerificationEventListenerProviderFactory implements EventListenerProviderFactory {

    public static final String ID = "email-verification-event-listener";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new EmailVerificationEventListenerProvider(session);
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return ID;
    }
}
