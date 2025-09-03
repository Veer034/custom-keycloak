package com.convonest.keycloak;

import java.util.Collections;
import java.util.List;

public interface Constants {
    int MAX_RETRIES = 3; // Maximum number of retries
    long RETRY_DELAY = 2000; // Delay between retries in milliseconds

    String TARGET_REACT_CLIENT_ID = "react-client";  // ✅ Filter only react-client

    int CRON_EXECUTION_DELAY_MILLIS = 10000;  // ✅ Filter only react-client


    String ORG_TYPE = "orgType";
    String COUNTRY_CODE = "countryCode";
    String ROLE = "role";
    String TENANT_ID = "tenantId";
    String ORG_ID = "orgId";
    String PRIVILEGES = "privileges";
    String PLAN = "plan";
    String IS_ACTIVE = "isActive";

    String IS_ONBOARDED = "isOnboarded";
    String COMPANY_NAME = "companyName";
    String PHONE_CODE = "phoneCode";
    String PHONE_NUMBER = "phoneNumber";
    String SECTOR = "sector";
    String INDIVIDUAL = "INDIVIDUAL";

    String ADMIN_ROLE = "ADMIN";

    List<String> ALL_ROLES = Collections.unmodifiableList(List.of("all_r", "all_w"));
    String ADMIN = "_ADMIN";
    String SOCIAL_PROVIDER = "socialProvider";
    String GOOGLE = "google";
    String FIRST_NAME = "firstName";
    String LAST_NAME = "lastName";
    String EMAIL = "email";
    String PASSWORD = "password";
}
