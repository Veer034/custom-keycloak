package com.convonest.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.models.GroupModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public interface KeycloakUtils {

    ObjectMapper MAPPER = new ObjectMapper();

    static String writeValueAsString(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    static void assignUserToGroup(RealmModel realm, UserModel user, String groupName) {
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


}
