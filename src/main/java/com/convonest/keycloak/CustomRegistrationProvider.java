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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        String companyName = formData.getFirst("companyName");
        String country = formData.getFirst("country");
        String firstName = formData.getFirst("firstName");
        String lastName = formData.getFirst("lastName");
        String countryCode = formData.getFirst("countryCode");
        String phoneNumber = formData.getFirst("phoneNumber");
        String sector = formData.getFirst("sector");

        // Validate input
        if (Validation.isBlank(email) || Validation.isBlank(password) || Validation.isBlank(phoneNumber)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Email, Password, and Phone Number are required")
                    .build();
        }

        RealmModel realm = session.getContext().getRealm();
        ClientModel client = realm.getClientByClientId("account");

        if (client == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Client not found").build();
        }

        // Check if user already exists
        if (session.users().getUserByEmail(realm, email) != null) {
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
        user.setAttribute("tenantId", Collections.singletonList(UUID.randomUUID().toString()));
        user.setAttribute("orgId", Collections.singletonList(UUID.randomUUID().toString()));
        user.setAttribute("companyName", Collections.singletonList(companyName));
        user.setAttribute("country", Collections.singletonList(country));
        user.setAttribute("countryCode", Collections.singletonList(countryCode));
        user.setAttribute("phoneNumber", Collections.singletonList(phoneNumber));
        user.setAttribute("orgType", Collections.singletonList("INDIVIDUAL"));
        user.setAttribute("sector", Collections.singletonList(sector));


        // Set password
        user.credentialManager().updateCredential(UserCredentialModel.password(password));

        // Force Keycloak to require email verification at next login
        user.addRequiredAction(UserModel.RequiredAction.VERIFY_EMAIL);


        // Create action token and send verification email
        int expirationInSec = 86400; // 1 day span
        int expiration = (int) (Instant.now().getEpochSecond() + expirationInSec);
        String tokenId = new VerifyEmailActionToken(user.getId(), expiration, user.getEmail(), email,
                client.getClientId()).serialize(session, realm, uriInfo);

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
        logger.error("verificationUrl: {}", actionUrl);
        // Redirect to confirmation page with success message
        return Response.ok("Registration successful! Please check your email to verify your account.").build();

    }
}
