<!-- verify-email.ftl -->
<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "header">
    <#elseif section = "form">
        <div class="login-container">
            <!-- Logo -->
            <div class="logo-container">
                <img src="${url.resourcesPath}/img/company_logo.png" alt="Convonest Logo" class="logo">
                <h2 class="title">Email Verification</h2>
                <p class="subtitle">Complete Your Account Setup</p>
            </div>

            <div class="verification-content">
                <p class="instruction">Please verify your email to activate your account.</p>
                <div class="action-button">
                    <a href="${url.loginAction}" class="submit-btn">Verify Email</a>
                </div>
            </div>
        </div>

        <style>
            /* Base styles */
            body {
                margin: 0;
                padding: 0;
                font-family: Arial, sans-serif;
                background: url('${url.resourcesPath}/img/bg.jpg') no-repeat center center fixed;
                background-size: cover;
                color: #333;
            }

            /* Container styles */
            .login-container {
                width: 100% !important;
                max-width: 500px !important;
                margin: 0 auto !important;
                padding: 20px !important;
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }

            /* Logo styles */
            .logo-container {
                text-align: center !important;
                margin-bottom: 24px !important;
            }

            .logo {
                max-width: 120px !important;
                margin-bottom: 16px !important;
            }

            .title {
                font-size: 24px;
                margin: 0 0 8px 0;
            }

            .subtitle {
                color: #666;
                margin: 0;
            }

            /* Content styles */
            .verification-content {
                text-align: center;
                padding: 20px 0;
            }

            .instruction {
                margin-bottom: 24px;
                font-size: 16px;
            }

            .submit-btn {
                display: inline-block;
                width: auto !important;
                min-width: 200px;
                height: 40px !important;
                line-height: 40px;
                background: #0061f2 !important;
                color: white !important;
                text-decoration: none;
                border: none !important;
                border-radius: 6px !important;
                font-size: 14px !important;
                font-weight: 500 !important;
                cursor: pointer !important;
                transition: background-color 0.2s !important;
            }

            .submit-btn:hover {
                background: #0052cc !important;
            }
        </style>
    </#if>
</@layout.registrationLayout>
