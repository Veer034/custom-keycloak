package com.convonest.keycloak;


import com.convonest.keycloak.model.LoggedOutUserRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.convonest.keycloak.Constants.MAX_RETRIES;
import static com.convonest.keycloak.Constants.RETRY_DELAY;

public class SessionManagementUtil {
    private static final Logger logger = LoggerFactory.getLogger(SessionManagementUtil.class);

    public static void handleExpiredSession(KeycloakSession session, RealmModel realm, UserSessionModel userSession,
                                            String logOutType) {
        logger.info("Handling expired session for user: {}", userSession.getUser().getUsername());

        UserModel userModel = userSession.getUser();
        Map<String, List<String>> attributes = userModel.getAttributes();

        if (!attributes.containsKey("tenantId") || attributes.get("tenantId").isEmpty()) {
            logger.warn("No tenantId found for user: {}", userModel.getUsername());
            return;
        }

        String tenantId = attributes.get("tenantId").get(0);
        String agentId = userModel.getId();
        String agentSessionId = userSession.getId();

        LoggedOutUserRecord loggedOutUserRecord = new LoggedOutUserRecord(
                tenantId,
                agentId,
                agentSessionId,
                System.currentTimeMillis(),
                logOutType
        );

        sendToKafka(loggedOutUserRecord);

        // Remove expired session
        session.sessions().removeUserSession(realm, userSession);
    }

    private static void sendToKafka(LoggedOutUserRecord record) {
        boolean success = false;
        int attempt = 0;
        String loggedOutTopic = KafkaManager.getLoggedOutTopic();

        try (KafkaProducer<String, LoggedOutUserRecord> recordKafkaProducer = KafkaManager.createModelProducer()) {
            while (!success && attempt < MAX_RETRIES) {
                try {
                    recordKafkaProducer.send(new ProducerRecord<>(loggedOutTopic, record), (metadata, exception) -> {
                        if (exception != null) {
                            logger.warn("❌ Error sending logged out message to Kafka topic {}: {}",
                                    loggedOutTopic, exception.getMessage());
                        } else {
                            logger.debug("✅ Message sent to logged out Kafka topic {} with offset {}",
                                    metadata.topic(), metadata.offset());
                        }
                    });

                    success = true;

                } catch (Exception e) {
                    attempt++;
                    logger.warn("⚠️ Attempt {}/{}: Failed to send logout message to Kafka. Retrying in {} ms...",
                            attempt, MAX_RETRIES, RETRY_DELAY, e);
                    try {
                        TimeUnit.MILLISECONDS.sleep(RETRY_DELAY);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.error("🚨 Retry interrupted", ie);
                        break;
                    }
                }
            }

            if (!success) {
                logger.error("🚨 CRITICAL ERROR: Failed to send logout message to Kafka after {} attempts for record: " +
                                "{}",
                        MAX_RETRIES, record);
            }
        } catch (Exception e) {
            logger.error("❌ Fatal error while creating or closing Kafka producer: {}", e.getMessage(), e);
        }
    }

}
