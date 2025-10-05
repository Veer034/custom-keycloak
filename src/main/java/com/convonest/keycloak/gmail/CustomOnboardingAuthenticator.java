package com.convonest.keycloak.gmail;

import com.convonest.keycloak.Constants;
import jakarta.ws.rs.core.MultivaluedMap;
import org.keycloak.Config;
import org.keycloak.authentication.AbstractFormAuthenticator;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.convonest.keycloak.Constants.ADMIN_ROLE;
import static com.convonest.keycloak.Constants.ALL_ROLES;
import static com.convonest.keycloak.KeycloakUtils.assignUserToGroup;
import static com.convonest.keycloak.KeycloakUtils.writeValueAsString;

public class CustomOnboardingAuthenticator extends AbstractFormAuthenticator implements Authenticator, AuthenticatorFactory {

    private static final Logger logger = LoggerFactory.getLogger(CustomOnboardingAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();

        if (user == null) {
            logger.warn("⚠️ User does not exist yet, waiting for user creation.");
            context.attempted(); // Skip this step and move forward
            return;
        }


        // Check if user has already completed onboarding by checking for required attributes
        if (isOnboardingComplete(user)) {
            logger.info("✅ User already completed onboarding, skipping form: " + user.getUsername());
            context.success();
            return;
        }


        logger.info("🚀 authenticate() called for user: " + user.getUsername());

        // Show the onboarding form

        Map<String, String> formData = new HashMap<>();
        formData.put(Constants.FIRST_NAME, user.getFirstName() != null ? user.getFirstName() : "");
        formData.put(Constants.LAST_NAME, user.getLastName() != null ? user.getLastName() : "");
        formData.put(Constants.EMAIL, user.getEmail() != null ? user.getEmail() : "");


        logger.info("📝 Displaying onboarding form for user: " + user.getUsername());
        context.challenge(context.form().setAttribute("onboarding", formData).createForm("onboarding.ftl"));
    }

    private boolean isOnboardingComplete(UserModel user) {
        // Check for required attributes that indicate completed onboarding
        return StringUtil.isNotBlank(user.getFirstAttribute(Constants.TENANT_ID));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        logger.info("🔍 Action method called");

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        logger.info("📝 Form parameters: " + formData);

        UserModel user = context.getUser();
        if (user == null) {
            logger.error("❌ User is null in action method");
            context.attempted();
            return;
        }

        logger.info("👤 Processing form for user: " + user.getUsername());

        try {

            String tenantId = UUID.randomUUID().toString();


            user.setSingleAttribute(Constants.TENANT_ID, tenantId);
            user.setSingleAttribute(Constants.ORG_ID, UUID.randomUUID().toString());
            user.setSingleAttribute(Constants.ORG_TYPE, Constants.INDIVIDUAL);

            // Process form data and save to user
            user.setFirstName(formData.getFirst(Constants.FIRST_NAME));
            user.setLastName(formData.getFirst(Constants.LAST_NAME));
            user.setSingleAttribute(Constants.COMPANY_NAME, formData.getFirst(Constants.COMPANY_NAME));
            user.setSingleAttribute(Constants.PHONE_CODE, formData.getFirst(Constants.PHONE_CODE));
            user.setSingleAttribute(Constants.PHONE_NUMBER, formData.getFirst(Constants.PHONE_NUMBER));
            user.setSingleAttribute(Constants.COUNTRY_CODE, formData.getFirst(Constants.COUNTRY_CODE));
            user.setSingleAttribute(Constants.SECTOR, formData.getFirst(Constants.SECTOR));
            user.setSingleAttribute(Constants.PLAN, formData.getFirst(Constants.PLAN));

            // Ensure the user has the correct social login provider attribute
            user.setSingleAttribute(Constants.SOCIAL_PROVIDER, SocialProvider.GOOGLE.name());

            // **1. Create Group in Keycloak & Assign User to Group**

            RealmModel realm = context.getRealm();

            String groupName = tenantId + Constants.ADMIN;
            assignUserToGroup(realm, user, groupName);


            // **2. Set Default Attributes (isActive, role)**
            user.setSingleAttribute(Constants.IS_ACTIVE, String.valueOf(false));
            user.setSingleAttribute(Constants.IS_ONBOARDED, String.valueOf(false));
            user.setSingleAttribute(Constants.ROLE, ADMIN_ROLE);
            user.setSingleAttribute(Constants.PRIVILEGES, writeValueAsString(ALL_ROLES));

            logger.info("✅ User details saved successfully for: " + user.getUsername());
            context.success();

            // Trigger REGISTER event
            // Get the session from context
            KeycloakSession session = context.getSession();


            new EventBuilder(realm, session, session.getContext().getConnection()).event(EventType.REGISTER).user(user)
                    .detail("username", user.getUsername()).detail(Constants.EMAIL, user.getEmail()).detail("register_method", "form").success();


        } catch (Exception e) {
            logger.error("❌ Error processing form: " + e.getMessage(), e);
            context.failure(AuthenticationFlowError.INTERNAL_ERROR);
        }


    }


    @Override
    public boolean requiresUser() {
        return false; // No pre-existing user required
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true; // Always execute this authenticator
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // No required actions
    }

    @Override
    public String getDisplayType() {
        return "Custom Onboarding Form";
    }

    @Override
    public String getReferenceCategory() {
        return "Onboarding";
    }

    @Override
    public boolean isConfigurable() {
        return false; // No additional configuration needed
    }

    @Override
    public String getId() {
        return "custom-onboarding-authenticator";
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {
        logger.info("Initializing CustomOnboardingAuthenticator");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        logger.info("Post Init CustomOnboardingAuthenticator");
    }


    @Override
    public void close() {
        logger.info("Closing CustomOnboardingAuthenticator");
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return new ArrayList<>(); // No configurable properties
    }


    @Override
    public int order() {
        return 0; // Default order
    }

    @Override
    public String getHelpText() {
        return "Displays a custom onboarding form for new Google users.";
    }

    @Override
    public boolean isUserSetupAllowed() {
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
}
