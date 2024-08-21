<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false displayWide=false showAnotherWayIfPresent=true>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" class="${properties.kcHtmlClass!}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Convonest</title>
    <link rel="stylesheet" type="text/css" href="${url.resourcesPath}/css/styles.css" />
</head>
<body class="${properties.kcBodyClass!}">
    <div id="kc-header" class="login-pf-page-header">
        <img src="${url.resourcesPath}/img/company_logo.png" alt="Convonest Logo" class="company-logo" />
        <h1>Convonest</h1>
    </div>
    <div class="${properties.kcFormCardClass!} <#if displayWide>${properties.kcFormCardAccountClass!}</#if>">
        <header class="${properties.kcFormHeaderClass!}">
            <h1 id="kc-page-title"><#nested "header"></h1>
        </header>
        <div id="kc-content">
            <div id="kc-content-wrapper">
                <#nested "form">
            </div>
        </div>
    </div>
</body>
</html>
</#macro>
