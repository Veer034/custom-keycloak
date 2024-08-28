package com.convonest.keycloak;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

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
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resources.LoginActionsService;
import org.keycloak.services.validation.Validation;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

public class CustomRegistrationProvider implements RealmResourceProvider {

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

        // Set password
        user.credentialManager().updateCredential(UserCredentialModel.password(password));

        // Create action token and send verification email
        int lifespan = 604800; // 7 days in seconds
        int expiration = (int) (Instant.now().getEpochSecond() + lifespan);
        String tokenId = new VerifyEmailActionToken(user.getId(), expiration, user.getEmail(), email,
                client.getClientId()).serialize(session, realm, uriInfo);

        URI actionUrl = LoginActionsService.actionTokenProcessor(uriInfo).queryParam("key", tokenId)
                .build(realm.getName());

        try {
            session.getProvider(EmailTemplateProvider.class).setRealm(realm).setUser(user)
                    .sendVerifyEmail(actionUrl.toString(), lifespan);
        } catch (EmailException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to send verification email")
                    .build();
        }

        // Trigger REGISTER event
        new EventBuilder(realm, session, session.getContext().getConnection()).event(EventType.REGISTER).user(user)
                .detail("username", email).detail("email", email).detail("register_method", "form").success();

        // Redirect to confirmation page with success message
        return Response.ok("Registration successful! Please check your email to verify your account.").build();

    }
}
