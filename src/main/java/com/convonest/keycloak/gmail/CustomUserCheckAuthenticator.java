package com.convonest.keycloak.gmail;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.Config;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;
import org.keycloak.provider.ProviderConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.convonest.keycloak.Constants.GOOGLE;

public class CustomUserCheckAuthenticator implements Authenticator, AuthenticatorFactory {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserCheckAuthenticator.class);

    // DTOs for Google OAuth2 Data Structure
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GoogleUserInfo {
        public String sub;           // Google's unique user identifier
        public String email;         // User's email address
        public Boolean email_verified; // Email verification status
        public String name;          // Full name
        public String given_name;    // First name
        public String family_name;   // Last name
        public String picture;       // Profile picture URL
        public String locale;        // User's locale/language
        public String hd;            // Hosted domain (for G Suite users)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class KeycloakUserAttributes {
        public List<String> email;
        public List<String> firstName;
        public List<String> lastName;
        public List<String> picture;
        public List<String> locale;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BrokerContext {
        public String id;                    // Google user ID
        public String brokerUsername;        // Usually the email
        public String brokerUserId;         // Format: google.[Google ID]
        public String identityProviderId;    // Should be "google"
        public Map<String, AttributeData> contextData;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class AttributeData {
        public String clazz;
        public String data;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        try {
            UserModel authenticatedUser = context.getAuthenticationSession().getAuthenticatedUser();
            logger.info("Starting authentication process...");

            String brokerContextJson = context.getAuthenticationSession().getAuthNote("BROKERED_CONTEXT");
            BrokerContext brokerContext = null;

            if (brokerContextJson != null) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    brokerContext = mapper.readValue(brokerContextJson, BrokerContext.class);
                    logger.info("Successfully parsed broker context for user: {}", brokerContext.brokerUsername);
                } catch (Exception e) {
                    logger.warn("Failed to parse broker context: {}", e.getMessage());
                }
            }

            String email = null;
            if (authenticatedUser != null) {
                email = authenticatedUser.getEmail();
                logger.info("Found email from authenticated user: {}", email);
            } else if (brokerContext != null) {
                email = brokerContext.brokerUsername;
                logger.info("Found email from broker context: {}", email);
            }

            if (email == null || email.isEmpty()) {
                logger.error("No email found in session or broker context");
                context.failure(AuthenticationFlowError.INVALID_USER);
                return;
            }

            // Process the user
            processUserAndComplete(context, email, brokerContext);

        } catch (Exception e) {
            logger.error("Unexpected error during authentication: {}", e.getMessage(), e);
            context.failure(AuthenticationFlowError.INTERNAL_ERROR);
        }
    }

    private void processUserAndComplete(AuthenticationFlowContext context, String email, BrokerContext brokerContext) {
        RealmModel realm = context.getRealm();
        UserProvider userProvider = context.getSession().users();
        UserModel user = userProvider.getUserByEmail(realm, email);

        try {
            if (user == null) {
                user = createNewUser(userProvider, realm, email);
                logger.info("Created new user: {}", email);
            } else {
                logger.info("Found existing user: {}", email);
            }

            // Update user attributes
            updateUserAttributes(user, brokerContext);

            // Ensure user has first and last name
            ensureUserNames(user, email);

            // Set the user in the authentication session
            context.getAuthenticationSession().setAuthenticatedUser(user);

            // Complete the authentication
            context.success();
            logger.info("Successfully completed authentication for user: {}", email);

        } catch (Exception e) {
            logger.error("Error in processUserAndComplete: {}", e.getMessage(), e);
            // Even if there's an error, try to complete with basic user info
            if (user != null) {
                ensureUserNames(user, email);
                context.getAuthenticationSession().setAuthenticatedUser(user);
                context.success();
                logger.info("Completed authentication with basic user info for: {}", email);
            } else {
                context.failure(AuthenticationFlowError.INTERNAL_ERROR);
            }
        }
    }

    private UserModel createNewUser(UserProvider userProvider, RealmModel realm, String email) {
        UserModel newUser = userProvider.addUser(realm, email);
        newUser.setEnabled(true);
        newUser.setEmail(email);
        newUser.setEmailVerified(true);
        newUser.setUsername(email);
        return newUser;
    }

    private void ensureUserNames(UserModel user, String email) {
        // Extract name from email (part before @)
        String emailPrefix = email.split("@")[0];

        // Always set firstName to email prefix if it's missing
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            user.setFirstName(emailPrefix);
            logger.info("Set firstName from email: {}", emailPrefix);
        }

        // Always set lastName to dash if it's missing
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            user.setLastName("-");
            logger.info("Set lastName to dash");
        }
    }

    private void updateUserAttributes(UserModel user, BrokerContext brokerContext) {
        if (brokerContext == null || brokerContext.contextData == null) {
            logger.warn("No broker context data available for attribute update");
            return;
        }

        try {
            // Store Google ID first
            if (brokerContext.id != null) {
                user.setSingleAttribute("google_id", brokerContext.id);
//                logger.info("Set Google ID for user: {}", brokerContext.id);
            }

            ObjectMapper mapper = new ObjectMapper();

            // Try to update from identity provider first
            if (!updateFromIdentityProvider(user, brokerContext, mapper)) {
                // If IDP update fails, try user attributes
                updateFromUserAttributes(user, brokerContext, mapper);
            }
        } catch (Exception e) {
            logger.warn("Failed to update user attributes: {}", e.getMessage());
            // Continue flow despite attribute update failure
        }
    }


    private boolean updateFromUserAttributes(UserModel user, BrokerContext brokerContext, ObjectMapper mapper) {
        try {
            AttributeData firstNameData = brokerContext.contextData.get("user.attributes.firstName");
            AttributeData lastNameData = brokerContext.contextData.get("user.attributes.lastName");
            boolean updated = false;

            if (firstNameData != null && firstNameData.data != null) {
                try {
                    String decodedFirstName = new String(java.util.Base64.getDecoder().decode(firstNameData.data));
                    List<String> firstNames = mapper.readValue(decodedFirstName,
                            mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    if (!firstNames.isEmpty()) {
                        user.setFirstName(firstNames.get(0));
//                        logger.info("Set firstName from user attributes: {}", firstNames.get(0));
                        updated = true;
                    }
                } catch (Exception e) {
                    logger.warn("Error processing firstName: {}", e.getMessage());
                }
            }

            if (lastNameData != null && lastNameData.data != null) {
                try {
                    String decodedLastName = new String(java.util.Base64.getDecoder().decode(lastNameData.data));
                    List<String> lastNames = mapper.readValue(decodedLastName,
                            mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    if (!lastNames.isEmpty()) {
                        user.setLastName(lastNames.get(0));
//                        logger.info("Set lastName from user attributes: {}", lastNames.get(0));
                        updated = true;
                    }
                } catch (Exception e) {
                    logger.warn("Error processing lastName: {}", e.getMessage());
                }
            }

            return updated;
        } catch (Exception e) {
            logger.warn("Failed to update from user attributes: {}", e.getMessage());
            return false;
        }
    }

    private boolean updateFromIdentityProvider(UserModel user, BrokerContext brokerContext, ObjectMapper mapper) {
        try {
            AttributeData idpData = brokerContext.contextData.get("identity_provider_identity");
            if (idpData != null && idpData.data != null) {
                GoogleUserInfo googleUser = mapper.readValue(idpData.data, GoogleUserInfo.class);
                boolean updated = false;

                if (googleUser.given_name != null) {
                    user.setFirstName(googleUser.given_name);
                    logger.info("Set firstName from IDP: {}", googleUser.given_name);
                    updated = true;
                }

                if (googleUser.family_name != null) {
                    user.setLastName(googleUser.family_name);
                    logger.info("Set lastName from IDP: {}", googleUser.family_name);
                    updated = true;
                }

                return updated;
            }
        } catch (Exception e) {
            logger.warn("Failed to update from identity provider: {}", e.getMessage());
        }
        return false;
    }

    // Required AuthenticatorFactory methods
    @Override
    public void action(AuthenticationFlowContext context) {
        context.success();
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public String getDisplayType() {
        return "Google OAuth User Authenticator";
    }

    @Override
    public String getReferenceCategory() {
        return GOOGLE;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.ALTERNATIVE,
                AuthenticationExecutionModel.Requirement.DISABLED
        };
    }

    @Override
    public String getId() {
        return "custom-user-check-authenticator";
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {
        logger.info("Initializing Google OAuth User Authenticator");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return new ArrayList<>();
    }

    @Override
    public String getHelpText() {
        return "Processes and stores Google OAuth user information";
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }
}