# custom-keycloak

Command for key cloak fat jar creation

    ./gradlew clean shadowJar

Google Configuration for OAuth2.0

### In Keycloak Admin Console:

1. Go to Identity Providers
2. Add Provider -> Google
3. You'll need:

   Client ID from Google Cloud Console
   Client Secret from Google Cloud Console
   Redirect URI (will be shown in Keycloak)

### Google Cloud Console Configuration:

1. Create a new project
2. Enable Google+ API
3. Create OAuth 2.0 credentials
4. Add authorized redirect URI from Keycloak

   ```
   http://localhost:8080/realms/master/broker/google/endpoint
   http://localhost:8090/email-access/token/gmail
   http://localhost:3000/email/add/gmail
   ```

6. Add authorized JavaScript origins

   ```
   http://localhost:3000
   https://convonest.com
   ```
