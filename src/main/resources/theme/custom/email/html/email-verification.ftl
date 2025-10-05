<#ftl output_format="HTML">
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email Verification - Convonest Analytics Platform</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: #333;
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
        }
        .email-container {
            max-width: 650px;
            margin: 30px auto;
            background: white;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        }
        .company-branding {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            text-align: center;
            padding: 20px 30px;
            border-bottom: 3px solid #ffffff20;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 20px;
        }
        .company-logo {
            position: relative;
            display: flex;
            align-items: center;
            justify-content: center;
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%);
            color: white;
            font-weight: bold;
            font-size: 20px;
            letter-spacing: -0.5px;
            box-shadow: 0 4px 15px rgba(37, 99, 235, 0.3);
            flex-shrink: 0;
        }
        .company-logo::before {
            content: '';
            position: absolute;
            inset: -2px;
            border-radius: 50%;
            border: 2px solid rgba(147, 197, 253, 0.4);
            animation: pulse 2s infinite;
        }
        .company-text {
            font-size: 24px;
            font-weight: 700;
            letter-spacing: 1.2px;
            background: linear-gradient(135deg, #ffffff 0%, #e2e8f0 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            line-height: 1;
            display: flex;
            align-items: center;
        }
        @keyframes pulse {
            0%, 100% {
                opacity: 1;
                transform: scale(1);
            }
            50% {
                opacity: 0.7;
                transform: scale(1.05);
            }
        }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            text-align: center;
            padding: 30px;
        }
        .header-icon {
            font-size: 40px;
            margin-bottom: 15px;
            display: block;
        }
        .header-title {
            font-size: 24px;
            font-weight: 600;
            margin-bottom: 8px;
        }
        .header-subtitle {
            font-size: 16px;
            opacity: 0.9;
            font-weight: 300;
        }
        .content {
            padding: 35px;
        }
        .greeting-section {
            text-align: center;
            margin-bottom: 30px;
        }
        .greeting-text {
            font-size: 18px;
            color: #2c3e50;
            margin-bottom: 15px;
            line-height: 1.5;
        }
        .verification-message {
            background: linear-gradient(135deg, #f8f9ff 0%, #f0f4ff 100%);
            padding: 25px;
            border-radius: 12px;
            border-left: 5px solid #667eea;
            margin: 25px 0;
            position: relative;
        }
        .verification-message::before {
            content: '✉️';
            font-size: 40px;
            position: absolute;
            top: 15px;
            right: 20px;
            opacity: 0.6;
        }
        .verification-text {
            font-size: 16px;
            color: #34495e;
            margin-bottom: 20px;
            line-height: 1.6;
        }
        .verify-button-container {
            text-align: center;
            margin: 30px 0;
        }
        .verify-button {
            display: inline-block;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
            color: #ffffff !important;
            text-decoration: none !important;
            padding: 15px 35px;
            border-radius: 25px;
            font-size: 16px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
            transition: all 0.3s ease;
            border: none;
            cursor: pointer;
        }
        .verify-button:hover {
            background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%) !important;
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
            color: #ffffff !important;
            text-decoration: none !important;
        }
        .verify-button:visited {
            color: #ffffff !important;
            text-decoration: none !important;
        }
        .verify-button:active {
            color: #ffffff !important;
            text-decoration: none !important;
        }
        .success-badge {
            background: linear-gradient(135deg, #00b894 0%, #00a085 100%);
            color: white;
            padding: 12px 25px;
            border-radius: 25px;
            font-size: 14px;
            font-weight: 600;
            display: inline-block;
            margin: 20px 0;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .meta-info {
            background-color: #f8f9fa;
            padding: 25px;
            border-radius: 12px;
            border: 2px solid #e9ecef;
            margin: 25px 0;
        }
        .meta-title {
            font-size: 18px;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 15px;
            text-align: center;
        }
        .meta-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #dee2e6;
        }
        .meta-item:last-child {
            border-bottom: none;
        }
        .meta-label {
            font-weight: 600;
            color: #6c757d;
            font-size: 14px;
        }
        .meta-value {
            color: #212529;
            font-size: 14px;
            word-break: break-all;
        }
        .meta-value a {
            color: #667eea !important;
            text-decoration: none !important;
        }
        .meta-value a:hover {
            color: #5a6fd8 !important;
            text-decoration: underline !important;
        }
        .warning-section {
            background: linear-gradient(135deg, #fff3e0 0%, #fef7ed 100%);
            padding: 20px;
            border-radius: 8px;
            border-left: 4px solid #ff9800;
            margin: 25px 0;
        }
        .warning-title {
            font-size: 16px;
            font-weight: 600;
            color: #e65100;
            margin-bottom: 10px;
        }
        .warning-content {
            color: #bf360c;
            font-size: 14px;
            line-height: 1.6;
        }
        .next-steps {
            background: linear-gradient(135deg, #e3f2fd 0%, #f3e5f5 100%);
            padding: 25px;
            border-radius: 10px;
            margin: 25px 0;
            border: 2px solid #90caf9;
        }
        .next-steps-title {
            font-size: 18px;
            font-weight: 600;
            color: #1565c0;
            margin-bottom: 15px;
            text-align: center;
        }
        .steps-list {
            color: #0d47a1;
            font-size: 15px;
            line-height: 1.8;
            margin: 0;
            padding-left: 20px;
        }
        .steps-list li {
            margin-bottom: 8px;
        }
        .contact-section {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
            margin: 25px 0;
            border: 1px solid #dee2e6;
        }
        .contact-title {
            font-size: 16px;
            font-weight: 600;
            color: #495057;
            margin-bottom: 15px;
        }
        .contact-info {
            display: flex;
            justify-content: center;
            gap: 20px;
            flex-wrap: wrap;
        }
        .contact-item {
            background: #667eea;
            color: white !important;
            padding: 10px 20px;
            border-radius: 25px;
            font-size: 14px;
            font-weight: 500;
            text-decoration: none !important;
        }
        .contact-item:hover {
            background: #5a6fd8;
            color: white !important;
            text-decoration: none !important;
        }
        .footer {
            background-color: #2c3e50;
            color: #bdc3c7;
            text-align: center;
            padding: 25px;
            font-size: 13px;
        }
        .footer p {
            margin: 8px 0;
        }
        .company-name {
            color: #667eea;
            font-weight: 600;
        }
        .footer a {
            color: #667eea !important;
            text-decoration: none !important;
        }
        .footer a:hover {
            color: #93c5fd !important;
            text-decoration: underline !important;
        }

        @media (max-width: 600px) {
            .company-branding {
                flex-direction: column;
                gap: 15px;
            }
            .company-text {
                font-size: 18px;
            }
            .contact-info {
                flex-direction: column;
                align-items: center;
            }
            .content {
                padding: 20px;
            }
            .meta-item {
                flex-direction: column;
                align-items: flex-start;
                gap: 5px;
            }
        }
    </style>
</head>
<body>
    <div class="email-container">
        <!-- Company Branding -->
        <div class="company-branding">
            <div class="company-logo">
                CN
            </div>
            <div class="company-text">
                CONVONEST TECH PVT LTD
            </div>
        </div>

        <!-- Header Section -->
        <div class="header">
            <span class="header-icon">🔐</span>
            <div class="header-title">Welcome to Convonest Analytics Platform!</div>
            <div class="header-subtitle">Verify Your Email Address</div>
        </div>

        <!-- Content Section -->
        <div class="content">
            <div class="greeting-section">
                <div class="greeting-text">
                    Hello <strong>${userName}</strong>,
                </div>
                <div class="success-badge">✉️ Email Verification Required</div>
            </div>

            <div class="verification-message">
                <div class="verification-text">
                    Thank you for signing up for Convonest Analytics Platform. Please click the button below to verify your email address and activate your account within ${linkExpiration / 60} hours:
                </div>

                <div class="verify-button-container">
                    <a href="${link}" class="verify-button">
                        🔓 Verify My Email
                    </a>
                </div>
            </div>

            <div class="warning-section">
                <div class="warning-title">⚠️ Important Security Notice</div>
                <div class="warning-content">
                    If you didn't sign up for this account, you can safely ignore this email. This verification link
                    will expire in ${linkExpiration / 60} hours for your security.
                </div>
            </div>

            <div class="meta-info">
                <div class="meta-title">📋 Verification Details</div>
                <div class="meta-item">
                    <div class="meta-label">👤 User:</div>
                    <div class="meta-value">${userName}</div>
                </div>
                <div class="meta-item">
                    <div class="meta-label">📧 Email Address:</div>
                    <div class="meta-value">${email}</div>
                </div>
                <div class="meta-item">
                    <div class="meta-label">⏰ Link Expires In:</div>
                    <div class="meta-value">${linkExpiration / 60} hours</div>
                </div>
                <div class="meta-item">
                    <div class="meta-label">🔗 Verification Link:</div>
                    <div class="meta-value">
                        <a href="${link}" style="color: #667eea !important; text-decoration: none !important;">
                            Click here to verify
                        </a>
                    </div>
                </div>
            </div>

            <div class="next-steps">
                <div class="next-steps-title">🚀 What Happens Next?</div>
                <ul class="steps-list">
                    <li><strong>Click the Verification Button:</strong> Use the blue button above to verify your email address</li>
                    <li><strong>Account Activation:</strong> Your account will be activated immediately upon verification</li>
                    <li><strong>Access Granted:</strong> You'll be able to access all Convonest Analytics Platform features</li>
                </ul>
            </div>

            <div class="contact-section">
                <div class="contact-title">Need help with verification?</div>
                <div class="contact-info">
                    <a href="mailto:support@convonest.com" class="contact-item">📧 Email Support</a>
                    <a href="tel:+918884161249" class="contact-item">📞 Call Support</a>
                </div>
            </div>
        </div>

        <!-- Footer Section -->
        <div class="footer">
            <p><strong>Thank you for choosing <span class="company-name">Convonest Tech Pvt Ltd</span></strong></p>
            <p>This verification email was sent to ${email} for account security purposes</p>
            <p>Need assistance? <a href="https://convonest.com/#contact">Contact our support team</a></p>
            <p><a href="https://www.convonest.com">Visit our website</a> | <a href="https://convonest.com/privacy">Privacy Policy</a></p>
            <p>&copy; 2025 Convonest Tech Pvt Ltd. All rights reserved.</p>
        </div>
    </div>
</body>
</html>