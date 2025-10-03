<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo displayMessage=!messagesPerField.existsError('username','password') displayRequiredFields=false; section>
    <#if section == "header">
        <!-- No Data -->
    <#elseif section == "form">
        <div id="kc-form">
            <div id="kc-form-wrapper">
                <form id="kc-register-form" onsubmit="return registerUser(event);" method="post">
                    <div class="login-container">
                        <!-- Logo -->
                        <div class="logo-container">
                            <img src="${url.resourcesPath}/img/company_logo.png" alt="Convonest Logo" class="logo">
                            <h2 class="title">Create Your Account</h2>
                            <p class="subtitle">Join Convonest Analytics</p>
                        </div>

                        <!-- Error message container at top -->
                        <div id="errorContainer" class="error-container" style="display: none;">
                            <div class="error-message"></div>
                        </div>

                        <!-- Loading message container at top -->
                        <div id="loadingContainer" class="loading-container" style="display: none;">
                            <div class="loading-message">Processing your registration... Please wait</div>
                        </div>

                        <div class="form-grid">
                            <!-- Company Name -->
                            <div class="form-group full-width">
                                <label for="companyName">Company Name</label>
                                <input type="text" id="companyName" class="form-control" name="companyName"
                                       value="${(register.formData.companyName!'')}" required
                                       placeholder="Enter your company name" tabindex="1"/>
                            </div>

                            <!-- Personal Info Row -->
                            <div class="form-row three-columns">
                                <div class="form-group">
                                    <label for="firstName">${msg("firstName")}</label>
                                    <input type="text" id="firstName" class="form-control" name="firstName"
                                           value="${(register.formData.firstName!'')}"
                                           placeholder="Enter your first name" required tabindex="2"
                                           maxlength="50"
                                           pattern="^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]{1,50}$"
                                           title="First name can only include letters, spaces, hyphens, and apostrophes (max 50 characters)"
                                           />
                                </div>

                                <div class="form-group">
                                    <label for="lastName">${msg("lastName")}</label>
                                    <input type="text" id="lastName" class="form-control" name="lastName"
                                           value="${(register.formData.lastName!'')}"
                                           placeholder="Enter your last name" required tabindex="3"
                                           maxlength="50"
                                           pattern="^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]{1,50}$"
                                           title="Last name can only include letters, spaces, hyphens, and apostrophes
                                           (max 50 characters)
                                           />
                                </div>
                                <!-- New Row for Plan -->
                                <div class="form-row">
                                    <div class="form-group full-width">
                                        <label for="plan">Plan</label>
                                        <select id="plan" name="plan" class="form-control" required tabindex="7">
                                            <option value="">Select your plan</option>
                                            <option value="basic">Basic</option>
                                            <option value="pro">Pro</option>
                                            <option value="enterprise">Enterprise</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <!-- Password Row -->
                            <div class="form-row three-columns">
                                <!-- Password Field -->
                                <div class="form-group">
                                    <label for="password">${msg("password")}</label>
                                    <input type="password" id="password" class="form-control" name="password"
                                           placeholder="Enter your password" required tabindex="4"
                                           oninput="validatePasswordStrength(); checkPasswordMatch(); checkFormValidity();" />
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
                                <div class="form-group">
                                    <label for="password-confirm">${msg("passwordConfirm")}</label>
                                    <input type="password" id="password-confirm" class="form-control"
                                           name="password-confirm" placeholder="Confirm your password" required tabindex="5"
                                           oninput="checkPasswordMatch(); checkFormValidity();" />
                                    <div id="password-match-message" class="password-match-message"></div>
                                </div>


                                <div class="form-group">
                                    <label for="sector">Sector</label>
                                    <select id="sector" name="sector" class="form-control" required tabindex="6">
                                        <option value="">Select your sector</option>
                                        <option value="ecommerce">E-commerce</option>
                                        <option value="retail">Retail</option>
                                        <option value="it">IT</option>
                                        <option value="tourism_services">Tourism Services</option>
                                        <option value="hospitality">Hospitality and Accommodation</option>
                                        <option value="travel">Travel and Transportation</option>
                                        <option value="health_insurance">Health Insurance</option>
                                        <option value="digital_healthcare">Digital Healthcare</option>
                                        <option value="healthcare">Hospitals and Healthcare</option>
                                        <option value="others">Others</option>
                                    </select>
                                </div>

                            </div>

                            <!-- Email and Phone Row -->
                            <div class="form-row three-columns">
                                <div class="form-group">
                                    <label for="email">${msg("email")}</label>
                                    <input type="email" id="email" class="form-control" name="email"
                                           value="${(register.formData.email!'')}"
                                           placeholder="Enter your email address" required tabindex="8"
                                           maxlength="100"
                                           title="Email address must be valid and under 100 characters"/>
                                </div>

                                <div class="form-group" style="grid-column: span 2;">
                                    <label for="phoneNumber">Phone Number</label>
                                    <div class="phone-input-container">
                                        <select id="countryCode" name="countryCode" class="country-select" required
                                        onchange="updateCountryCode()" tabindex="9">
                                            <#include "country-codes.ftl">
                                        </select>
                                        <input type="tel" id="phoneNumber" class="phone-input" name="phoneNumber"
                                               placeholder="Enter phone number" required pattern="[0-9]*"
                                               oninput="this.value = this.value.replace(/[^0-9]/g, '')" tabindex="10"/>
                                        <input type="hidden" id="phoneCode" name="phoneCode" value=""/>
                                    </div>
                                    <div id="phoneError" class="error-message"></div>
                                </div>
                            </div>
                        </div>

                        <!-- Terms and Conditions -->
                        <div class="form-group terms-container">
                            <label class="checkbox-label">
                                <input type="checkbox" id="terms" name="terms" required tabindex="11" onchange="checkFormValidity();">
                                <span>I agree to the <a href="#" onclick="showTerms(); return false;"
                                tabindex="12">Terms and Conditions</a></span>
                            </label>
                        </div>

                        <!-- Submit Button -->
                        <div class="form-group">
                            <button class="submit-btn disabled" type="submit" tabindex="13" id="registerButton" disabled>${msg("doRegister")}</button>
                            <div id="form-validation-message" class="form-validation-message">Please complete all required fields and meet password requirements</div>
                        </div>

                        <!-- Login Link -->
                        <div class="form-group login-link-container">
                            <div class="login-text">Already have an account?</div>
                            <a href="${url.loginUrl}" class="login-button" tabindex="14">Sign In</a>
                        </div>

                        <!-- Error message container -->
                        <div id="errorContainer" class="error-container" style="display: none;">
                            <div class="error-message"></div>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Terms Modal -->
        <div id="termsModal" class="modal">
            <div class="modal-content">
                <span class="close" onclick="closeTerms()">&times;</span>
                <h2>Terms and Conditions</h2>
                <div class="terms-content">
                    <h3>1. Data Collection and Usage</h3>
                    <p>Convonest Analytics ("we", "our", or "the Company") collects and processes user data to provide analytics services. By using our platform, you agree that we may collect:</p>
                    <ul>
                        <li>User authentication information</li>
                        <li>Usage patterns and analytics data</li>
                        <li>System performance metrics</li>
                        <li>User preferences and settings</li>
                    </ul>

                    <h3>2. Data Protection</h3>
                    <p>We implement industry-standard security measures to protect your data. Your information is:</p>
                    <ul>
                        <li>Encrypted during transmission and storage</li>
                        <li>Accessible only to authorized personnel</li>
                        <li>Regularly backed up and monitored</li>
                    </ul>

                    <h3>3. User Rights</h3>
                    <p>You have the right to:</p>
                    <ul>
                        <li>Access your personal data</li>
                        <li>Request data modification or deletion</li>
                        <li>Opt-out of non-essential data collection</li>
                        <li>Export your data in a machine-readable format</li>
                    </ul>

                    <h3>4. Partial Refund</h3>
                    <p>All subscription fees are subject to our refund policy. Partial refunds may be issued at the sole discretion of Convonest Analytics and under circumstances defined in the official refund policy.</p>

                    <h3>5. No Liability</h3>
                    <p>Convonest Analytics shall not be liable for any indirect, incidental, or consequential damages arising from the use of our platform, including but not limited to data loss, service interruption, or any financial loss.</p>

                    <h3>6. Privacy Policy</h3>
                    <p>For detailed information about how we handle your data, please refer to our complete Privacy Policy.</p>
                </div>
            </div>
        </div>


        <style>
            /* Base styles */
            body {
                margin: 0;
                padding: 0;
                font-family: Arial, sans-serif;
                background: url('${url.resourcesPath}/img/bg.jpg') no-repeat center center fixed;
                background-size: cover;
                color: #333;
            }

            /* Header hiding */
            #kc-header,
            #kc-header-wrapper {
                display: none !important;
            }

            /* Container styles */
            #kc-form,
            #kc-form-wrapper,
            .login-container {
                width: 100% !important;
                max-width: 700px !important;
                margin: 0 auto !important;
                padding: 20px !important;
                position: relative !important;
                left: -15px !important;
            }

            /* Form grid layout */
            .form-grid {
                display: flex !important;
                flex-direction: column !important;
                gap: 6px !important;
                width: 100% !important;
            }

            /* Row layouts */
            .form-row {
                display: flex !important;
                gap: 6px !important;
                width: 100% !important;
            }

            .three-columns {
                display: grid !important;
                grid-template-columns: repeat(3, 1fr) !important;
                gap: 6px !important;
            }

            .full-width {
                width: 100% !important;
            }

            /* Form group styling */
            .form-group {
                width: 100% !important;
                margin-bottom: 6px !important;
            }

            /* Phone input container */
            .phone-input-container {
                display: grid !important;
                grid-template-columns: 150px 1fr !important;
                gap: 8px !important;
            }

            /* Input styling */
            .form-control,
            .country-select,
            .phone-input {
                width: 100% !important;
                height: 40px !important;
                padding: 8px 12px !important;
                border: 1px solid #e5e7eb !important;
                border-radius: 6px !important;
                font-size: 14px !important;
                box-sizing: border-box !important;
            }

            /* Focus styles */
            .form-control:focus,
            .country-select:focus,
            .phone-input:focus {
                outline: none !important;
                border-color: #0061f2 !important;
                box-shadow: 0 0 0 2px rgba(0,97,242,0.2) !important;
            }

            /* Submit button */
            .submit-btn {
                width: 100% !important;
                height: 40px !important;
                background: #0061f2 !important;
                color: white !important;
                border: none !important;
                border-radius: 6px !important;
                font-size: 14px !important;
                font-weight: 500 !important;
                cursor: pointer !important;
                transition: background-color 0.2s !important;
                margin: 5px 0 !important;
            }

            .submit-btn:hover {
                background: #0052cc !important;
            }

            .submit-btn.disabled {
                background-color: #9ca3af !important;
                cursor: not-allowed !important;
                opacity: 0.6 !important;
            }

            .submit-btn:disabled:hover {
                background-color: #9ca3af !important;
            }

            /* Form validation message */
            .form-validation-message {
                font-size: 12px !important;
                color: #dc2626 !important;
                margin-top: 5px !important;
                text-align: center !important;
                display: block !important;
            }

            .form-validation-message.hidden {
                display: none !important;
            }

            /* Error container */
            .error-container {
                background-color: #fee2e2;
                border: 1px solid #ef4444;
                padding: 10px;
                border-radius: 6px;
                margin-bottom: 10px;
                display: none;
            }

            .error-message {
                color: #dc2626;
                font-size: 14px;
                margin-top: 4px;
            }

            /* Terms container */
            .terms-container {
                text-align: center !important;
                margin: 5px 0 !important;
                display: flex !important;
                justify-content: center !important;
                align-items: center !important;
                width: 100% !important;
            }

            .checkbox-label {
                display: flex !important;
                align-items: center !important;
                justify-content: center !important;
                gap: 8px !important;
                font-size: 14px !important;
                color: #374151 !important;
                cursor: pointer !important;
            }

            .checkbox-label input[type="checkbox"] {
                margin: 0 !important;
                width: 16px !important;
                height: 16px !important;
                accent-color: #0061f2 !important;
                cursor: pointer !important;
            }

            .checkbox-label span {
                text-align: center !important;
            }

            .checkbox-label a {
                color: #0061f2 !important;
                text-decoration: underline !important;
            }

            .checkbox-label a:hover {
                color: #0052cc !important;
            }


             .logo {
                 max-width: 100px;
             }

            /* Responsive design */
            @media (max-width: 768px) {
                .three-columns {
                    grid-template-columns: 1fr !important;
                }

                .phone-input-container {
                    grid-template-columns: 1fr !important;
                }

                .form-row {
                    flex-direction: column !important;
                }
            }

            /* Terms modal refinements */
            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.5);
            }

            .modal-content {
                background-color: white;
                margin: 5% auto;
                padding: 20px;
                border-radius: 8px;
                width: 80%;
                max-width: 700px;
                max-height: 80vh;
                overflow-y: auto;
                position: relative;
            }

            .close {
                position: absolute;
                right: 20px;
                top: 20px;
                font-size: 28px;
                font-weight: bold;
                cursor: pointer;
            }

            .terms-content {
                margin-top: 20px;
            }

            .terms-content h3 {
                margin-top: 20px;
                color: #333;
            }

            .terms-content ul {
                margin: 10px 0;
                padding-left: 20px;
            }

            .terms-content li {
                margin: 5px 0;
            }

            .loading-container {
                background-color: #e0f2fe;
                border: 1px solid #0284c7;
                padding: 10px;
                border-radius: 6px;
                margin-bottom: 10px;
                text-align: center;
            }
            .loading-message {
                color: #0369a1;
                font-size: 14px;
            }

            .password-strength {
                font-weight: bold;
                margin-top: 4px;
            }
            .password-strength.weak { color: #dc2626; }
            .password-strength.medium { color: #d97706; }
            .password-strength.strong { color: #16a34a; }

            .password-requirements {
                margin: 5px 0 0 0;
                padding-left: 20px;
                font-size: 13px;
            }
            .password-requirements li {
                color: #6b7280;
            }
            .password-requirements li.met {
                color: #16a34a;
                font-weight: bold;
            }

            .password-match-message {
                font-size: 13px;
                margin-top: 4px;
            }
            .password-match-message.mismatch { color: #dc2626; }
            .password-match-message.match { color: #16a34a; }

            .oauth-note a {
                color: #0061f2;
                text-decoration: underline;
            }

            .oauth-note a:hover {
                color: #0052cc;
            }

        </style>

        <script>
            // Global variables to track validation state
            let passwordRequirementsMet = false;
            let passwordsMatch = false;

            function validatePasswordStrength() {
                const password = document.getElementById("password").value;

                // Requirement checks
                const hasLength = password.length >= 8;
                const hasUpper = /[A-Z]/.test(password);
                const hasLower = /[a-z]/.test(password);
                const hasNumber = /[0-9]/.test(password);
                const hasSpecial = /[^A-Za-z0-9]/.test(password);

                // Update requirement list
                updateRequirement("req-length", hasLength);
                updateRequirement("req-uppercase", hasUpper);
                updateRequirement("req-lowercase", hasLower);
                updateRequirement("req-number", hasNumber);
                updateRequirement("req-special", hasSpecial);

                // Check if all requirements are met
                passwordRequirementsMet = hasLength && hasUpper && hasLower && hasNumber && hasSpecial;

                // Strength level
                let strengthCount = [hasLength, hasUpper, hasLower, hasNumber, hasSpecial].filter(Boolean).length;
                const strengthEl = document.getElementById("password-strength");

                if (strengthCount <= 2) {
                    strengthEl.textContent = "Strength: Weak";
                    strengthEl.className = "password-strength weak";
                } else if (strengthCount === 3 || strengthCount === 4) {
                    strengthEl.textContent = "Strength: Medium";
                    strengthEl.className = "password-strength medium";
                } else if (strengthCount === 5) {
                    strengthEl.textContent = "Strength: Strong";
                    strengthEl.className = "password-strength strong";
                }
            }

            function updateRequirement(id, met) {
                const el = document.getElementById(id);
                if (met) {
                    el.classList.add("met");
                } else {
                    el.classList.remove("met");
                }
            }

            function checkPasswordMatch() {
                const password = document.getElementById("password").value;
                const confirm = document.getElementById("password-confirm").value;
                const matchMessage = document.getElementById("password-match-message");

                if (!confirm) {
                    matchMessage.textContent = "";
                    passwordsMatch = false;
                    return;
                }

                if (password === confirm) {
                    matchMessage.textContent = "Passwords match";
                    matchMessage.className = "password-match-message match";
                    passwordsMatch = true;
                } else {
                    matchMessage.textContent = "Passwords do not match";
                    matchMessage.className = "password-match-message mismatch";
                    passwordsMatch = false;
                }
            }

            function checkFormValidity() {
                const button = document.getElementById("registerButton");
                const validationMessage = document.getElementById("form-validation-message");
                const termsChecked = document.getElementById("terms").checked;

                // Check if all conditions are met
                const isFormValid = passwordRequirementsMet && passwordsMatch && termsChecked;

                if (isFormValid) {
                    // Enable button
                    button.disabled = false;
                    button.classList.remove("disabled");
                    validationMessage.classList.add("hidden");
                } else {
                    // Disable button
                    button.disabled = true;
                    button.classList.add("disabled");
                    validationMessage.classList.remove("hidden");

                    // Update validation message based on what's missing
                    let message = "Please ";
                    let issues = [];

                    if (!termsChecked) {
                        issues.push("accept the terms and conditions");
                    }

                    if (issues.length > 0) {
                        message += issues.join(", ");
                        validationMessage.textContent = message;
                    }
                }
            }

            function showLoading() {
                document.getElementById('loadingContainer').style.display = 'block';
            }
            function hideLoading() {
                document.getElementById('loadingContainer').style.display = 'none';
            }


            function validatePhoneNumber(phone) {
                return /^\d{7,15}$/.test(phone);
            }

            function showError(message) {
                const errorContainer = document.getElementById('errorContainer');
                const errorMessage = errorContainer.querySelector('.error-message');
                errorMessage.textContent = message;
                errorContainer.style.display = 'block';
                errorContainer.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }

            function hideError() {
                const errorContainer = document.getElementById('errorContainer');
                errorContainer.style.display = 'none';
            }

            function updatePhoneCode() {
                const select = document.getElementById('countryCode');
                const code = select.options[select.selectedIndex].getAttribute('data-phone-code');
                document.getElementById('phoneCode').value = code;
            }

            function disableSubmitButton() {
                const btn = document.querySelector(".submit-btn");
                btn.disabled = true;
                btn.textContent = "Processing..."; // Optional: change text
                btn.classList.add("disabled");
            }

            function enableSubmitButton() {
                const btn = document.querySelector(".submit-btn");
                btn.disabled = false;
                btn.textContent = "${msg("doRegister")}"; // Back to original text
                btn.classList.remove("disabled");
            }

            async function registerUser(event) {
                event.preventDefault();

                // Double-check form validity before proceeding
                if (!passwordRequirementsMet || !passwordsMatch || !document.getElementById("terms").checked) {
                    showError("Please complete all required fields and meet all password requirements before submitting.");
                    return false;
                }

                const form = event.target;
                const phoneNumber = document.getElementById("phoneNumber").value;
                const password = document.getElementById("password").value;
                const passwordConfirm = document.getElementById("password-confirm").value;
                const terms = document.getElementById("terms").checked;

                hideError();
                hideLoading();

                if (!validatePhoneNumber(phoneNumber)) {
                    showError("Please enter a valid phone number (7-15 digits)");
                    return false;
                }

                if (password !== passwordConfirm) {
                    showError("Passwords do not match");
                    return false;
                }

                if (!terms) {
                    showError("Please accept the Terms and Conditions");
                    return false;
                }

                // Ensure country code is updated
                updatePhoneCode();

                // Create URL-encoded data string
                const formData = new URLSearchParams(new FormData(form)).toString();
                try {
                    showLoading(); // Show spinner/loading text
                    disableSubmitButton();
                    // Simulate network delay for testing loading UI
                    await new Promise(resolve => setTimeout(resolve, 3000)); // wait 3 seconds

                    const response = await fetch("/realms/master/custom-registration/register", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded", // Explicitly set the correct content type
                        },
                        body: formData, // Pass URL-encoded string as the body
                    });
                    hideLoading(); // Hide loading
                    enableSubmitButton();

                    if (response.ok) {
                        // Instead of redirecting, show success message
                        showSuccessMessage(
                            "Registration successful! We've sent a verification link to your email address. " +
                            "Please check your inbox and click the link to complete your registration."
                        );
                    } else {
                        if (response.status === 409) {
                            showError("An account with this email already exists. Please log in or use a different email.");
                        } else {
                            const result = await response.json().catch(() => ({ message: "Unknown error occurred" }));
                            showError(result.message || "Registration failed");
                        }
                    }
                } catch (error) {
                    hideLoading(); // Hide loading
                    enableSubmitButton();
                    console.error("Error submitting registration form:", error);
                    showError("An unexpected error occurred. Please try again.");
                }
                return false;
            }

            function showSuccessMessage(message) {
                const errorContainer = document.getElementById('errorContainer');
                const errorMessage = errorContainer.querySelector('.error-message');

                // Style for success message
                errorContainer.style.backgroundColor = '#ecfdf5';
                errorContainer.style.borderColor = '#34d399';
                errorMessage.style.color = '#047857';

                errorMessage.textContent = message;
                errorContainer.style.display = 'block';
                errorContainer.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }

            function updateCountryCode() {
                const select = document.getElementById('countryCode');
                const code = select.options[select.selectedIndex].getAttribute('data-phone-code');
                document.getElementById('phoneCode').value = code;
            }

            function showTerms() {
                document.getElementById('termsModal').style.display = 'block';
            }

            function closeTerms() {
                document.getElementById('termsModal').style.display = 'none';
            }

            window.onclick = function(event) {
                const modal = document.getElementById('termsModal');
                if (event.target == modal) {
                    modal.style.display = 'none';
                }
            }

            // Initialize form validation on page load
            window.onload = function() {
                checkFormValidity();
            }
        </script>
    </#if>
</@layout.registrationLayout>