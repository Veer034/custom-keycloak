Welcome to <#if user.attributes.companyName?? && user.attributes.companyName?has_content><#if user.attributes.companyName?is_sequence>${user.attributes.companyName?first}<#else>${user.attributes.companyName}</#if><#else>${realmName}</#if>, ${user.firstName!'User'}!

You Have Been Added to the Team
================================

Hi ${user.firstName!'User'},

You have been added to the team at <#if user.attributes.companyName?? && user.attributes.companyName?has_content><#if user.attributes.companyName?is_sequence>${user.attributes.companyName?first}<#else>${user.attributes.companyName}</#if><#else>${realmName}</#if>. To access your account, please verify your email and set your password.

Verify and Set Password:
${link}

⏱ This link will expire in ${linkExpirationFormatted}.

If you did not expect this email, please ignore it.

----------------------
Email: ${user.email}
Verification Link: ${link}
----------------------

---
Powered by Convonest - Customer Engagement Platform
www.convonest.com