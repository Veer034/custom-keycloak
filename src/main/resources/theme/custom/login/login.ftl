<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
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
                            <input tabindex="1" id="username" class="${properties.kcInputClass!}"
                                   name="username" value="${(login.username!'')}"
                                   placeholder="Enter your email address" type="text" autofocus autocomplete="off"
                                   aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"/>
                        </div>

                        <!-- Password Field -->
                        <div class="form-group">
                            <label for="password" class="${properties.kcLabelClass!}">${msg("password")}</label>
                            <input tabindex="2" id="password" class="${properties.kcInputClass!}"
                                   name="password" type="password" autocomplete="off"
                                   placeholder="Enter your password"
                                   aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"/>
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

                        <!-- Registration Link -->
                        <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
                            <div class="register-link">
                                <span>${msg("noAccount")} <a tabindex="6" href="${url.registrationUrl}">${msg("doRegister")}</a></span>
                            </div>
                        </#if>
                    </div>
                </form>
            </div>
        </div>


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

            .logo-container {
                text-align: center;
                margin-bottom: 30px;
            }

            .logo {
                max-width: 150px;
                margin-bottom: 15px;
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


        </style>

    </#if>
</@layout.registrationLayout>