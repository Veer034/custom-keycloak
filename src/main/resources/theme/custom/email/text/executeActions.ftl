Welcome to <#if user.attributes.companyName?? && user.attributes.companyName??>${user.attributes.companyName}<#else>Convonest</#if>, ${user.firstName!'User'}!

You Have Been Added to the Team

Hi ${user.firstName!'User'},


You have been added to the team at <#if user.attributes.companyName?? && user.attributes.companyName??>${user.attributes.companyName}<#else></#if> To access your account, please verify your email and set your password.

Verify and Set Password: ${link}

If you did not expect this email, please ignore it.

----------------------
Email: ${user.email}
Verification Link: ${link}
----------------------

Need help? Contact Support: https://convonest.com/#contact  

This email was sent by Convonest Tech Pvt Ltd, ensuring secure access to your account.

Visit our website: https://convonest.com
