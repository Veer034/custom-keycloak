package com.convonest.keycloak;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomRegistrationProviderFactory implements RealmResourceProviderFactory {

    public static final String ID = "custom-registration";


    private static final Logger logger = LoggerFactory.getLogger(CustomRegistrationProviderFactory.class);

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        logger.info("🚀 Creating CustomRegistrationProvider instance...");
        return new CustomRegistrationProvider(session);
    }


    @Override
    public void init(org.keycloak.Config.Scope config) {
        logger.info("🔧 CustomRegistrationProviderFactory initialized.");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        logger.info("✅ CustomRegistrationProviderFactory postInit completed.");
    }

    @Override
    public void close() {
        logger.info("🛑 Closing CustomRegistrationProviderFactory.");
    }

    @Override
    public String getId() {
        return ID;
    }
}
