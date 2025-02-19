package com.convonest.keycloak.gmail;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.AbstractFormAuthenticator;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.ArrayList;
import java.util.List;

public class CustomOnboardingAuthenticator extends AbstractFormAuthenticator implements Authenticator, AuthenticatorFactory {

    private static final Logger logger = Logger.getLogger(CustomOnboardingAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();

        if (user == null) {
            logger.warn("⚠️ User does not exist yet, waiting for user creation.");
            context.attempted(); // Skip this step and move forward
            return;
        }

        logger.info("🚀 authenticate() called for user: " + user.getUsername());

        AuthenticationSessionModel authSession = context.getAuthenticationSession();

        // Check if form has already been submitted successfully in this session
        if ("true".equals(authSession.getAuthNote("onboarding_completed"))) {
            logger.info("✅ Onboarding already completed in this session. Skipping.");
            context.success();
            return;
        }

        // Check if form has already been shown but not submitted
        if ("true".equals(authSession.getAuthNote("onboarding_shown"))) {
            logger.info("⚠️ Onboarding form already shown but not completed. Reshowing form.");
            context.challenge(context.form().createForm("onboarding-form.ftl"));
            return;
        }

        // Mark the form as shown
        authSession.setAuthNote("onboarding_shown", "true");

        // Show the onboarding form
        logger.info("📝 Displaying onboarding form for user: " + user.getUsername());
        context.challenge(context.form().createForm("onboarding-form.ftl"));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        String phoneNumber = context.getHttpRequest().getDecodedFormParameters().getFirst("phone");
        String company = context.getHttpRequest().getDecodedFormParameters().getFirst("company");

        if (phoneNumber == null || phoneNumber.trim().isEmpty() || company == null || company.trim().isEmpty()) {
            logger.warn("❌ Form validation failed. Missing fields.");
            context.failureChallenge(AuthenticationFlowError.INVALID_USER,
                    context.form().setError("All fields are required").createForm("onboarding-form.ftl"));
            return;
        }

        UserModel user = context.getUser();
        user.setSingleAttribute("phoneNumber", phoneNumber);
        user.setSingleAttribute("company", company);
        user.setSingleAttribute("tenantId", "12345");

        // Mark onboarding as completed
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        authSession.setAuthNote("onboarding_completed", "true");
        authSession.removeAuthNote("onboarding_shown"); // Clean up

        logger.info("✅ User details saved successfully: " + user.getUsername());
        context.success();
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
