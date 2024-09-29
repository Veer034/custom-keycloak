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
            <h1>Update Your Password</h1>
        </div>
        <div class="content">
            <h2>Hello,</h2>
            <p>Your administrator has requested that you update your password. Please click the link below to start this process:</p>
            <p><a href="${link}" class="button">Update Password</a></p>
            <p>This link will expire in ${linkExpirationInMinutes} minutes.</p>
            <p>If you did not request this, please ignore this email.</p>
        </div>
        <div class="footer">
            <p>Best Regards,</p>
            <p>Your Company Name</p>
            <p>&copy; 2024 Your Company Name. All rights reserved.</p>
        </div>
    </div>
</body>
</html>
