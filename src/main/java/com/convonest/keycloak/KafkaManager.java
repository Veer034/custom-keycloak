package com.convonest.keycloak;

import com.convonest.keycloak.model.LoggedOutUserRecord;
import com.convonest.keycloak.serialization.LoggedOutUserRecordSerializer;
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
    private static String onBoardingTopic;
    private static String loggedOutTopic;

    static {
        Properties props = initializeProperties();
        onBoardingTopic = props.getProperty("kafka.topic.on-boarding", "default-on-boarding-topic");
        loggedOutTopic = props.getProperty("kafka.topic.logged-out", "default-logged-out-topic");
    }

    public static KafkaProducer<String, String> createStringProducer() {
        Properties props = initializeProperties();
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public static KafkaProducer<String, LoggedOutUserRecord> createModelProducer() {
        Properties props = initializeProperties();
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LoggedOutUserRecordSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public static String getOnBoardingTopic() {
        return onBoardingTopic;
    }

    public static String getLoggedOutTopic() {
        return loggedOutTopic;
    }

    private static Properties initializeProperties() {
        Properties props = new Properties();
        String bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        String onBoardingTopicEnv = System.getenv("KAFKA_TOPIC_ON_BOARDING");
        String loggedOutTopicEnv = System.getenv("KAFKA_TOPIC_LOGGED_OUT");

        if (bootstrapServers == null || bootstrapServers.isEmpty()) {
            logger.warn("Environment variable KAFKA_BOOTSTRAP_SERVERS is missing. Falling back to properties file.");
        }

        try (InputStream input = KafkaManager.class.getClassLoader().getResourceAsStream("keycloak.properties")) {
            if (input != null) {
                props.load(input);
                logger.info("✅ Kafka properties loaded from keycloak.properties.");
            } else {
                logger.warn("❌ keycloak.properties not found. Using only environment variables.");
            }
        } catch (IOException ex) {
            logger.error("❌ Failed to load Kafka properties from keycloak.properties", ex);
        }

        props.put("bootstrap.servers", bootstrapServers != null ? bootstrapServers : props.getProperty("kafka.bootstrap.servers", "localhost:9092"));

        // Final fallback to default values
        props.put("bootstrap.servers", bootstrapServers != null ? bootstrapServers : props.getProperty("kafka.bootstrap.servers", "localhost:9092"));
        onBoardingTopic = onBoardingTopicEnv != null ? onBoardingTopicEnv : props.getProperty("kafka.topic.on-boarding", "default-on-boarding-topic");
        loggedOutTopic = loggedOutTopicEnv != null ? loggedOutTopicEnv : props.getProperty("kafka.topic.logged-out", "default-logged-out-topic");

        logger.info("✅ Using Kafka Bootstrap Servers: {}", props.get("bootstrap.servers"));
        logger.info("✅ On-Boarding Topic: {}", onBoardingTopic);
        logger.info("✅ Logged Out Topic: {}", loggedOutTopic);

        // Set required Kafka producer configurations
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.get("bootstrap.servers"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return props;
    }
}
