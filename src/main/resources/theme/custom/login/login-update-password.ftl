<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false displayInfo=false; section>
    <#if section = "header">
       <!-- No Data -->
    <#elseif section = "form">
        <div id="kc-form">
            <div id="kc-form-wrapper">
                <form id="kc-passwd-update-form" action="${url.loginAction}" method="post">
                    <div class="login-container">
                        <!-- Logo -->
                        <div class="logo-container">
                            <img src="${url.resourcesPath}/img/company_logo.png" alt="Convonest Logo" class="logo">
                            <h2>Set Your Password</h2>
                            <p>Complete Your Account Setup</p>
                        </div>

                        <!-- New Password Field -->
                        <div class="form-group">
                            <label for="password-new">New Password</label>
                            <input type="password" id="password-new" name="password-new"
                                   class="${properties.kcInputClass!}"
                                   autofocus autocomplete="new-password"
                                   placeholder="Enter new password"/>
                        </div>

                        <!-- Confirm Password Field -->
                        <div class="form-group">
                            <label for="password-confirm">Confirm Password</label>
                            <input type="password" id="password-confirm" name="password-confirm"
                                   class="${properties.kcInputClass!}" autocomplete="new-password"
                                   placeholder="Confirm new password"/>
                        </div>
                        <!-- Submit Button -->
                        <div class="form-group">
                            <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential??>value="${auth.selectedCredential}"</#if>/>
                            <input class="submit-btn" name="login" id="kc-login" type="submit" value="Update Password"/>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <style>
            /* Hide all Keycloak messages */
            .alert-error, .alert-success, .alert-info, .alert-warning,
            #kc-info-message, #kc-feedback-wrapper, #kc-info, #kc-feedback {
                display: none !important;
            }

            /* Add this to hide any unwanted header text */
            #kc-header,
            #kc-header-wrapper,
            .message-text {
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

            /* Responsive design */
            @media (max-width: 768px) {
                .login-container {
                    margin: 0 15px;
                }
            }
        </style>
    </#if>
</@layout.registrationLayout>