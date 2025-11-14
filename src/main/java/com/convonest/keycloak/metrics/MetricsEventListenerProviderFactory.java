package com.convonest.keycloak.metrics;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;


public class MetricsEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final String PROVIDER_ID = "custom-metrics-listener";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new MetricsEventListener();
    }

    @Override
    public void init(Config.Scope config) {
        // Initialize if needed
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Post-initialization
    }

    @Override
    public void close() {
        // Cleanup
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}