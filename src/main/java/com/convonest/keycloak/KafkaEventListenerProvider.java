package com.convonest.keycloak;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class KafkaEventListenerProvider implements EventListenerProvider {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventListenerProvider.class);

    private final int maxRetries = 5; // Maximum number of retries
    private final long retryDelay = 2000; // Delay between retries in milliseconds

    private KafkaProducer<String, String> producer;
    private final String topic;

    public KafkaEventListenerProvider() {
        // Get the singleton instance from KafkaManager
        this.producer = KafkaManager.getProducer();
        this.topic = KafkaManager.getTopic();
        logger.info("KafkaEventListenerProvider initialized with topic: {}", this.topic);
    }

    @Override
    public void onEvent(Event event) {
        logger.info("Received event of type: {}", event.getType());

        if (event.getType() == EventType.REGISTER) {
            String emailId = event.getDetails().get("email");

            logger.info("Publishing message to Kafka topic {}: {}", topic, emailId);

            boolean success = false;
            int attempt = 0;

            while (!success && attempt < maxRetries) {
                try {
                    // Ensure the producer is valid
                    if (producer == null || isProducerClosed()) {
                        logger.warn("Kafka producer is unavailable. Reinitializing...");
                        producer = KafkaManager.getProducer();
                    }

                    // Asynchronously send the message
                    producer.send(new ProducerRecord<>(topic, emailId), (metadata, exception) -> {
                        if (exception != null) {
                            logger.error("Error sending message to Kafka topic {}: {}", topic, exception.getMessage());
                        } else {
                            logger.info("Message sent to Kafka topic {} with offset {}", metadata.topic(),
                                    metadata.offset());
                        }
                    });

                    success = true; // If no exception, set success to true

                } catch (Exception e) {
                    attempt++;
                    logger.error("Attempt {}/{}: Failed to send message to Kafka. Retrying in {} ms...", attempt,
                            maxRetries, retryDelay, e);
                    try {
                        TimeUnit.MILLISECONDS.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        logger.error("Retry interrupted", ie);
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (!success) {
                logger.error("CRITICAL ERROR: Failed to send message to Kafka after {} attempts, EmailId: {}",
                        maxRetries, emailId);
            }
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        // Implement AdminEvent logic if needed
    }

    @Override
    public void close() {
        logger.debug("Closing Kafka producer");
        if (producer != null) {
            producer.close();
            producer = null; // Ensure producer is nullified to avoid reuse
        }
    }

    private boolean isProducerClosed() {
        try {
            producer.partitionsFor(topic); // Check if producer can fetch metadata
            return false;
        } catch (Exception e) {
            logger.warn("Kafka producer is closed or unavailable: {}", e.getMessage());
            return true;
        }
    }
}
