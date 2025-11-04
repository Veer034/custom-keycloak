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
        /* Button styles */
        a.reset-button,
        a.reset-button:link,
        a.reset-button:visited,
        a.reset-button:hover,
        a.reset-button:active {
            display: inline-block;
            background-color: #0052cc;
            color: #ffffff !important;
            text-decoration: none !important;
            padding: 12px 20px;
            border-radius: 5px;
            font-size: 16px;
            font-weight: bold;
            margin-top: 20px;
        }
        a.reset-button:hover {
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
        .meta-info a {
            color: #0052cc !important;
            text-decoration: none;
            word-break: break-all;
        }
        .meta-info a:hover {
            text-decoration: underline;
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
            Password Reset Request
        </div>

        <!-- Content Section -->
        <div class="content">
            <h2>Reset Your Password</h2>
            <p>Hi ${userName!'there'},</p>
            <p>We received a request to reset the password for your account.</p>
            <p>If you made this request, click the button below to reset your password:</p>

            <!-- Button with table structure for better email client support -->
            <table border="0" cellspacing="0" cellpadding="0" style="margin-top: 20px;">
                <tr>
                    <td align="center" style="border-radius: 5px; background-color: #0052cc;">
                        <a href="${link}" style="display: inline-block; padding: 12px 20px; font-family: Arial, sans-serif; font-size: 16px; font-weight: bold; color: #ffffff !important; text-decoration: none !important; border-radius: 5px;">
                            Reset Password
                        </a>
                    </td>
                </tr>
            </table>

            <p style="margin-top: 20px;">Or copy and paste this link into your browser:</p>
            <div class="meta-info">
                <p><a href="${link}" style="color: #0052cc !important; text-decoration: none;">${link}</a></p>
                <p style="margin-top: 10px;"><strong>⏱ This link will expire in ${linkExpirationFormatted}.</strong></p>
            </div>

            <p style="margin-top: 20px; color: #d32f2f; font-weight: bold;">⚠️ Important:</p>
            <p style="color: #666;">If you did not request a password reset, please ignore this email. Your password will remain unchanged and your account is secure.</p>
        </div>

        <!-- Footer Section -->
        <div class="footer">
            <p>Powered by <strong>Convonest</strong> - Customer Engagement Platform</p>
            <p><a href="https://convonest.com">www.convonest.com</a></p>
        </div>
    </div>
</body>
</html>