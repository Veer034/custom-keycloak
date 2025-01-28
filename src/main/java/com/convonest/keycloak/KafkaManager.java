package com.convonest.keycloak;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaManager {
    private static final Logger logger = LoggerFactory.getLogger(KafkaManager.class);
    private static KafkaProducer<String, String> producer;
    private static String topic;

    static {
        // Initialize Kafka properties and the topic
        Properties props = initializeProperties();
        topic = props.getProperty("kafka.topic", "default-topic");

        // Initialize the producer
        initializeProducer(props);

        // Register a shutdown hook to clean up the producer
        Runtime.getRuntime().addShutdownHook(new Thread(() -> closeProducer()));
    }

    public static synchronized KafkaProducer<String, String> getProducer() {
        // Check if the producer is closed and reinitialize if necessary
        if (producer == null || isProducerClosed()) {
            logger.warn("Kafka producer is closed or unavailable. Reinitializing...");
            Properties props = initializeProperties();
            initializeProducer(props);
        }
        return producer;
    }

    public static String getTopic() {
        return topic;
    }

    private static synchronized void initializeProducer(Properties props) {
        try {
            producer = new KafkaProducer<>(props);
            logger.info("Kafka producer initialized successfully with topic: {}", topic);
        } catch (Exception e) {
            logger.error("Failed to initialize Kafka producer", e);
            throw new RuntimeException("Failed to initialize Kafka producer", e);
        }
    }

    private static Properties initializeProperties() {
        Properties props = new Properties();
        String bootstrapServers = System.getenv("kafka.bootstrap.servers");
        String kafkaTopic = System.getenv("kafka.topic");

        // Fallback to properties file if environment variables are not set
        if (bootstrapServers == null || bootstrapServers.isEmpty() || kafkaTopic == null || kafkaTopic.isEmpty()) {
            logger.info("Reading local properties as some values are missing: bootstrapServers={}, kafkaTopic={}",
                    bootstrapServers, kafkaTopic);
            try (InputStream input = KafkaManager.class.getClassLoader().getResourceAsStream("keycloak.properties")) {
                if (input != null) {
                    props.load(input);
                    logger.info("Kafka properties loaded successfully from keycloak.properties");
                } else {
                    logger.warn("keycloak.properties not found, defaulting to hardcoded values");
                }
            } catch (IOException ex) {
                logger.error("Failed to load Kafka properties from file", ex);
            }

            bootstrapServers = props.getProperty("kafka.bootstrap.servers", "localhost:9092");
            kafkaTopic = props.getProperty("kafka.topic", "default-topic");
        }

        logger.info("Using Kafka bootstrap servers: {}", bootstrapServers);
        logger.info("Using Kafka topic: {}", kafkaTopic);

        // Set Kafka producer configuration
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return props;
    }

    private static boolean isProducerClosed() {
        try {
            // Check if the producer can fetch metadata for the topic
            producer.partitionsFor(topic);
            return false; // If no exception, producer is open
        } catch (Exception e) {
            logger.warn("Kafka producer is closed or unavailable: {}", e.getMessage());
            return true;
        }
    }

    public static synchronized void closeProducer() {
        if (producer != null) {
            try {
                producer.close();
                logger.info("Kafka producer closed successfully");
            } catch (Exception ex) {
                logger.error("Error while closing Kafka producer", ex);
            } finally {
                producer = null; // Ensure producer is nullified to avoid reuse
            }
        }
    }
}
