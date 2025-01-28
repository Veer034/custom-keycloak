<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=false; section>
    <#if section == "header">
         <!-- No Data -->
    <#elseif section == "form">
        <div id="kc-form">
            <div id="kc-form-wrapper">
                <form id="kc-forgot-password" onsubmit="forgotPassword.disabled = true; return true;" action="${url.loginResetCredentialsUrl}" method="post">
                    <div class="login-container">
                        <!-- Logo -->
                        <div class="logo-container">
                            <img src="${url.resourcesPath}/img/company_logo.png" alt="Convonest Logo" class="logo">
                            <h2>Forgot Your Password?</h2>
                            <p>Enter your email address to reset your password</p>
                        </div>

                        <!-- Error Message -->
                        <#if message?has_content>
                            <div id="error-message" style="color: red; text-align: center; margin-bottom: 15px;">
                                ${messageSummary}
                            </div>
                        </#if>

                        <!-- Email Field -->
                        <div class="form-group">
                            <label for="username" class="${properties.kcLabelClass!}">${msg("email")}</label>
                            <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username"
                                   value="${(auth.attemptedUsername!'')}" placeholder="Enter your email address"
                                   type="email" autofocus autocomplete="off"
                                   aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"/>
                        </div>

                        <!-- Submit Button -->
                        <div class="form-group">
                            <input tabindex="2" class="submit-btn" name="forgotPassword" id="kc-forgot-password-submit" type="submit" value="Reset Password"/>
                        </div>

                        <!-- Back to Login Link -->
                        <div class="register-link">
                            <span><a tabindex="3" href="${url.loginUrl}">Back to Login</a></span>
                        </div>
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

            .form-group input[type="email"] {
                width: 100%;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 14px;
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