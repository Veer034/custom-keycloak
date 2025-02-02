package com.convonest.keycloak;

import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailVerificationEventListenerProviderFactory implements EventListenerProviderFactory {

    public static final String ID = "email-verification-event-listener";
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventListenerProviderFactory.class);

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        logger.info("🚀 Creating EmailVerificationEventListenerProvider instance...");
        return new EmailVerificationEventListenerProvider(session);
    }


    @Override
    public void init(org.keycloak.Config.Scope config) {
        logger.info("🔧 EmailVerificationEventListenerProviderFactory initialized.");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        logger.info("✅ EmailVerificationEventListenerProviderFactory postInit completed.");
    }

    @Override
    public void close() {
        logger.info("🛑 Closing EmailVerificationEventListenerProviderFactory.");
    }

    @Override
    public String getId() {
        return ID;
    }
}
