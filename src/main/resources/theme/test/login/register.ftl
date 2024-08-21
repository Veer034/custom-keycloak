<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo displayWide=false; section>
    <#if section == "header">
        Register
    <#elseif section == "form">
    <div id="kc-form">
        <div id="kc-form-wrapper">
            <form id="kc-register-form" onsubmit="register.disabled = true; return validateForm();" action="/realms/master/custom-registration/register" method="post">
                <!-- Company Name Field -->
                <div class="form-group">
                    <label for="companyName" class="${properties.kcLabelClass!}">Company Name:</label>
                    <input tabindex="1" id="companyName" class="${properties.kcInputClass!}" name="companyName" type="text" required/>
                </div>

                <!-- First Name Field -->
                <div class="form-group">
                    <label for="firstName" class="${properties.kcLabelClass!}">First Name:</label>
                    <input tabindex="2" id="firstName" class="${properties.kcInputClass!}" name="firstName" type="text" required/>
                </div>

                <!-- Last Name Field -->
                <div class="form-group">
                    <label for="lastName" class="${properties.kcLabelClass!}">Last Name:</label>
                    <input tabindex="3" id="lastName" class="${properties.kcInputClass!}" name="lastName" type="text" required/>
                </div>

                <!-- Email Field -->
                <div class="form-group">
                    <label for="email" class="${properties.kcLabelClass!}">Email:</label>
                    <input tabindex="4" id="email" class="${properties.kcInputClass!}" name="email" type="email" required/>
                </div>

                <!-- Password Field -->
                <div class="form-group">
                    <label for="password" class="${properties.kcLabelClass!}">Password:</label>
                    <input tabindex="5" id="password" class="${properties.kcInputClass!}" name="password" type="password" required/>
                </div>

                <!-- Confirm Password Field -->
                <div class="form-group">
                    <label for="passwordConfirm" class="${properties.kcLabelClass!}">Confirm Password:</label>
                    <input tabindex="6" id="passwordConfirm" class="${properties.kcInputClass!}" name="passwordConfirm" type="password" required/>
                </div>

                <!-- Country Code and Phone Number Field (Same Row) -->
                <div class="form-group">
                    <label for="phoneNumber" class="${properties.kcLabelClass!}">Phone Number:</label>
                    <div style="display: flex; align-items: center;">
                        <select id="country" name="country" class="${properties.kcInputClass!}" style="flex: 1; margin-right: 10px;" required onchange="updateCountryCode()">
                            <!-- Country List -->
                            <#list [
                                {"code": "+1", "label": "United States of America", "country_code": "US"},
                                {"code": "+1", "label": "Canada", "country_code": "CA"},
                                {"code": "+7", "label": "Russia", "country_code": "RU"},
                                {"code": "+20", "label": "Egypt", "country_code": "EG"},
                                {"code": "+27", "label": "South Africa", "country_code": "ZA"},
                                {"code": "+30", "label": "Greece", "country_code": "GR"},
                                {"code": "+31", "label": "Netherlands", "country_code": "NL"},
                                {"code": "+32", "label": "Belgium", "country_code": "BE"},
                                {"code": "+33", "label": "France", "country_code": "FR"},
                                {"code": "+34", "label": "Spain", "country_code": "ES"},
                                {"code": "+36", "label": "Hungary", "country_code": "HU"},
                                {"code": "+39", "label": "Italy", "country_code": "IT"},
                                {"code": "+40", "label": "Romania", "country_code": "RO"},
                                {"code": "+41", "label": "Switzerland", "country_code": "CH"},
                                {"code": "+43", "label": "Austria", "country_code": "AT"},
                                {"code": "+44", "label": "United Kingdom", "country_code": "GB"},
                                {"code": "+45", "label": "Denmark", "country_code": "DK"},
                                {"code": "+46", "label": "Sweden", "country_code": "SE"},
                                {"code": "+47", "label": "Norway", "country_code": "NO"},
                                {"code": "+48", "label": "Poland", "country_code": "PL"},
                                {"code": "+49", "label": "Germany", "country_code": "DE"},
                                {"code": "+52", "label": "Mexico", "country_code": "MX"},
                                {"code": "+53", "label": "Cuba", "country_code": "CU"},
                                {"code": "+54", "label": "Argentina", "country_code": "AR"},
                                {"code": "+55", "label": "Brazil", "country_code": "BR"},
                                {"code": "+56", "label": "Chile", "country_code": "CL"},
                                {"code": "+57", "label": "Colombia", "country_code": "CO"},
                                {"code": "+58", "label": "Venezuela", "country_code": "VE"},
                                {"code": "+60", "label": "Malaysia", "country_code": "MY"},
                                {"code": "+61", "label": "Australia", "country_code": "AU"},
                                {"code": "+62", "label": "Indonesia", "country_code": "ID"},
                                {"code": "+63", "label": "Philippines", "country_code": "PH"},
                                {"code": "+64", "label": "New Zealand", "country_code": "NZ"},
                                {"code": "+65", "label": "Singapore", "country_code": "SG"},
                                {"code": "+66", "label": "Thailand", "country_code": "TH"},
                                {"code": "+81", "label": "Japan", "country_code": "JP"},
                                {"code": "+82", "label": "South Korea", "country_code": "KR"},
                                {"code": "+84", "label": "Vietnam", "country_code": "VN"},
                                {"code": "+86", "label": "China", "country_code": "CN"},
                                {"code": "+90", "label": "Turkey", "country_code": "TR"},
                                {"code": "+91", "label": "India", "country_code": "IN"},
                                {"code": "+92", "label": "Pakistan", "country_code": "PK"},
                                {"code": "+93", "label": "Afghanistan", "country_code": "AF"},
                                {"code": "+94", "label": "Sri Lanka", "country_code": "LK"},
                                {"code": "+95", "label": "Myanmar", "country_code": "MM"},
                                {"code": "+98", "label": "Iran", "country_code": "IR"},
                                {"code": "+211", "label": "South Sudan", "country_code": "SS"},
                                {"code": "+212", "label": "Morocco", "country_code": "MA"},
                                {"code": "+213", "label": "Algeria", "country_code": "DZ"},
                                {"code": "+216", "label": "Tunisia", "country_code": "TN"},
                                {"code": "+218", "label": "Libya", "country_code": "LY"},
                                {"code": "+220", "label": "Gambia", "country_code": "GM"},
                                {"code": "+221", "label": "Senegal", "country_code": "SN"},
                                {"code": "+222", "label": "Mauritania", "country_code": "MR"},
                                {"code": "+223", "label": "Mali", "country_code": "ML"},
                                {"code": "+224", "label": "Guinea", "country_code": "GN"},
                                {"code": "+225", "label": "Ivory Coast", "country_code": "CI"},
                                {"code": "+226", "label": "Burkina Faso", "country_code": "BF"},
                                {"code": "+227", "label": "Niger", "country_code": "NE"},
                                {"code": "+228", "label": "Togo", "country_code": "TG"},
                                {"code": "+229", "label": "Benin", "country_code": "BJ"},
                                {"code": "+230", "label": "Mauritius", "country_code": "MU"},
                                {"code": "+231", "label": "Liberia", "country_code": "LR"},
                                {"code": "+232", "label": "Sierra Leone", "country_code": "SL"},
                                {"code": "+233", "label": "Ghana", "country_code": "GH"},
                                {"code": "+234", "label": "Nigeria", "country_code": "NG"},
                                {"code": "+235", "label": "Chad", "country_code": "TD"},
                                {"code": "+236", "label": "Central African Republic", "country_code": "CF"},
                                {"code": "+237", "label": "Cameroon", "country_code": "CM"},
                                {"code": "+238", "label": "Cape Verde", "country_code": "CV"},
                                {"code": "+239", "label": "Sao Tome and Principe", "country_code": "ST"},
                                {"code": "+240", "label": "Equatorial Guinea", "country_code": "GQ"},
                                {"code": "+241", "label": "Gabon", "country_code": "GA"},
                                {"code": "+242", "label": "Congo", "country_code": "CG"},
                                {"code": "+243", "label": "Democratic Republic of the Congo", "country_code": "CD"},
                                {"code": "+244", "label": "Angola", "country_code": "AO"},
                                {"code": "+245", "label": "Guinea-Bissau", "country_code": "GW"},
                                {"code": "+246", "label": "British Indian Ocean Territory", "country_code": "IO"},
                                {"code": "+248", "label": "Seychelles", "country_code": "SC"},
                                {"code": "+249", "label": "Sudan", "country_code": "SD"},
                                {"code": "+250", "label": "Rwanda", "country_code": "RW"},
                                {"code": "+251", "label": "Ethiopia", "country_code": "ET"},
                                {"code": "+252", "label": "Somalia", "country_code": "SO"},
                                {"code": "+253", "label": "Djibouti", "country_code": "DJ"},
                                {"code": "+254", "label": "Kenya", "country_code": "KE"},
                                {"code": "+255", "label": "Tanzania", "country_code": "TZ"},
                                {"code": "+256", "label": "Uganda", "country_code": "UG"},
                                {"code": "+257", "label": "Burundi", "country_code": "BI"},
                                {"code": "+258", "label": "Mozambique", "country_code": "MZ"},
                                {"code": "+260", "label": "Zambia", "country_code": "ZM"},
                                {"code": "+261", "label": "Madagascar", "country_code": "MG"},
                                {"code": "+262", "label": "Réunion", "country_code": "RE"},
                                {"code": "+263", "label": "Zimbabwe", "country_code": "ZW"},
                                {"code": "+264", "label": "Namibia", "country_code": "NA"},
                                {"code": "+265", "label": "Malawi", "country_code": "MW"},
                                {"code": "+266", "label": "Lesotho", "country_code": "LS"},
                                {"code": "+267", "label": "Botswana", "country_code": "BW"},
                                {"code": "+268", "label": "Eswatini", "country_code": "SZ"},
                                {"code": "+269", "label": "Comoros", "country_code": "KM"},
                                {"code": "+290", "label": "Saint Helena", "country_code": "SH"},
                                {"code": "+291", "label": "Eritrea", "country_code": "ER"},
                                {"code": "+297", "label": "Aruba", "country_code": "AW"},
                                {"code": "+298", "label": "Faroe Islands", "country_code": "FO"},
                                {"code": "+299", "label": "Greenland", "country_code": "GL"},
                                {"code": "+350", "label": "Gibraltar", "country_code": "GI"},
                                {"code": "+351", "label": "Portugal", "country_code": "PT"},
                                {"code": "+352", "label": "Luxembourg", "country_code": "LU"},
                                {"code": "+353", "label": "Ireland", "country_code": "IE"},
                                {"code": "+354", "label": "Iceland", "country_code": "IS"},
                                {"code": "+355", "label": "Albania", "country_code": "AL"},
                                {"code": "+356", "label": "Malta", "country_code": "MT"},
                                {"code": "+357", "label": "Cyprus", "country_code": "CY"},
                                {"code": "+358", "label": "Finland", "country_code": "FI"},
                                {"code": "+359", "label": "Bulgaria", "country_code": "BG"},
                                {"code": "+370", "label": "Lithuania", "country_code": "LT"},
                                {"code": "+371", "label": "Latvia", "country_code": "LV"},
                                {"code": "+372", "label": "Estonia", "country_code": "EE"},
                                {"code": "+373", "label": "Moldova", "country_code": "MD"},
                                {"code": "+374", "label": "Armenia", "country_code": "AM"},
                                {"code": "+375", "label": "Belarus", "country_code": "BY"},
                                {"code": "+376", "label": "Andorra", "country_code": "AD"},
                                {"code": "+377", "label": "Monaco", "country_code": "MC"},
                                {"code": "+378", "label": "San Marino", "country_code": "SM"},
                                {"code": "+380", "label": "Ukraine", "country_code": "UA"},
                                {"code": "+381", "label": "Serbia", "country_code": "RS"},
                                {"code": "+382", "label": "Montenegro", "country_code": "ME"},
                                {"code": "+383", "label": "Kosovo", "country_code": "XK"},
                                {"code": "+385", "label": "Croatia", "country_code": "HR"},
                                {"code": "+386", "label": "Slovenia", "country_code": "SI"},
                                {"code": "+387", "label": "Bosnia and Herzegovina", "country_code": "BA"},
                                {"code": "+389", "label": "North Macedonia", "country_code": "MK"},
                                {"code": "+420", "label": "Czech Republic", "country_code": "CZ"},
                                {"code": "+421", "label": "Slovakia", "country_code": "SK"},
                                {"code": "+423", "label": "Liechtenstein", "country_code": "LI"},
                                {"code": "+500", "label": "Falkland Islands", "country_code": "FK"},
                                {"code": "+501", "label": "Belize", "country_code": "BZ"},
                                {"code": "+502", "label": "Guatemala", "country_code": "GT"},
                                {"code": "+503", "label": "El Salvador", "country_code": "SV"},
                                {"code": "+504", "label": "Honduras", "country_code": "HN"},
                                {"code": "+505", "label": "Nicaragua", "country_code": "NI"},
                                {"code": "+506", "label": "Costa Rica", "country_code": "CR"},
                                {"code": "+507", "label": "Panama", "country_code": "PA"},
                                {"code": "+508", "label": "Saint Pierre and Miquelon", "country_code": "PM"},
                                {"code": "+509", "label": "Haiti", "country_code": "HT"},
                                {"code": "+590", "label": "Guadeloupe", "country_code": "GP"},
                                {"code": "+591", "label": "Bolivia", "country_code": "BO"},
                                {"code": "+592", "label": "Guyana", "country_code": "GY"},
                                {"code": "+593", "label": "Ecuador", "country_code": "EC"},
                                {"code": "+595", "label": "Paraguay", "country_code": "PY"},
                                {"code": "+596", "label": "Martinique", "country_code": "MQ"},
                                {"code": "+597", "label": "Suriname", "country_code": "SR"},
                                {"code": "+598", "label": "Uruguay", "country_code": "UY"},
                                {"code": "+599", "label": "Netherlands Antilles", "country_code": "AN"},
                                {"code": "+670", "label": "East Timor", "country_code": "TL"},
                                {"code": "+672", "label": "Norfolk Island", "country_code": "NF"},
                                {"code": "+673", "label": "Brunei", "country_code": "BN"},
                                {"code": "+674", "label": "Nauru", "country_code": "NR"},
                                {"code": "+675", "label": "Papua New Guinea", "country_code": "PG"},
                                {"code": "+676", "label": "Tonga", "country_code": "TO"},
                                {"code": "+677", "label": "Solomon Islands", "country_code": "SB"},
                                {"code": "+678", "label": "Vanuatu", "country_code": "VU"},
                                {"code": "+679", "label": "Fiji", "country_code": "FJ"},
                                {"code": "+680", "label": "Palau", "country_code": "PW"},
                                {"code": "+681", "label": "Wallis and Futuna", "country_code": "WF"},
                                {"code": "+682", "label": "Cook Islands", "country_code": "CK"},
                                {"code": "+683", "label": "Niue", "country_code": "NU"},
                                {"code": "+685", "label": "Samoa", "country_code": "WS"},
                                {"code": "+686", "label": "Kiribati", "country_code": "KI"},
                                {"code": "+687", "label": "New Caledonia", "country_code": "NC"},
                                {"code": "+688", "label": "Tuvalu", "country_code": "TV"},
                                {"code": "+689", "label": "French Polynesia", "country_code": "PF"},
                                {"code": "+690", "label": "Tokelau", "country_code": "TK"},
                                {"code": "+691", "label": "Micronesia", "country_code": "FM"},
                                {"code": "+692", "label": "Marshall Islands", "country_code": "MH"},
                                {"code": "+850", "label": "North Korea", "country_code": "KP"},
                                {"code": "+852", "label": "Hong Kong", "country_code": "HK"},
                                {"code": "+853", "label": "Macau", "country_code": "MO"},
                                {"code": "+855", "label": "Cambodia", "country_code": "KH"},
                                {"code": "+856", "label": "Laos", "country_code": "LA"},
                                {"code": "+880", "label": "Bangladesh", "country_code": "BD"},
                                {"code": "+886", "label": "Taiwan", "country_code": "TW"},
                                {"code": "+960", "label": "Maldives", "country_code": "MV"},
                                {"code": "+961", "label": "Lebanon", "country_code": "LB"},
                                {"code": "+962", "label": "Jordan", "country_code": "JO"},
                                {"code": "+963", "label": "Syria", "country_code": "SY"},
                                {"code": "+964", "label": "Iraq", "country_code": "IQ"},
                                {"code": "+965", "label": "Kuwait", "country_code": "KW"},
                                {"code": "+966", "label": "Saudi Arabia", "country_code": "SA"},
                                {"code": "+967", "label": "Yemen", "country_code": "YE"},
                                {"code": "+968", "label": "Oman", "country_code": "OM"},
                                {"code": "+971", "label": "United Arab Emirates", "country_code": "AE"},
                                {"code": "+972", "label": "Israel", "country_code": "IL"},
                                {"code": "+973", "label": "Bahrain", "country_code": "BH"},
                                {"code": "+974", "label": "Qatar", "country_code": "QA"},
                                {"code": "+975", "label": "Bhutan", "country_code": "BT"},
                                {"code": "+976", "label": "Mongolia", "country_code": "MN"},
                                {"code": "+977", "label": "Nepal", "country_code": "NP"},
                                {"code": "+992", "label": "Tajikistan", "country_code": "TJ"},
                                {"code": "+993", "label": "Turkmenistan", "country_code": "TM"},
                                {"code": "+994", "label": "Azerbaijan", "country_code": "AZ"},
                                {"code": "+995", "label": "Georgia", "country_code": "GE"},
                                {"code": "+996", "label": "Kyrgyzstan", "country_code": "KG"},
                                {"code": "+998", "label": "Uzbekistan", "country_code": "UZ"}
                            ] as country>
                                <option value="${country.country_code}" data-phone-code="${country.code}">${country.code}(${country.label})</option>
                            </#list>
                        </select>
                        <input tabindex="8" id="phoneNumber" class="${properties.kcInputClass!}" name="phoneNumber" type="text" style="flex: 2;" required placeholder="Enter your phone number"/>
                        <input type="hidden" id="countryCode" name="countryCode" value="+91">
                    </div>
                </div>

                <!-- Terms and Conditions -->
                <div class="form-group">
                    <label class="${properties.kcLabelClass!}">
                        <input type="checkbox" required> I agree to the <a href="#">terms and conditions</a>
                    </label>
                </div>

                <!-- Submit Button -->
                <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">
                    <input tabindex="9" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="register" id="kc-register" type="submit" value="Register"/>
                </div>
            </form>
        </div>
    </div>

    <!-- Go Back Link -->
    <div class="form-group">
        <a href="http://localhost:8081" class="kc-back-link">Go Back</a>
    </div>
    </#if>

    <!-- JavaScript for Validation and Dynamic Updates -->
    <script>
        function validateForm() {
            var phoneNumber = document.getElementById("phoneNumber").value;
            var phonePattern = /^\d{1,15}$/;
            if (!phonePattern.test(phoneNumber)) {
                alert("Please enter a valid phone number with up to 15 digits.");
                return false;
            }
            return validatePassword();
        }

        function validatePassword() {
            var password = document.getElementById("password").value;
            var confirmPassword = document.getElementById("passwordConfirm").value;
            if (password !== confirmPassword) {
                alert("Passwords do not match.");
                return false;
            }
            return true;
        }

        function updateCountryCode() {
            var countrySelect = document.getElementById("country");
            var selectedOption = countrySelect.options[countrySelect.selectedIndex];
            var countryCode = selectedOption.getAttribute("data-phone-code");
            document.getElementById("countryCode").value = countryCode;
        }
    </script>
</@layout.registrationLayout>
