<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username') displayInfo=false; section>
    <#if section == "form">
        <div id="kc-form">
            <div id="kc-form-wrapper">
                <form id="kc-reset-password-form" action="${url.loginAction}" method="post">
                    <div class="login-container">
                        <!-- Logo -->
                        <div class="logo-container">
                            <img src="${url.resourcesPath}/img/company_logo.png" alt="Convonest Logo" class="logo">
                            <h2>Forgot Your Password?</h2>
                            <p>Enter your registered email address to receive a password reset link.</p>
                        </div>

                        <!-- Message -->
                        <#if message?has_content>
                            <div id="error-message" style="color:red; text-align:center; margin-bottom:15px;">
                                ${messageSummary}
                            </div>
                        </#if>

                        <!-- Email Field -->
                        <div class="form-group">
                            <label for="username">${msg("email")}</label>
                            <input id="username" name="username" type="email"
                                   value="${(auth.attemptedUsername!'')}"
                                   placeholder="Enter your email address"
                                   autofocus autocomplete="off" required
                                   aria-invalid="<#if messagesPerField.existsError('username')>true</#if>" />
                            <#if messagesPerField.existsError('username')>
                                <span class="error-text">${messagesPerField.get('username')}</span>
                            </#if>
                        </div>

                        <!-- Submit -->
                        <div class="form-group">
                            <input class="submit-btn" id="kc-reset-password-submit" type="submit" value="Send Reset Link" />
                        </div>

                        <!-- Back to Login -->
                        <div class="register-link">
                            <a href="${url.loginUrl}">Back to Login</a>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <style>
            /* Hide default Keycloak header */
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
                margin: 60px auto;
                padding: 25px;
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }

            .logo {
                max-width: 100px;
                display: block;
                margin: 0 auto 10px;
            }

            h2 {
                text-align: center;
                margin-bottom: 5px;
            }

            p {
                text-align: center;
                margin-bottom: 20px;
                color: #666;
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

            .form-group input[type="email"] {
                width: 100%;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 14px;
            }

            .error-text {
                color: red;
                font-size: 13px;
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

            .register-link a {
                color: #4A90E2;
                text-decoration: none;
            }

            .register-link a:hover {
                text-decoration: underline;
            }
        </style>
    </#if>
</@layout.registrationLayout>
