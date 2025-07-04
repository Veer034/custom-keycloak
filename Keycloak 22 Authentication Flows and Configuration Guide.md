# Keycloak 22 Authentication Flows and Configuration Guide

This guide explains the various authentication flows available in Keycloak 22, when to use them, and how to configure
them properly. It also covers custom themes, event listeners, and identity provider integration.

## Table of Contents

- [Authentication Flows](#authentication-flows)
    - [Standard Flow (Authorization Code)](#standard-flow-authorization-code)
    - [Direct Access Grants (Resource Owner Password Credentials)](#direct-access-grants-resource-owner-password-credentials)
    - [Implicit Flow](#implicit-flow)
    - [Service Accounts Roles](#service-accounts-roles)
    - [OAuth 2.0 Device Authorization Grant](#oauth-20-device-authorization-grant)
    - [OIDC CIBA Grant](#oidc-ciba-grant)
- [Client Authentication](#client-authentication)
- [Authorization](#authorization)
- [Custom Themes](#custom-themes)
- [Event Listeners](#event-listeners)
- [Identity Provider Integration (Gmail)](#identity-provider-integration-gmail)

## Authentication Flows

Keycloak supports multiple authentication flows to accommodate different use cases. You can enable or disable these
flows at the client level.

### Standard Flow (Authorization Code)

**What it is**: The most secure OAuth 2.0 flow, also known as the Authorization Code flow. It is a redirect-based flow
that issues an authorization code which is exchanged for tokens.

**When to use**:

- Web applications with a server-side component
- Mobile applications with secure storage
- Any application where security is paramount

**Configuration**:

1. In the Keycloak admin console, go to **Clients** → select your client
2. Under the **Settings** tab, ensure **Standard Flow Enabled** is toggled ON
3. Configure valid redirect URIs for your client
4. Set up a proper post-logout redirect URI

**Security considerations**:

- Requires PKCE (Proof Key for Code Exchange) for public clients
- Needs proper redirect URI validation
- Uses refresh tokens for extended sessions

### Direct Access Grants (Resource Owner Password Credentials)

**What it is**: Allows clients to obtain an access token by directly sending the user's credentials (username and
password) to Keycloak.

**When to use**:

- Legacy applications that cannot implement redirect flows
- Command-line interfaces or scripts
- Testing and development environments

**Configuration**:

1. Navigate to **Clients** → select your client
2. Under **Settings**, enable **Direct Access Grants Enabled**
3. Set appropriate client roles and scopes

**Security considerations**:

- Less secure as it exposes user credentials to the client
- Should be avoided in public clients
- Not recommended for production applications if alternatives exist

### Implicit Flow

**What it is**: A simplified OAuth flow that returns tokens directly in the redirect URI fragment.

**When to use**:

- Legacy applications that can't securely store authorization codes
- Simple applications with limited security requirements
- Note: This flow is deprecated in OAuth 2.0 standards

**Configuration**:

1. Go to **Clients** → select your client
2. Enable **Implicit Flow Enabled** under **Settings**
3. Configure valid redirect URIs

**Security considerations**:

- Less secure as tokens are exposed in the browser URL
- No refresh token support
- Cannot use PKCE
- Considered deprecated in favor of Authorization Code flow with PKCE

### Service Accounts Roles

**What it is**: Allows a client to request an access token for itself (client credentials flow) rather than on behalf of
a user.

**When to use**:

- Service-to-service authentication
- Background processes
- APIs that don't act on behalf of a user

**Configuration**:

1. Navigate to **Clients** → select your client
2. Set **Access Type** to **confidential**
3. Enable **Service Accounts Enabled**
4. Go to **Service Account Roles** tab to assign appropriate roles

**Security considerations**:

- Requires proper client authentication
- Access limited to the client's assigned roles
- Should use strong client secrets

### OAuth 2.0 Device Authorization Grant

**What it is**: Enables authentication for devices with limited input capabilities (like smart TVs, IoT devices).

**When to use**:

- Smart TVs, gaming consoles
- IoT devices
- Devices where typing passwords is difficult

**Configuration**:

1. Go to **Clients** → select your client
2. Enable **OAuth 2.0 Device Authorization Grant Enabled**
3. Customize the device code expiration and polling interval in realm settings

**Security considerations**:

- Requires a secondary device for authentication
- Uses short-lived device codes
- Requires polling from the device

### OIDC CIBA Grant

**What it is**: Client Initiated Backchannel Authentication flow allows clients to initiate authentication and
authorization without redirects.

**When to use**:

- Advanced banking applications
- Scenarios where the authentication device differs from the requesting device
- Cases requiring strong decoupled authentication

**Configuration**:

1. Navigate to **Clients** → select your client
2. Enable **OIDC CIBA Grant Enabled**
3. Configure CIBA policy in the Authentication section of your realm

**Security considerations**:

- Requires backchannel authentication
- Complex to implement but highly secure
- Requires pre-registered authentication devices

## Client Authentication

Client authentication determines how a confidential client proves its identity to Keycloak when requesting tokens.

**Options**:

- **Client ID and Secret**: A shared secret between Keycloak and the client
- **Client JWT**: Client signs a JWT with a private key
- **X.509 Certificate**: Client uses a certificate for authentication

**When to enable**:

- Always enable for confidential clients
- Required for service accounts
- Necessary for token exchange

**Configuration**:

1. Set **Access Type** to **confidential** in client settings
2. Choose the authentication method in the **Credentials** tab
3. Configure the credentials based on the selected method

## Authorization

Keycloak's Authorization Services provides fine-grained authorization capabilities beyond simple role-based access
control.

**When to enable**:

- When you need complex permission rules
- For applications requiring resource-based permissions
- When you need to centralize authorization policies

**Configuration**:

1. Navigate to **Clients** → select your client
2. Enable **Authorization Enabled** under **Settings**
3. Define resources, scopes, policies, and permissions in the **Authorization** tab
4. Implement policy enforcement in your application

**Key components**:

- **Resources**: Objects you want to protect
- **Scopes**: Actions that can be performed on resources
- **Policies**: Rules that decide if access is granted
- **Permissions**: Combine resources/scopes with policies

## Custom Themes

Keycloak supports custom themes for login pages, admin console, email templates, and more.

**Creating a custom theme**:

1. **Theme structure**:
   Create a directory structure like:
   ```
   themes/
   └── your-theme/
       ├── login/
       │   ├── theme.properties
       │   ├── resources/
       │   ├── messages/
       │   └── templates/
       ├── account/
       └── email/
   ```

2. **Base files**:
    - Start by copying files from an existing theme (like `keycloak` or `base`)
    - Modify HTML templates, CSS, and messages as needed

3. **Properties file**:
   ```properties
   # theme.properties
   parent=base
   import=common/keycloak
   styles=css/styles.css
   ```

4. **Installation**:
    - Place your theme in the `themes` directory of your Keycloak server
    - For containerized deployments, you can mount your themes directory

5. **Activation**:
    - Go to **Realm Settings** → **Themes**
    - Select your theme for Login, Account, Email, etc.
    - Save changes

**Development tips**:

- Use the `keycloak.theme.cacheThemes=false` system property during development
- Leverage the parent theme inheritance to minimize duplication
- Use message bundles for internationalization

## Event Listeners

Event listeners in Keycloak provide a way to hook into various events like user registration, login, logout, etc.

**Types of events**:

- **Login events**: Success, failure, reset credentials, etc.
- **Admin events**: User creation, client creation, role mapping, etc.

**Configuring event listeners**:

1. **Enable events**:
    - Go to **Realm Settings** → **Events**
    - Under **Login Events Settings** or **Admin Events Settings**, enable **Save Events**
    - Select event types to log or save all events
    - Configure event expiration

2. **Enabling built-in listeners**:
    - In the **Event Listeners** field, add listeners like:
        - `jboss-logging`: Logs events to the server log
        - `email`: Sends email for specific events
        - `kafka`: Publishes events to Kafka (requires additional setup)

3. **Creating custom event listeners**:
    - Implement the `EventListenerProvider` interface
    - Register your provider using Java SPI
    - Deploy as a JAR file or module to Keycloak

**Example: Email event listener configuration**:

1. Add `email` to the Event Listeners list
2. Configure email settings in the realm
3. Define which events trigger emails

**Example: Kafka integration**:

1. Add Kafka dependencies to Keycloak
2. Configure Kafka connection properties
3. Add `kafka` to the Event Listeners list
4. Define topic mapping for events

## Identity Provider Integration (Gmail)

Setting up Gmail (Google) as an identity provider allows users to log in with their Google accounts.

### Setting up Google Identity Provider

1. **Create Google OAuth credentials**:
    - Go to [Google Cloud Console](https://console.cloud.google.com/)
    - Navigate to **APIs & Services** → **Credentials**
    - Create an **OAuth client ID** (Web application type)
    - Add authorized redirect URIs: `https://your-keycloak-server/realms/your-realm/broker/google/endpoint`
    - Note your Client ID and Client Secret

2. **Configure Google IdP in Keycloak**:
    - In Keycloak admin console, go to **Identity Providers**
    - Select **Google** from the dropdown
    - Enter the Google Client ID and Client Secret
    - Configure other settings:
        - **Enabled**: Turn ON
        - **Display Name**: Google
        - **Default Scopes**: openid profile email
        - **Store Tokens**: Optional, enable to store tokens for later use

3. **Advanced Configuration**:

   **User Synchronization**:
    - **Sync Mode**:
        - `import`: Import user details on first login only
        - `force`: Update user details on every login
        - `legacy`: Match by username or email

   **First Login Flow**:
    - Customize the authentication flow when a user logs in with Google for the first time
    - You can add steps like email verification, profile update, etc.

   **Trust Email**:
    - Enable to skip email verification for Google-authenticated users
    - Recommended for Google as it verifies emails

4. **Mappers**:
    - Create mappers to properly map Google profile data to Keycloak user attributes
    - Common mappers:
        - **Name**: Maps Google's `name` to Keycloak's `firstName` and `lastName`
        - **Email**: Maps Google's `email` to Keycloak's `email`
        - **Username**: Maps Google's `email` or `sub` to Keycloak's `username`

### Testing and Customization

1. **Test the integration**:
    - Go to your Keycloak login page
    - You should see a "Login with Google" button
    - Click it and go through the Google authentication process

2. **Customizing the Button**:
    - Modify the login theme to change the appearance of the Google button
    - Add custom CSS for the social buttons

3. **Troubleshooting**:
    - Check Keycloak server logs for errors
    - Verify redirect URIs match exactly
    - Ensure Google API is enabled in Google Cloud Console

### Advanced Gmail Integration Features

1. **Automatic Link**:
    - Enable "Auto-link existing users" to link Google accounts to existing Keycloak users with matching emails

2. **Hiding the provider**:
    - Set "Hide on Login Page" if you want to use the provider for background linking but not display it on the login
      page

3. **Forced Identity Provider**:
    - You can force users to use Google by configuring an authenticator in your browser flow

4. **MFA with Google**:
    - Combine Google login with additional factors for enhanced security
    - Configure a post-login flow with additional factors

## Summary of Authentication Flow Selection

| Flow Type | Use Case | Security Level | Refresh Token Support |
|-----------|----------|---------------|----------------------|
| Standard Flow | Web apps, mobile apps | High | Yes |
| Direct Access Grants | CLI tools, legacy apps | Medium | Yes |
| Implicit Flow | Simple SPA (legacy) | Low | No |
| Service Accounts | Service-to-service | High | N/A |
| Device Flow | IoT, Smart TVs | Medium | Yes |
| CIBA | Banking, advanced security | Very High | Yes |

Remember to always select the most secure flow that your application can support. The Standard Flow with PKCE is
recommended for most modern applications.