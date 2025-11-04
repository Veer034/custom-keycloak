<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false displayInfo=false; section>
<#if section = "header">
<#elseif section = "form">
<div id="kc-form">
    <div id="kc-form-wrapper">
        <form id="kc-passwd-update-form" onsubmit="return validateAndSubmit(event);" action="${url.loginAction}" method="post">
            <div class="login-container">
                <!-- Logo + title -->
                <div class="logo-container">
                    <img src="${url.resourcesPath}/img/company_logo.png" alt="Convonest Logo" class="logo">
                    <h2>Set Your Password</h2>
                    <p>Complete Your Account Setup</p>
                </div>

                <!-- Error -->
                <div id="errorContainer" class="error-container" style="display: none;">
                    <div class="error-message"></div>
                </div>

                <!-- Password -->
                <div class="form-group password-field">
                    <label for="password-new">New Password</label>
                    <div class="password-input-wrapper">
                        <input type="password" id="password-new" name="password-new" class="form-control"
                               placeholder="Enter new password"
                               oninput="validatePasswordStrength(); checkPasswordMatch(); checkFormValidity();" required />
                        <span class="toggle-password" onclick="togglePasswordVisibility('password-new', this)">
                            <svg xmlns="http://www.w3.org/2000/svg" class="eye-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                      d="M1.5 12s4-7.5 10.5-7.5S22.5 12 22.5 12s-4 7.5-10.5 7.5S1.5 12 1.5 12z"/>
                                <circle cx="12" cy="12" r="3" />
                            </svg>
                        </span>

                    </div>
                    <div id="password-strength" class="password-strength"></div>
                    <ul id="password-requirements" class="password-requirements">
                        <li id="req-length">Minimum 8 characters</li>
                        <li id="req-uppercase">At least 1 uppercase letter</li>
                        <li id="req-lowercase">At least 1 lowercase letter</li>
                        <li id="req-number">At least 1 number</li>
                        <li id="req-special">At least 1 special character</li>
                    </ul>
                </div>

                <!-- Confirm Password -->
                <div class="form-group password-field">
                    <label for="password-confirm">Confirm Password</label>
                    <div class="password-input-wrapper">
                        <input type="password" id="password-confirm" name="password-confirm" class="form-control"
                               placeholder="Confirm new password"
                               oninput="checkPasswordMatch(); checkFormValidity();" required />
                        <span class="toggle-password" onclick="togglePasswordVisibility('password-confirm', this)">
                            <svg xmlns="http://www.w3.org/2000/svg" class="eye-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                      d="M1.5 12s4-7.5 10.5-7.5S22.5 12 22.5 12s-4 7.5-10.5 7.5S1.5 12 1.5 12z"/>
                                <circle cx="12" cy="12" r="3" />
                            </svg>
                        </span>

                    </div>
                    <div id="password-match-message" class="password-match-message"></div>
                </div>

                <!-- Hidden -->
                <input type="hidden" id="id-hidden-input" name="credentialId"
                       <#if auth.selectedCredential??>value="${auth.selectedCredential}"</#if> />

                <!-- Submit -->
                <div class="form-group">
                    <button class="submit-btn disabled" id="kc-login" type="submit" disabled>Update Password</button>
                    <div id="form-validation-message" class="form-validation-message">
                        Please meet password requirements and confirm your password
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<style>
    body {
        margin: 0; padding: 0;
        font-family: Arial, sans-serif;
        background: url('${url.resourcesPath}/img/bg.jpg') no-repeat center center fixed;
        background-size: cover;
        color: #333;
    }
    #kc-header, #kc-header-wrapper { display: none !important; }

    .login-container {
        max-width: 400px;
        margin: 50px auto;
        padding: 25px;
        background: #fff;
        border-radius: 10px;
        box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    }
    .logo { max-width: 90px; }
    .form-group { margin-bottom: 15px; }

    .password-input-wrapper {
        position: relative;
    }

    .toggle-password {
        position: absolute;
        right: 10px;
        top: 50%;
        transform: translateY(-50%);
        cursor: pointer;
        user-select: none;
        font-size: 18px;
        color: #666;
    }

    .toggle-password:hover .eye-icon {
        color: #111;
    }


    .form-control {
        width: 100%;
        padding: 10px;
        padding-right: 35px;
        border: 1px solid #ccc;
        border-radius: 6px;
        font-size: 14px;
    }

    .submit-btn {
        width: 100%;
        padding: 10px;
        background: #0061f2;
        color: white;
        border: none;
        border-radius: 6px;
        font-size: 16px;
        cursor: pointer;
    }
    .submit-btn.disabled {
        background-color: #9ca3af;
        cursor: not-allowed;
        opacity: 0.7;
    }

    .password-strength { font-weight: bold; margin-top: 5px; }
    .weak { color: #dc2626; }
    .medium { color: #d97706; }
    .strong { color: #16a34a; }

    .password-requirements { margin: 5px 0; padding-left: 20px; font-size: 13px; color: #555; }
    .password-requirements li.met { color: #16a34a; font-weight: bold; }

    .password-match-message { font-size: 13px; margin-top: 5px; }
    .match { color: #16a34a; }
    .mismatch { color: #dc2626; }

    .form-validation-message {
        font-size: 12px;
        color: #dc2626;
        margin-top: 5px;
        text-align: center;
    }

    .eye-icon {
        width: 20px;
        height: 20px;
        color: #666;
        transition: color 0.2s ease;
    }

    .error-container {
        background: #fee2e2;
        border: 1px solid #ef4444;
        padding: 8px;
        border-radius: 4px;
        margin-bottom: 10px;
    }
</style>

<script>
    let passwordRequirementsMet = false;
    let passwordsMatch = false;

    function togglePasswordVisibility(id, iconSpan) {
        const input = document.getElementById(id);
        const isPassword = input.type === "password";
        input.type = isPassword ? "text" : "password";

        // Toggle SVG icon
        iconSpan.innerHTML = isPassword
            ? `<svg xmlns="http://www.w3.org/2000/svg" class="eye-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                   <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                         d="M13.875 18.825A10.05 10.05 0 0 1 12 19.5c-6.5 0-10.5-7.5-10.5-7.5a18.96 18.96 0 0 1 3.243-3.903m3.543-2.388A9.982 9.982 0 0 1 12 4.5c6.5 0 10.5 7.5 10.5 7.5a18.933 18.933 0 0 1-3.167 3.766M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0z" />
               </svg>`
            : `<svg xmlns="http://www.w3.org/2000/svg" class="eye-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                   <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                         d="M1.5 12s4-7.5 10.5-7.5S22.5 12 22.5 12s-4 7.5-10.5 7.5S1.5 12 1.5 12z"/>
                   <circle cx="12" cy="12" r="3" />
               </svg>`;
    }


    function updateRequirement(id, met) {
        const el = document.getElementById(id);
        met ? el.classList.add("met") : el.classList.remove("met");
    }

    function validatePasswordStrength() {
        const password = document.getElementById("password-new").value;

        const hasLength = password.length >= 8;
        const hasUpper = /[A-Z]/.test(password);
        const hasLower = /[a-z]/.test(password);
        const hasNumber = /[0-9]/.test(password);
        const hasSpecial = /[^A-Za-z0-9]/.test(password);

        updateRequirement("req-length", hasLength);
        updateRequirement("req-uppercase", hasUpper);
        updateRequirement("req-lowercase", hasLower);
        updateRequirement("req-number", hasNumber);
        updateRequirement("req-special", hasSpecial);

        passwordRequirementsMet = hasLength && hasUpper && hasLower && hasNumber && hasSpecial;

        const strength = document.getElementById("password-strength");
        const score = [hasLength, hasUpper, hasLower, hasNumber, hasSpecial].filter(Boolean).length;
        if (score <= 2) {
            strength.textContent = "Strength: Weak";
            strength.className = "password-strength weak";
        } else if (score === 3 || score === 4) {
            strength.textContent = "Strength: Medium";
            strength.className = "password-strength medium";
        } else {
            strength.textContent = "Strength: Strong";
            strength.className = "password-strength strong";
        }
    }

    function checkPasswordMatch() {
        const pass = document.getElementById("password-new").value;
        const confirm = document.getElementById("password-confirm").value;
        const msg = document.getElementById("password-match-message");

        if (!confirm) {
            msg.textContent = "";
            passwordsMatch = false;
            return;
        }

        if (pass === confirm) {
            msg.textContent = "Passwords match";
            msg.className = "password-match-message match";
            passwordsMatch = true;
        } else {
            msg.textContent = "Passwords do not match";
            msg.className = "password-match-message mismatch";
            passwordsMatch = false;
        }
    }

    function checkFormValidity() {
        const btn = document.getElementById("kc-login");
        const msg = document.getElementById("form-validation-message");
        const valid = passwordRequirementsMet && passwordsMatch;

        btn.disabled = !valid;
        btn.classList.toggle("disabled", !valid);
        msg.style.display = valid ? "none" : "block";
    }

    function showError(message) {
        const errorContainer = document.getElementById('errorContainer');
        errorContainer.querySelector('.error-message').textContent = message;
        errorContainer.style.display = 'block';
    }

    function validateAndSubmit(e) {
        if (!passwordRequirementsMet || !passwordsMatch) {
            e.preventDefault();
            showError("Please ensure your password meets all requirements and both fields match.");
            return false;
        }
        return true;
    }
</script>
</#if>
</@layout.registrationLayout>
