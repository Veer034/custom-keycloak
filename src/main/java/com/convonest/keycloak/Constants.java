package com.convonest.keycloak;

public interface Constants {
    int MAX_RETRIES = 3; // Maximum number of retries
    long RETRY_DELAY = 2000; // Delay between retries in milliseconds

    String TARGET_REACT_CLIENT_ID = "react-client";  // ✅ Filter only react-client

    int CRON_EXECUTION_DELAY_MILLIS = 10000;  // ✅ Filter only react-client


}
