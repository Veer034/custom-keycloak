package com.convonest.keycloak;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaEventListenerProvider implements EventListenerProvider {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventListenerProvider.class);

    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final int maxRetries = 5; // Maximum number of retries
    private final long retryDelay = 2000; // Delay between retries in milliseconds

    public KafkaEventListenerProvider() {
        Properties props = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("keycloak.properties")) {
            if (input == null) {
                logger.error("Could not find keycloak.properties");
                throw new RuntimeException("Sorry, unable to find keycloak.properties");
            }

            props.load(input);
            logger.info("Kafka properties loaded successfully");

        } catch (IOException ex) {
            logger.error("Failed to load Kafka properties", ex);
            throw new RuntimeException("Failed to load Kafka properties", ex);
        }

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getProperty("kafka.bootstrap.servers"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
        this.topic = props.getProperty("kafka.topic");

        logger.info("Kafka producer initialized with topic: {}", this.topic);
    }

    @Override
    public void onEvent(Event event) {
        logger.debug("Received event of type: {}", event.getType());

        if (event.getType() == EventType.REGISTER) {
            String emailId = event.getDetails().get("email");

            logger.info("Publishing message to Kafka topic {}: {}", topic, emailId);

            boolean success = false;
            int attempt = 0;

            while (!success && attempt < maxRetries) {
                try {
                    producer.send(new ProducerRecord<>(topic, emailId), (metadata, exception) -> {
                        if (exception != null) {
                            throw new RuntimeException("Failed to send message to Kafka", exception);
                        } else {
                            logger.info("Message sent to Kafka topic {} with offset {}", metadata.topic(),
                                    metadata.offset());
                        }
                    }).get(); // .get() to make the send synchronous and catch exceptions

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
                logger.error("WARNING CRITICAL ERROR : Failed to send message to Kafka after {} attempts, EmailId: {}",
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
        producer.close();
    }
}
