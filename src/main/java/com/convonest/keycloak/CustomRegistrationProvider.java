package com.convonest.keycloak;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.keycloak.authentication.actiontoken.verifyemail.VerifyEmailActionToken;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resources.LoginActionsService;
import org.keycloak.services.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.convonest.keycloak.Constants.ADMIN_ROLE;
import static com.convonest.keycloak.Constants.ALL_ROLES;
import static com.convonest.keycloak.Constants.TARGET_REACT_CLIENT_ID;
import static com.convonest.keycloak.KeycloakUtils.assignUserToGroup;
import static com.convonest.keycloak.KeycloakUtils.writeValueAsString;

public class CustomRegistrationProvider implements RealmResourceProvider {
    private static final Logger logger = LoggerFactory.getLogger(CustomRegistrationProvider.class);

    private final KeycloakSession session;

    public CustomRegistrationProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return this;
    }

    @Override
    public void close() {
    }

    @GET
    @Path("/health/liveness")
    public Response liveness() {
        // Simple liveness check
        return Response.ok("Liveness check passed").build();
    }

    @GET
    @Path("/health/readiness")
    public Response readiness() {
        // Add any readiness checks you need, e.g., DB or cache connectivity
        return Response.ok("Readiness check passed").build();
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response register(@Context UriInfo uriInfo, @Context HttpHeaders headers,
                             MultivaluedMap<String, String> formData) {

        try {
            String email = formData.getFirst(Constants.EMAIL);
            logger.info("Registration process started for user:{} ", email);
            String password = formData.getFirst(Constants.PASSWORD);
            String companyName = formData.getFirst(Constants.COMPANY_NAME);
            String countryCode = formData.getFirst(Constants.COUNTRY_CODE);
            String firstName = formData.getFirst(Constants.FIRST_NAME);
            String lastName = formData.getFirst(Constants.LAST_NAME);
            String phoneCode = formData.getFirst(Constants.PHONE_CODE);
            String phoneNumber = formData.getFirst(Constants.PHONE_NUMBER);
            String sector = formData.getFirst(Constants.SECTOR);
            String plan = formData.getFirst(Constants.PLAN);
            // Validate input
            if (Validation.isBlank(email)) {
                logger.warn("EmailId is blank, Invalid request ");
                return Response.status(Response.Status.BAD_REQUEST)
                        .type(MediaType.APPLICATION_JSON)
                        .entity("Email are required")
                        .build();
            }

            if (Validation.isBlank(password)) {
                logger.warn("Password is blank for user :{} ", email);
                return
                        Response.status(Response.Status.BAD_REQUEST)
                                .type(MediaType.APPLICATION_JSON)
                                .entity("Password are required")
                                .build();
            }

            if (Validation.isBlank(phoneNumber)) {
                logger.warn("Phone number is blank for user :{} ", email);
                return Response.status(Response.Status.BAD_REQUEST)
                        .type(MediaType.APPLICATION_JSON)
                        .entity("Phone Number are required")
                        .build();
            }

            RealmModel realm = session.getContext().getRealm();
            ClientModel reactClient = realm.getClientByClientId(TARGET_REACT_CLIENT_ID);

            if (reactClient == null) {
                logger.error("Error in Client model registration in keycloak, Missing clientId:{} ",
                        TARGET_REACT_CLIENT_ID);
                return Response.status(Response.Status.BAD_REQUEST).entity("Client not found").build();
            }

            // Check if user already exists
            if (session.users().getUserByEmail(realm, email) != null) {
                logger.warn("Registration stopped started for duplicate user:{} ", email);
                return Response.status(Response.Status.CONFLICT).entity("User with this email already exists").build();
            }

            // Create user
            UserModel user = session.users().addUser(realm, email);
            user.setEnabled(true);
            user.setEmail(email);
            user.setUsername(email);
            user.setEmailVerified(false);
            user.setFirstName(firstName);
            user.setLastName(lastName);

            // NOTE :: IF changing the name of any Attributes, Also modify the tenant
            // Service Onboard flow in OnBoardingServiceImpl
            String tenantId = UUID.randomUUID().toString();


            user.setSingleAttribute(Constants.TENANT_ID, tenantId);
            user.setSingleAttribute(Constants.ORG_ID, UUID.randomUUID().toString());
            user.setSingleAttribute(Constants.COMPANY_NAME, companyName);
            user.setSingleAttribute(Constants.COUNTRY_CODE, countryCode);
            user.setSingleAttribute(Constants.PHONE_CODE, phoneCode);
            user.setSingleAttribute(Constants.PHONE_NUMBER, phoneNumber);
            user.setSingleAttribute(Constants.ORG_TYPE, Constants.INDIVIDUAL);
            user.setSingleAttribute(Constants.SECTOR, sector);
            user.setSingleAttribute(Constants.IS_ACTIVE, String.valueOf(false));
            user.setSingleAttribute(Constants.IS_ONBOARDED, String.valueOf(false));
            user.setSingleAttribute(Constants.ROLE, ADMIN_ROLE);
            user.setSingleAttribute(Constants.PRIVILEGES, writeValueAsString(ALL_ROLES));
            user.setSingleAttribute(Constants.PLAN, plan);


            String groupName = tenantId + Constants.ADMIN;
            assignUserToGroup(realm, user, groupName);

            // Set password
            user.credentialManager().updateCredential(UserCredentialModel.password(password));

            // Force Keycloak to require email verification at next login
            user.addRequiredAction(UserModel.RequiredAction.VERIFY_EMAIL);


            // Create action token and send verification email
            int expirationInSec = 86400; // 1 day span
            int expiration = (int) (Instant.now().getEpochSecond() + expirationInSec);
            String tokenId = new VerifyEmailActionToken(user.getId(), expiration, user.getEmail(), email,
                    reactClient.getClientId()).serialize(session, realm, uriInfo);

            URI actionUrl = LoginActionsService.actionTokenProcessor(uriInfo).queryParam("key", tokenId)
                    .build(realm.getName());


            List<ProviderFactory> emailProviders =
                    session.getKeycloakSessionFactory().getProviderFactoriesStream(EmailTemplateProvider.class).collect(Collectors.toList());

            if (emailProviders == null || emailProviders.isEmpty()) {
                logger.warn("No EmailTemplateProvider implementations found!");
            } else {
                logger.info("Available EmailTemplateProvider implementations:");
                for (ProviderFactory factory : emailProviders) {
                    logger.info(" - Implementation: " + factory.getId());
                }
            }


            EmailTemplateProvider emailProvider = session.getProvider(EmailTemplateProvider.class,
                    CustomEmailTemplateProviderFactory.ID);

            if (emailProvider == null) {
                logger.warn("Email provider is null!");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Email provider not configured")
                        .build();
            }

            try {

                int expirationInMin = expirationInSec / 60;
                emailProvider.setRealm(realm)
                        .setUser(user)
                        .sendVerifyEmail(actionUrl.toString(), expirationInMin);
            } catch (EmailException e) {
                logger.error("Exception while sending email verification ", e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Failed to send verification email: " + e.getMessage())
                        .build();
            }

            // Trigger REGISTER event
            new EventBuilder(realm, session, session.getContext().getConnection()).event(EventType.REGISTER).user(user)
                    .detail("username", email).detail("email", email).detail("register_method", "form").success();
            logger.info("Registration processed for the user :{}", email);
            // Redirect to confirmation page with success message
            return Response.ok("Registration successful! Please check your email to verify your account.").build();
        } catch (Exception ex) {
            logger.error("Error while registration", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error while registration, Try again later")
                    .build();
        }
    }
}
