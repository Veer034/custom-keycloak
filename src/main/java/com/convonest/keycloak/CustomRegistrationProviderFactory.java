package com.convonest.keycloak;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Security;

public class CustomRegistrationProviderFactory implements RealmResourceProviderFactory {

    public static final String ID = "custom-registration";


    private static final Logger logger = LoggerFactory.getLogger(CustomRegistrationProviderFactory.class);

    static {
        try {
            // Get current constraints
            String currentCertPathConstraints = Security.getProperty("jdk.certpath.disabledAlgorithms");
            String currentTlsConstraints = Security.getProperty("jdk.tls.disabledAlgorithms");

            System.out.println("Original jdk.certpath.disabledAlgorithms: " + currentCertPathConstraints);
            System.out.println("Original jdk.tls.disabledAlgorithms: " + currentTlsConstraints);

            // Remove SHA1withRSA from disabled algorithms lists
            String updatedCertPathConstraints = removeConstraint(currentCertPathConstraints, "SHA1");
            String updatedTlsConstraints = removeConstraint(currentTlsConstraints, "SHA1");

            // Set the modified constraints
            Security.setProperty("jdk.certpath.disabledAlgorithms", updatedCertPathConstraints);
            Security.setProperty("jdk.tls.disabledAlgorithms", updatedTlsConstraints);

            // Also set via system properties
            System.setProperty("jdk.certpath.disabledAlgorithms", updatedCertPathConstraints);
            System.setProperty("jdk.tls.disabledAlgorithms", updatedTlsConstraints);


            logger.info("Updated jdk.certpath.disabledAlgorithms:{} ", Security.getProperty("jdk.certpath" +
                    ".disabledAlgorithms"));
            logger.info("Updated jdk.tls.disabledAlgorithms: {}", Security.getProperty("jdk.tls.disabledAlgorithms"));

            logger.info("SecurityOverrideProvider: Selectively enabled SHA1withRSA while keeping other constraints");
        } catch (Exception e) {
            logger.error("SecurityOverrideProvider: Failed to modify algorithm constraints: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        logger.info("🚀 Creating CustomRegistrationProvider instance...");
        return new CustomRegistrationProvider(session);
    }


    @Override
    public void init(org.keycloak.Config.Scope config) {
        logger.info("🔧 CustomRegistrationProviderFactory initialized.");
        initRemoveSHA1withRSA();
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        logger.info("✅ CustomRegistrationProviderFactory postInit completed.");
    }

    @Override
    public void close() {
        logger.info("🛑 Closing CustomRegistrationProviderFactory.");
    }

    @Override
    public String getId() {
        return ID;
    }


    /**
     * NOTE:: This is written in this class instead of another provider. in future another provider required.
     */
    private void initRemoveSHA1withRSA() {
        try {
            // Get current constraints again in case they've been reset
            String currentCertPathConstraints = Security.getProperty("jdk.certpath.disabledAlgorithms");
            String currentTlsConstraints = Security.getProperty("jdk.tls.disabledAlgorithms");

            // Remove SHA1withRSA again
            String updatedCertPathConstraints = removeConstraint(currentCertPathConstraints, "SHA1");
            String updatedTlsConstraints = removeConstraint(currentTlsConstraints, "SHA1");

            // Set the modified constraints again
            Security.setProperty("jdk.certpath.disabledAlgorithms", updatedCertPathConstraints);
            Security.setProperty("jdk.tls.disabledAlgorithms", updatedTlsConstraints);

            logger.info("SecurityOverrideProvider init: Selectively enabled SHA1withRSA");
        } catch (Exception e) {
            logger.error("SecurityOverrideProvider init: Failed to modify algorithm constraints: {} ", e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Helper method to remove a specific constraint from the constraint string
     */
    private static String removeConstraint(String constraints, String constraintToRemove) {
        if (constraints == null || constraints.isEmpty()) {
            return "";
        }

        // Split by commas
        String[] parts = constraints.split(",");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            String trimmed = part.trim();
            // Skip the constraint we want to remove
            if (!trimmed.contains(constraintToRemove)) {
                if (result.length() > 0) {
                    result.append(", ");
                }
                result.append(trimmed);
            }
        }

        return result.toString();
    }

}
