package com.convonest.keycloak.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom Keycloak Event Listener for Prometheus Metrics
 * Tracks login attempts, failures, and user sessions
 * <p>
 * NO external dependencies, fully controlled by your team
 */
public class MetricsEventListener implements EventListenerProvider {

    private final MeterRegistry registry;

    public MetricsEventListener() {
        this.registry = Metrics.globalRegistry;
    }

    @Override
    public void onEvent(Event event) {
        String realm = event.getRealmId();
        EventType type = event.getType();

        switch (type) {
            case LOGIN:
                incrementCounter("keycloak_logins_total",
                        Tags.of("realm", realm, "client", event.getClientId()));
                break;

            case LOGIN_ERROR:
                incrementCounter("keycloak_failed_login_attempts",
                        Tags.of("realm", realm, "error", event.getError()));
                break;

            case LOGOUT:
                incrementCounter("keycloak_logouts_total",
                        Tags.of("realm", realm));
                break;

            case REGISTER:
                incrementCounter("keycloak_user_registrations",
                        Tags.of("realm", realm));
                break;

            case REFRESH_TOKEN:
                incrementCounter("keycloak_token_refreshes",
                        Tags.of("realm", realm));
                break;

            case CODE_TO_TOKEN:
                incrementCounter("keycloak_code_to_token",
                        Tags.of("realm", realm));
                break;

            default:
                // Track all other events
                incrementCounter("keycloak_events_total",
                        Tags.of("realm", realm, "type", type.name()));
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        incrementCounter("keycloak_admin_events_total",
                Tags.of(
                        "realm", adminEvent.getRealmId(),
                        "operation", adminEvent.getOperationType().name(),
                        "resource", adminEvent.getResourceType().name()
                ));
    }

    @Override
    public void close() {
        // Nothing to clean up
    }

    private final ConcurrentHashMap<String, Counter> counters = new ConcurrentHashMap<>();

    private void incrementCounter(String name, Tags tags) {
        String key = name + tags.toString();
        Counter counter = counters.computeIfAbsent(key, k ->
                Counter.builder(name)
                        .tags(tags)
                        .description("Keycloak metric: " + name)
                        .register(registry)
        );
        counter.increment();
    }
}