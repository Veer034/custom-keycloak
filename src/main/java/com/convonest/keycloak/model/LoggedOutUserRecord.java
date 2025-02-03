package com.convonest.keycloak.model;

//NOTE:: Must be same as the tenant service, Modify both places
public record LoggedOutUserRecord(String tenantId, String agentId, String agentSessionId, long eventTime,
                                  String logOutType) {
}
