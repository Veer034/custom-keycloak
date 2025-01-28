<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "header">
        <h1 style="color: red;">Custom Theme Test</h1>
    <#elseif section = "form">
        <div style="background: yellow; padding: 20px;">
            <p>This is a test of the custom theme</p>
            <form id="kc-verify-form" action="${url.loginAction}" method="POST">
                <input type="hidden" id="email" name="email" value="${email}"/>
                <button type="submit">Verify Email</button>
            </form>
        </div>
    </#if>
</@layout.registrationLayout>