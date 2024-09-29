<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${url.themeResource('style.css')}">
</head>
<body>
    <div class="container">
        <div class="header">
            <img src="https://yourcompany.com/logo.png" alt="Company Logo">
            <h1>Welcome to Your Company</h1>
        </div>
        <div class="content">
            <h2>Hello ${user.firstName} ${user.lastName},</h2>
            <p>We are excited to welcome you to our platform. To get started, please click the link below to set up your account:</p>
            <p><a href="${link}" class="button">Set Up Account</a></p>
            <p>If you did not expect this email, please ignore it.</p>
        </div>
        <div class="footer">
            <p>Best Regards,</p>
            <p>Your Company Name</p>
            <p>&copy; 2024 Your Company Name. All rights reserved.</p>
        </div>
    </div>
</body>
</html>
