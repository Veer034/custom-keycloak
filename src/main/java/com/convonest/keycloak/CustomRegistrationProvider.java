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
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resources.LoginActionsService;
import org.keycloak.services.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.convonest.keycloak.Constants.ALL_ROLES;
import static com.convonest.keycloak.Constants.TARGET_REACT_CLIENT_ID;

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
        String email = formData.getFirst("email");
        String password = formData.getFirst("password");
        String companyName = formData.getFirst(Constants.COMPANY_NAME);
        String country = formData.getFirst(Constants.COUNTRY);
        String firstName = formData.getFirst("firstName");
        String lastName = formData.getFirst("lastName");
        String countryCode = formData.getFirst(Constants.COUNTRY_CODE);
        String phoneNumber = formData.getFirst(Constants.PHONE_NUMBER);
        String sector = formData.getFirst(Constants.SECTOR);


        String isSocialLogin = formData.getFirst("isSocialLogin");

        boolean isSocialLoginFlag = isSocialLogin != null && isSocialLogin == "true";

        // Validate input
        if (Validation.isBlank(email)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Email are required")
                    .build();
        }

        if (!isSocialLoginFlag && Validation.isBlank(password)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(" Password  are required")
                    .build();
        }

        if (Validation.isBlank(phoneNumber)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(" Phone Number are required")
                    .build();
        }


        RealmModel realm = session.getContext().getRealm();
        ClientModel reactClient = realm.getClientByClientId(TARGET_REACT_CLIENT_ID);

        if (reactClient == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Client not found").build();
        }


        // **Check if the user already exists**
        UserModel existingUser = session.users().getUserByEmail(realm, email);

        if (existingUser != null) {
            if (isSocialLoginFlag) {
                logger.info("User exists. Updating fields for social login user: {}", email);

                // **Update existing user profile without affecting password**
                existingUser.setFirstName(firstName);
                existingUser.setLastName(lastName);
                existingUser.setAttribute(Constants.COMPANY_NAME, Collections.singletonList(companyName));
                existingUser.setAttribute(Constants.COUNTRY, Collections.singletonList(country));
                existingUser.setAttribute(Constants.COUNTRY_CODE, Collections.singletonList(countryCode));
                existingUser.setAttribute(Constants.PHONE_NUMBER, Collections.singletonList(phoneNumber));
                existingUser.setAttribute(Constants.SECTOR, Collections.singletonList(sector));

                // Ensure the user has the correct social login provider attribute
                existingUser.setAttribute(Constants.SOCIAL_PROVIDER, Collections.singletonList("true"));


                // **1. Create Group in Keycloak & Assign User to Group**
                String tenantId = existingUser.getFirstAttribute(Constants.TENANT_ID);

                String groupName = tenantId + Constants.ADMIN;
                assignUserToGroup(realm, existingUser, groupName);


                // **2. Set Default Attributes (isActive, role)**
                existingUser.setAttribute(Constants.IS_ACTIVE, Collections.singletonList(String.valueOf(true)));
                existingUser.setAttribute(Constants.ROLE, ALL_ROLES);

                logger.info("Updated social user successfully.");

                // Redirect to frontend with token in URL
                URI redirectUri = URI.create("http://localhost:3000/setup-status");
                return Response.seeOther(redirectUri).build();
            } else {
                return Response.status(Response.Status.CONFLICT).entity("User with this email already exists").build();
            }
        }

        // Create user
        UserModel newUser = session.users().addUser(realm, email);
        newUser.setEnabled(true);
        newUser.setEmail(email);
        newUser.setUsername(email);
        newUser.setEmailVerified(false);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);

        // NOTE :: IF changing the name of any Attributes, Also modify the tenant
        // Service Onboard flow in OnBoardingServiceImpl
        newUser.setAttribute(Constants.TENANT_ID, Collections.singletonList(UUID.randomUUID().toString()));
        newUser.setAttribute(Constants.ORG_ID, Collections.singletonList(UUID.randomUUID().toString()));
        newUser.setAttribute(Constants.COMPANY_NAME, Collections.singletonList(companyName));
        newUser.setAttribute(Constants.COUNTRY, Collections.singletonList(country));
        newUser.setAttribute(Constants.COUNTRY_CODE, Collections.singletonList(countryCode));
        newUser.setAttribute(Constants.PHONE_NUMBER, Collections.singletonList(phoneNumber));
        newUser.setAttribute(Constants.ORG_TYPE, Collections.singletonList(Constants.INDIVIDUAL));
        newUser.setAttribute(Constants.SECTOR, Collections.singletonList(sector));


        // If this is a social login, **DO NOT** set a password
        if (isSocialLoginFlag) {

            newUser.setAttribute(Constants.SOCIAL_PROVIDER, Collections.singletonList("true"));


            logger.info("social user registration successfully.");

            // Redirect to frontend with token in URL
            URI redirectUri = URI.create("http://localhost:3000/setup-status");
            return Response.seeOther(redirectUri).build();

        } else {
            // Set password
            newUser.credentialManager().updateCredential(UserCredentialModel.password(password));
            // Force Keycloak to require email verification at next login
            newUser.addRequiredAction(UserModel.RequiredAction.VERIFY_EMAIL);

            // Create action token and send verification email
            int expirationInSec = 86400; // 1 day span
            int expiration = (int) (Instant.now().getEpochSecond() + expirationInSec);
            String tokenId = new VerifyEmailActionToken(newUser.getId(), expiration, newUser.getEmail(), email,
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

            EmailTemplateProvider emailProvider = session.getProvider(EmailTemplateProvider.class);

            if (emailProvider == null) {
                logger.warn("Email provider is null!");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Email provider not configured")
                        .build();
            }

//            try {
//
//                int expirationInMin = expirationInSec / 60;
//                emailProvider.setRealm(realm)
//                        .setUser(newUser)
//                        .sendVerifyEmail(actionUrl.toString(), expirationInMin);
//            } catch (EmailException e) {
//                logger.error("Exception while sending email verification ", e);
//                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                        .entity("Failed to send verification email: " + e.getMessage())
//                        .build();
//            }

            // Trigger REGISTER event
            new EventBuilder(realm, session, session.getContext().getConnection()).event(EventType.REGISTER).user(newUser)
                    .detail("username", email).detail("email", email).detail("register_method", "form").success();


            logger.info("social user registration successfully.");


            // Redirect to confirmation page with success message
            return Response.ok("Registration successful! Please check your email to verify your account.").build();

        }


    }


    private void assignUserToGroup(RealmModel realm, UserModel user, String groupName) {
        // Use Keycloak's `getGroupsStream()` to efficiently find the group
        GroupModel group = realm.getGroupsStream()
                .filter(g -> g.getName().equals(groupName))
                .findFirst()
                .orElse(null);

        // If the group does not exist, create it
        if (group == null) {
            group = realm.createGroup(groupName);
        }

        // Assign the user to the group if not already a member
        if (!user.isMemberOf(group)) {
            user.joinGroup(group);
        }
    }


    private String generateTokenForUser(UserModel user, RealmModel realm) {
        KeycloakSessionFactory sessionFactory = session.getKeycloakSessionFactory();
        KeycloakSession newSession = sessionFactory.create();

        try {
            AccessToken token = new AccessToken();
            token.setSubject(user.getId());
            token.setEmail(user.getEmail());
            token.setGivenName(user.getFirstName());
            token.setFamilyName(user.getLastName());

            // **1. Add User Attributes to Token**
            Map<String, List<String>> attributes = user.getAttributes();
            if (attributes != null) {
                token.getOtherClaims().put(Constants.ORG_TYPE, getUserAttribute(user, Constants.ORG_TYPE));
                token.getOtherClaims().put(Constants.COUNTRY, getUserAttribute(user, Constants.COUNTRY));
                token.getOtherClaims().put(Constants.ROLE, getUserAttribute(user, Constants.ROLE));
                token.getOtherClaims().put(Constants.TENANT_ID, getUserAttribute(user, Constants.TENANT_ID));
                token.getOtherClaims().put(Constants.ORG_ID, getUserAttribute(user, Constants.ORG_ID));
                token.getOtherClaims().put(Constants.PRIVILEGES, getUserAttribute(user, Constants.PRIVILEGES));
                token.getOtherClaims().put(Constants.IS_ACTIVE, getUserAttribute(user, Constants.IS_ACTIVE));
            }

            // **2. Add Client Information from User Session**
            AuthenticatedClientSessionModel clientSession = newSession.sessions().getUserSession(realm, user.getId())
                    .getAuthenticatedClientSessionByClient(realm.getClientByClientId(TARGET_REACT_CLIENT_ID).getId());

            if (clientSession != null) {
                token.getOtherClaims().put("client_host", clientSession.getNote("Client Host"));
                token.getOtherClaims().put("client_ip", clientSession.getNote("Client IP Address"));
                token.getOtherClaims().put("client_id", clientSession.getClient().getClientId());
            }

            return newSession.tokens().encode(token);

        } finally {
            newSession.close();
        }
    }

    /**
     * Helper method to get user attribute safely
     */
    private String getUserAttribute(UserModel user, String attribute) {
        List<String> values = user.getAttributes().get(attribute);
        return (values != null && !values.isEmpty()) ? values.get(0) : "";
    }

}
