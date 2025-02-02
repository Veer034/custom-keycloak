package com.convonest.keycloak.serialization;

import com.convonest.keycloak.model.LoggedOutUserRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class LoggedOutUserRecordSerializer implements Serializer<LoggedOutUserRecord> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, LoggedOutUserRecord data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing LoggedOutUserRecord", e);
        }
    }

    @Override
    public void close() {
    }
}
