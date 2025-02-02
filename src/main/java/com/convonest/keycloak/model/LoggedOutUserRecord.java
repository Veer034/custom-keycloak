package com.convonest.keycloak.model;

public record LoggedOutUserRecord(String tenantId, String agentId, String agentSessionId, long eventTime,
                                  String logOutType) {
}
