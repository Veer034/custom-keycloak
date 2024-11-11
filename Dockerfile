
# Use below command to build a jar with dependencies


FROM quay.io/keycloak/keycloak:22.0.4

COPY build/libs/custom-keycloak-1.0.0-all.jar /opt/keycloak/providers/
COPY src/main/resources/theme/custom /opt/keycloak/themes/custom
# Can use to change config like debug and DB endpoint.
COPY src/main/resources/keycloak.conf /opt/keycloak/conf/keycloak.conf

# Copy the log4j2.xml file for custom logging configuration, if needed
COPY src/main/resources/log4j2.xml /opt/keycloak/conf/log4j2.xml