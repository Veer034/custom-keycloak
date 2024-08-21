package com.convonest.keycloak;

import java.util.logging.Logger;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class CustomEventListenerProvider implements EventListenerProvider {

	private static final Logger logger = Logger.getLogger(CustomEventListenerProvider.class.getSimpleName());
	private final KeycloakSession session;

	public CustomEventListenerProvider(KeycloakSession session) {
		this.session = session;
	}

	@Override
	public void onEvent(Event event) {
		if (event.getType() == EventType.VERIFY_EMAIL) {
			RealmModel realm = session.realms().getRealm(event.getRealmId());
			UserModel user = session.users().getUserById(realm, event.getUserId());
			if (user != null) {
				user.setEmailVerified(true);
				logger.info("User [" + user.getUsername() + "] has been enabled after email verification.");
			}
		}
	}

	@Override
	public void onEvent(AdminEvent event, boolean includeRepresentation) {

	}

	@Override
	public void close() {
	}
}
