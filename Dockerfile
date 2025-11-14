FROM quay.io/keycloak/keycloak:22.0.4

COPY build/libs/custom-keycloak-1.0.0-all.jar /opt/keycloak/providers/
COPY src/main/resources/theme/custom /opt/keycloak/themes/custom
# Can use to change config like debug and DB endpoint.
COPY src/main/resources/keycloak.conf /opt/keycloak/conf/keycloak.conf

# Add this line to build Keycloak with MySQL support
RUN /opt/keycloak/bin/kc.sh build --db=mysql

# Enable metrics and health endpoints
ENV KC_METRICS_ENABLED=true
ENV KC_HEALTH_ENABLED=true


ENV TZ=UTC
ENV KC_LOG_CONSOLE_FORMAT="%d{yyyy-MM-dd HH:mm:ss} [%t] %-5p %c{2.} - %s%e%n"


ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start", "--hostname-strict=false", "-Dkeycloak.profile.feature.upload_scripts=enabled"]
