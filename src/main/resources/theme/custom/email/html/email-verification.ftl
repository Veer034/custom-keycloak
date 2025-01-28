<!DOCTYPE html>
<html>
<head>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            margin: 0;
            padding: 0;
        }
        .email-container {
            max-width: 600px;
            margin: 20px auto;
            border: 1px solid #ddd;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .header {
            background-color: #0052cc;
            color: white;
            text-align: center;
            padding: 20px;
            font-size: 24px;
        }
        .content {
            padding: 20px;
        }
        .content h2 {
            font-size: 20px;
            color: #0052cc;
        }
        .content p {
            margin: 15px 0;
            color: #555;
            font-size: 14px;
        }
        .verify-button {
            display: inline-block;
            background-color: #0052cc;
            color: white;
            text-decoration: none;
            padding: 12px 20px;
            border-radius: 5px;
            font-size: 16px;
            font-weight: bold;
            margin-top: 20px;
        }
        .verify-button:hover {
            background-color: #0041a8;
        }
        .meta-info {
            margin-top: 20px;
            padding: 15px;
            background-color: #f4f5f7;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            color: #333;
        }
        .meta-info p {
            margin: 5px 0;
        }
        .footer {
            text-align: center;
            font-size: 12px;
            color: #666;
            padding: 20px;
            border-top: 1px solid #ddd;
            background-color: #f9f9f9;
        }
        .footer a {
            color: #0052cc;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="email-container">
        <!-- Header Section -->
        <div class="header">
            ${userName} - Verify Your Email
        </div>

        <!-- Content Section -->
        <div class="content">
            <h2>Verify Your Email Address</h2>
            <p>Hi ${userName},</p>
            <p>Thank you for signing up for Convonest Analytics Platform. Please click the button below to verify your
            email address and activate your account within 24 hours:</p>
            <a href="${link}" class="verify-button">Verify My Email</a>
            <p>If you didn’t sign up for this account, you can safely ignore this email.</p>
            <div class="meta-info">
                <p><strong>Account:</strong> ${email}</p>
                <p><strong>Verification Link:</strong> <a href="${link}" style="color: #0052cc;">${link}</a></p>
            </div>
        </div>

        <!-- Footer Section -->
        <div class="footer">
            <p>Need help? <a href="https://convonest.com/#contact">Contact Support</a></p>
            <p>This email was sent by <strong>Convonest Tech Pvt Ltd</strong>, ensuring secure access to your account.</p>
            <p><a href="https://www.convonest.com">Visit our website</a></p>
        </div>
    </div>
</body>
</html>
