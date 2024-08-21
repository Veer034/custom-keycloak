
# Use below command to build a jar with dependencies


FROM quay.io/keycloak/keycloak:22.0.4

COPY build/libs/custom-keycloak-1.0.0-all.jar /opt/keycloak/providers/
COPY src/main/resources/theme/test /opt/keycloak/themes/test
# Can use to change config like debug and DB endpoint.
COPY src/main/resources/keycloak.conf /opt/keycloak/conf/keycloak.conf