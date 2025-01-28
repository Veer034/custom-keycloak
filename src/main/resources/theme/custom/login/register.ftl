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
                                           placeholder="Enter your first name" required tabindex="2"/>
                                </div>

                                <div class="form-group">
                                    <label for="lastName">${msg("lastName")}</label>
                                    <input type="text" id="lastName" class="form-control" name="lastName"
                                           value="${(register.formData.lastName!'')}"
                                           placeholder="Enter your last name" required tabindex="3"/>
                                </div>
                            </div>

                            <!-- Password Row -->
                            <div class="form-row three-columns">
                                <div class="form-group">
                                    <label for="password">${msg("password")}</label>
                                    <input type="password" id="password" class="form-control" name="password"
                                           placeholder="Enter your password" required tabindex="4"/>
                                </div>

                                <div class="form-group">
                                    <label for="password-confirm">${msg("passwordConfirm")}</label>
                                    <input type="password" id="password-confirm" class="form-control"
                                           name="password-confirm"
                                           placeholder="Confirm your password" required tabindex="5"/>
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
                                    </select>
                                </div>
                            </div>

                            <!-- Email and Phone Row -->
                            <div class="form-row three-columns">
                                <div class="form-group">
                                    <label for="email">${msg("email")}</label>
                                    <input type="email" id="email" class="form-control" name="email"
                                           value="${(register.formData.email!'')}"
                                           placeholder="Enter your email address" required tabindex="7"/>
                                </div>

                                <div class="form-group" style="grid-column: span 2;">
                                    <label for="phoneNumber">Phone Number</label>
                                    <div class="phone-input-container">
                                        <select id="country" name="country" class="country-select" required onchange="updateCountryCode()" tabindex="8">
                                            <#include "country-codes.ftl">
                                        </select>
                                        <input type="tel" id="phoneNumber" class="phone-input" name="phoneNumber"
                                               placeholder="Enter phone number" required pattern="[0-9]*"
                                               oninput="this.value = this.value.replace(/[^0-9]/g, '')" tabindex="9"/>
                                        <input type="hidden" id="countryCode" name="countryCode" value=""/>
                                    </div>
                                    <div id="phoneError" class="error-message"></div>
                                </div>
                            </div>
                        </div>

                        <!-- Terms and Conditions -->
                        <div class="form-group terms-container">
                            <label class="checkbox-label">
                                <input type="checkbox" id="terms" name="terms" required tabindex="10">
                                <span>I agree to the <a href="#" onclick="showTerms(); return false;" tabindex="11">Terms and Conditions</a></span>
                            </label>
                        </div>

                        <!-- Submit Button -->
                        <div class="form-group">
                            <button class="submit-btn" type="submit" tabindex="12">${msg("doRegister")}</button>
                        </div>

                        <!-- Login Link -->
                        <div class="form-group login-link-container">
                            <div class="login-text">Already have an account?</div>
                            <a href="${url.loginUrl}" class="login-button" tabindex="13">Sign In</a>
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

                    <h3>4. Privacy Policy</h3>
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
            }

            /* Logo container */
            .logo-container {
                text-align: center !important;
                margin-bottom: 5px !important;
            }

            .logo {
                max-width: 120px !important;
                margin-bottom: 5px !important;
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
        </style>

        <script>

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

          async function registerUser(event) {
              event.preventDefault();

              const form = event.target;
              const phoneNumber = document.getElementById("phoneNumber").value;
              const password = document.getElementById("password").value;
              const passwordConfirm = document.getElementById("password-confirm").value;
              const terms = document.getElementById("terms").checked;

              hideError();

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


              // Create URL-encoded data string
              const formData = new URLSearchParams(new FormData(form)).toString();
              try {
                  const response = await fetch("/realms/master/custom-registration/register", {
                      method: "POST",
                      headers: {
                          "Content-Type": "application/x-www-form-urlencoded", // Explicitly set the correct content type
                      },
                      body: formData, // Pass URL-encoded string as the body
                  });

                  if (response.ok) {
                       // Instead of redirecting, show success message
                      showSuccessMessage(
                          "Registration successful! We've sent a verification link to your email address. " +
                          "Please check your inbox and click the link to complete your registration."
                      );
                  } else {
                    if (response.status === 409) {
                        showError("This email is already registered. Try another email.");
                    } else {
                        const result = await response.json().catch(() => ({ message: "Unknown error occurred" }));
                        showError(result.message || "Registration failed");
                    }
                  }
              } catch (error) {
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
                const select = document.getElementById('country');
                const code = select.options[select.selectedIndex].getAttribute('data-phone-code');
                document.getElementById('countryCode').value = code;
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
        </script>
    </#if>
</@layout.registrationLayout>