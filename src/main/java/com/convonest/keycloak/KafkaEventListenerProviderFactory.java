package com.convonest.keycloak;

import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaEventListenerProviderFactory implements EventListenerProviderFactory {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventListenerProviderFactory.class);

    public static final String ID = "kafka-event-listener";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        logger.info("🚀 Creating KafkaEventListenerProvider instance for the first time...");
        return new KafkaEventListenerProvider(session);
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {
        logger.info("🔧 KafkaEventListenerProviderFactory initialized.");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        logger.info("✅ KafkaEventListenerProviderFactory postInit completed.");
//
//        factory.register(new ProviderEventListener() {
//            @Override
//            public void onEvent(ProviderEvent event) {
//                try (KeycloakSession session = factory.create()) {
//                    TimerProvider timer = session.getProvider(TimerProvider.class);
//                    if (timer != null) {
//                        ExpiredSessionCheckerTask task = new ExpiredSessionCheckerTask(factory);
//                        timer.schedule(
//                                () -> task.run(),
//                                CRON_EXECUTION_DELAY_MILLIS,
//                                "expired-session-checker"
//                        );
//                        logger.info("✅ Scheduled ExpiredSessionCheckerTask every 10 minutes.");
//                    } else {
//                        logger.error("❌ TimerProvider not found, cannot schedule ExpiredSessionCheckerTask.");
//                    }
//                } catch (Exception e) {
//                    logger.error("❌ Error scheduling ExpiredSessionCheckerTask", e);
//                }
//            }
//        });
    }

    @Override
    public void close() {
        logger.info("🛑 Closing KafkaEventListenerProviderFactory.");
    }

    @Override
    public String getId() {
        return ID;
    }
}
