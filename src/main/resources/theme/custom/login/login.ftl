<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false displayInfo=realm.password && realm.registrationAllowed &&
!registrationDisabled??; section>
    <#if section = "header">
       <!-- No Data -->
    <#elseif section = "form">
        <div id="kc-form">
            <div id="kc-form-wrapper">
                <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
                    <div class="login-container">
                        <!-- Logo -->
                        <div class="logo-container">
                            <img src="${url.resourcesPath}/img/company_logo.png" alt="Convonest Logo" class="logo">
                            <h2>Welcome to Convonest Analytics</h2>
                            <p>Sign in to access your dashboard</p>
                        </div>

                        <!-- Error Messages - FIXED -->
                        <#if messagesPerField.existsError('username','password')>
                            <div class="error-message">
                                <#if messagesPerField.existsError('username')>
                                    <#assign usernameErrors = messagesPerField.get('username')>
                                    <#if usernameErrors?is_sequence>
                                        <#list usernameErrors as error>
                                            <span>${error}</span><br/>
                                        </#list>
                                    <#else>
                                        <span>${usernameErrors}</span><br/>
                                    </#if>
                                </#if>
                                <#if messagesPerField.existsError('password')>
                                    <#assign passwordErrors = messagesPerField.get('password')>
                                    <#if passwordErrors?is_sequence>
                                        <#list passwordErrors as error>
                                            <span>${error}</span><br/>
                                        </#list>
                                    <#else>
                                        <span>${passwordErrors}</span><br/>
                                    </#if>
                                </#if>
                            </div>
                        <#elseif message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
                            <div class="${message.type}-message">
                                <#if message.type = 'success'><span class="pficon pficon-ok"></span></#if>
                                <#if message.type = 'warning'><span class="pficon pficon-warning-triangle-o"></span></#if>
                                <#if message.type = 'error'><span class="pficon pficon-error-circle-o"></span></#if>
                                <#if message.type = 'info'><span class="pficon pficon-info"></span></#if>
                                <span class="kc-feedback-text">${kcSanitize(message.summary)?no_esc}</span>
                            </div>
                        </#if>

                        <!-- Username/Email Field -->
                        <div class="form-group">
                            <label for="username" class="${properties.kcLabelClass!}">
                                <#if !realm.loginWithEmailAllowed>
                                    ${msg("username")}
                                <#elseif !realm.registrationEmailAsUsername>
                                    ${msg("usernameOrEmail")}
                                <#else>
                                    ${msg("email")}
                                </#if>
                            </label>
                            <input tabindex="1" id="username" class="${properties.kcInputClass!} <#if messagesPerField.existsError('username','password')>input-error</#if>"
                                   name="username" value="${(login.username!'')}"
                                   placeholder="Enter your email address" type="text" autofocus autocomplete="off"
                                   aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"/>
                        </div>

                        <!-- Password Field -->
                        <div class="form-group">
                            <label for="password" class="${properties.kcLabelClass!}">${msg("password")}</label>
                            <div class="password-input-container">
                                <input tabindex="2" id="password" class="${properties.kcInputClass!} <#if messagesPerField.existsError('username','password')>input-error</#if>"
                                       name="password" type="password" autocomplete="off"
                                       placeholder="Enter your password"
                                       aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"/>
                                <button type="button" class="password-toggle" onclick="togglePasswordVisibility()" aria-label="Toggle password visibility">
                                    <span id="password-toggle-icon">🐵</span>
                                </button>
                            </div>
                        </div>

                        <!-- Remember Me -->
                        <div class="form-group remember-forgot">
                            <#if realm.rememberMe && !usernameEditDisabled??>
                                <div class="checkbox">
                                    <label>
                                        <#if login.rememberMe??>
                                            <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox" checked>
                                        <#else>
                                            <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox">
                                        </#if>
                                        ${msg("rememberMe")}
                                    </label>
                                </div>
                            </#if>
                            <div class="forgot-password">
                                <#if realm.resetPasswordAllowed>
                                    <a tabindex="5" href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a>
                                </#if>
                            </div>
                        </div>

                        <!-- Login Button -->
                        <div class="form-group">
                            <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential??>value="${auth.selectedCredential}"</#if>/>
                            <input tabindex="4" class="submit-btn" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
                        </div>

                        <!-- Social Login -->
                        <#if social.providers??>
                            <div class="social-login">
                                <div class="separator"><span>or continue with</span></div>
                                <#list social.providers as p>
                                    <#if p.alias == "google">
                                        <a href="${p.loginUrl}" class="google-btn">
                                            <span class="social-icon">
                                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48" width="18px" height="18px">
                                                    <path fill="#FFC107" d="M43.611,20.083H42V20H24v8h11.303c-1.649,4.657-6.08,8-11.303,8c-6.627,0-12-5.373-12-12c0-6.627,5.373-12,12-12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C12.955,4,4,12.955,4,24c0,11.045,8.955,20,20,20c11.045,0,20-8.955,20-20C44,22.659,43.862,21.35,43.611,20.083z"/>
                                                    <path fill="#FF3D00" d="M6.306,14.691l6.571,4.819C14.655,15.108,18.961,12,24,12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C16.318,4,9.656,8.337,6.306,14.691z"/>
                                                    <path fill="#4CAF50" d="M24,44c5.166,0,9.86-1.977,13.409-5.192l-6.19-5.238C29.211,35.091,26.715,36,24,36c-5.202,0-9.619-3.317-11.283-7.946l-6.522,5.025C9.505,39.556,16.227,44,24,44z"/>
                                                    <path fill="#1976D2" d="M43.611,20.083H42V20H24v8h11.303c-0.792,2.237-2.231,4.166-4.087,5.571c0.001-0.001,0.002-0.001,0.003-0.002l6.19,5.238C36.971,39.205,44,34,44,24C44,22.659,43.862,21.35,43.611,20.083z"/>
                                                </svg>
                                            </span>
                                            <span>Sign in with Google</span>
                                        </a>
                                    </#if>
                                </#list>
                                <div class="social-login-note" style="font-size:12px;color:#6b7280;margin-top:10px;text-align:center;display:inline-block;white-space:nowrap;">
                                    By logging in, you agree to our
                                    <a href="https://www.convonest.com/terms" target="_blank" style="color:#0061f2;text-decoration:underline;display:inline;">Terms & Conditions</a>
                                    and
                                    <a href="https://www.convonest.com/privacy" target="_blank" style="color:#0061f2;text-decoration:underline;display:inline;">Privacy Policy</a>.
                                </div>
                            </div>
                        </#if>

                        <!-- Registration Link -->
                        <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
                           <div class="register-link" style="text-align:center;margin-top:10px;">
                               <span>New User? <a tabindex="6" href="${url.registrationUrl}">${msg("doRegister")}</a></span>
                           </div>
                        </#if>
                    </div>
                </form>
            </div>
        </div>

        <script>
            function togglePasswordVisibility() {
                const passwordField = document.getElementById('password');
                const toggleIcon = document.getElementById('password-toggle-icon');

                if (passwordField.type === 'password') {
                    passwordField.type = 'text';
                    toggleIcon.textContent = '🙈';
                } else {
                    passwordField.type = 'password';
                    toggleIcon.textContent = '🐵️';
                }
            }
        </script>

        <style>
             /* Add this to hide any unwanted header text */
               #kc-header,
               #kc-header-wrapper {
                   display: none !important;
               }

             body {
                margin: 0;
                padding: 0;
                font-family: Arial, sans-serif;
                background: url('${url.resourcesPath}/img/bg.jpg') no-repeat center center fixed;
                background-size: cover;
                color: #333;
             }

            .login-container {
                max-width: 400px;
                margin: 0 auto;
                padding: 20px;
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }

            .logo {
                max-width: 100px;
            }

            .form-group {
                margin-bottom: 20px;
            }

            .form-group label {
                display: block;
                margin-bottom: 8px;
                font-weight: 500;
                color: #333;
            }

            .form-group input[type="text"],
            .form-group input[type="password"] {
                width: 100%;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 14px;
                box-sizing: border-box;
            }

            /* Error message styles */
            .error-message {
                background-color: #fee;
                border: 1px solid #fcc;
                border-radius: 4px;
                padding: 12px;
                margin-bottom: 20px;
                color: #d00;
                font-size: 14px;
            }

            .success-message {
                background-color: #efe;
                border: 1px solid #cfc;
                border-radius: 4px;
                padding: 12px;
                margin-bottom: 20px;
                color: #060;
                font-size: 14px;
            }

            .warning-message {
                background-color: #ffd;
                border: 1px solid #ffb;
                border-radius: 4px;
                padding: 12px;
                margin-bottom: 20px;
                color: #a60;
                font-size: 14px;
            }

            .info-message {
                background-color: #def;
                border: 1px solid #bdf;
                border-radius: 4px;
                padding: 12px;
                margin-bottom: 20px;
                color: #036;
                font-size: 14px;
            }

            /* Input error styles */
            .input-error {
                border-color: #d00 !important;
                box-shadow: 0 0 0 2px rgba(221, 0, 0, 0.2);
            }

            /* Password input container */
            .password-input-container {
                position: relative;
                display: flex;
                align-items: center;
            }

            .password-input-container input {
                padding-right: 45px;
            }

            .password-toggle {
                position: absolute;
                right: 10px;
                background: none;
                border: none;
                cursor: pointer;
                font-size: 16px;
                padding: 5px;
                border-radius: 3px;
                transition: background-color 0.2s;
            }

            .password-toggle:hover {
                background-color: #f0f0f0;
            }

            .password-toggle:focus {
                outline: 2px solid #4A90E2;
                outline-offset: 1px;
            }

            .remember-forgot {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 20px;
            }

            .checkbox label {
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .submit-btn {
                width: 100%;
                padding: 12px;
                background: #4A90E2;
                color: white;
                border: none;
                border-radius: 4px;
                font-size: 16px;
                cursor: pointer;
                transition: background 0.3s;
            }

            .submit-btn:hover {
                background: #357ABD;
            }

            .register-link {
                text-align: center;
                margin-top: 20px;
            }

            /* Improved social login styles */
            .social-login {
                margin-top: 20px;
                text-align: center;
                width: 100%;
            }

            .separator {
                display: flex;
                align-items: center;
                text-align: center;
                margin: 15px 0;
            }

            .separator::before,
            .separator::after {
                content: '';
                flex: 1;
                border-bottom: 1px solid #e2e8f0;
            }

            .separator span {
                padding: 0 10px;
                color: #718096;
                font-size: 14px;
            }

            /* Improved Google button styling */
            .google-btn {
                display: flex;
                align-items: center;
                justify-content: center;
                width: 100%;
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 4px;
                background: #ffffff;
                color: #333;
                text-decoration: none;
                font-weight: 500;
                transition: background 0.3s, box-shadow 0.3s;
                box-sizing: border-box;
                box-shadow: 0 1px 3px rgba(0,0,0,0.12);
            }

            .google-btn:hover {
                background: #f8f9fa;
                box-shadow: 0 2px 4px rgba(0,0,0,0.2);
            }

            .social-icon {
                display: flex;
                align-items: center;
                justify-content: center;
                margin-right: 10px;
            }
        </style>

    </#if>
</@layout.registrationLayout>