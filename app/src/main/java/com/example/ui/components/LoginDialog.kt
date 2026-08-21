package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

data class UserProfile(
    val fullName: String,
    val email: String,
    val phoneNumber: String = "",
    val companyName: String = "",
    val role: String = "Contractor",
    val gstNumber: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onLoginSuccess: (UserProfile) -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }

    // Login Form Fields
    var loginEmailOrPhone by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // Register Form Fields
    var regFullName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regCompany by remember { mutableStateOf("") }
    var regRole by remember { mutableStateOf("Applicator / Contractor") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regGstNumber by remember { mutableStateOf("") }
    var isRegPasswordVisible by remember { mutableStateOf(false) }
    var regError by remember { mutableStateOf<String?>(null) }

    // OTP Simulated State
    var showOtpStep by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }

    val roles = listOf(
        "Applicator / Contractor",
        "Civil Engineer",
        "Architect / Consultant",
        "Project Manager",
        "Building / Property Owner"
    )
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 520.dp)
                .wrapContentHeight()
                .padding(vertical = 16.dp)
                .testTag("login_dialog_surface"),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Bar with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FromchemBrandLogo(compact = true, showSubtext = false)

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(FromchemSurfaceVariant)
                            .testTag("login_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Login",
                            tint = FromchemTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Welcome Header
                Text(
                    text = if (isRegisterMode) "Create Contractor Account" else "Sign In to Fromchem Portal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextPrimary
                )

                Text(
                    text = if (isRegisterMode) 
                        "Register for bulk chemical trade pricing, product data sheets, and project tracking."
                    else 
                        "Access your waterproofing quotes, technical specs, and order history.",
                    fontSize = 12.sp,
                    color = FromchemTextSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Sign In / Create Account Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(FromchemSurfaceVariant)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isRegisterMode) Color.White else Color.Transparent)
                            .clickable {
                                isRegisterMode = false
                                showOtpStep = false
                                loginError = null
                            }
                            .padding(vertical = 10.dp)
                            .testTag("tab_login"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign In",
                            fontSize = 13.sp,
                            fontWeight = if (!isRegisterMode) FontWeight.Bold else FontWeight.Medium,
                            color = if (!isRegisterMode) FromchemPrimary else FromchemTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isRegisterMode) Color.White else Color.Transparent)
                            .clickable {
                                isRegisterMode = true
                                showOtpStep = false
                                regError = null
                            }
                            .padding(vertical = 10.dp)
                            .testTag("tab_register"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Create Account",
                            fontSize = 13.sp,
                            fontWeight = if (isRegisterMode) FontWeight.Bold else FontWeight.Medium,
                            color = if (isRegisterMode) FromchemPrimary else FromchemTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Content View
                if (!isRegisterMode) {
                    if (showOtpStep) {
                        // OTP verification view
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = FromchemPrimaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sms,
                                        contentDescription = null,
                                        tint = FromchemPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Verification code sent via SMS to $loginEmailOrPhone",
                                        fontSize = 12.sp,
                                        color = FromchemPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = { if (it.length <= 6) otpCode = it },
                                label = { Text("Enter 6-Digit OTP") },
                                placeholder = { Text("e.g. 582910") },
                                leadingIcon = {
                                    Icon(Icons.Default.Pin, contentDescription = null, tint = FromchemPrimary)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_input")
                            )

                            Button(
                                onClick = {
                                    if (otpCode.length == 6) {
                                        val user = UserProfile(
                                            fullName = if (loginEmailOrPhone.contains("@")) loginEmailOrPhone.substringBefore("@") else "Contractor Partner",
                                            email = if (loginEmailOrPhone.contains("@")) loginEmailOrPhone else "contractor@fromchem.com",
                                            phoneNumber = if (loginEmailOrPhone.contains("@")) "+91 97277 51868" else loginEmailOrPhone,
                                            companyName = "Apex Waterproofing Solutions",
                                            role = "Applicator / Contractor"
                                        )
                                        onLoginSuccess(user)
                                        onDismiss()
                                    } else {
                                        loginError = "Please enter valid 6-digit OTP code (Demo: 123456)"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("verify_otp_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Verify & Sign In", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            TextButton(
                                onClick = { showOtpStep = false },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("← Back to password sign in", fontSize = 12.sp, color = FromchemTextSecondary)
                            }
                        }
                    } else {
                        // Standard Email/Password Sign In
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Demo auto-fill banner
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = FromchemSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = FromchemPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Testing Demo Account",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FromchemTextPrimary
                                        )
                                    }

                                    TextButton(
                                        onClick = {
                                            loginEmailOrPhone = "contractor@fromchem.com"
                                            loginPassword = "Fromchem@2026"
                                            loginError = null
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Auto-fill Credentials", fontSize = 11.sp, color = FromchemPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (loginError != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEE2E2),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = loginError!!,
                                        fontSize = 12.sp,
                                        color = FromchemSecondaryRed,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            // Email / Phone Field
                            OutlinedTextField(
                                value = loginEmailOrPhone,
                                onValueChange = {
                                    loginEmailOrPhone = it
                                    loginError = null
                                },
                                label = { Text("Email or Registered Mobile Number") },
                                placeholder = { Text("contractor@fromchem.com") },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = FromchemPrimary)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_email_input")
                            )

                            // Password Field
                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = {
                                    loginPassword = it
                                    loginError = null
                                },
                                label = { Text("Password") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = FromchemPrimary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password visibility",
                                            tint = FromchemTextSecondary
                                        )
                                    }
                                },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input")
                            )

                            // Remember Me & Forgot Password Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = rememberMe,
                                        onCheckedChange = { rememberMe = it },
                                        colors = CheckboxDefaults.colors(checkedColor = FromchemPrimary),
                                        modifier = Modifier.testTag("remember_me_checkbox")
                                    )
                                    Text("Remember me", fontSize = 12.sp, color = FromchemTextSecondary)
                                }

                                TextButton(
                                    onClick = {
                                        if (loginEmailOrPhone.isNotBlank()) {
                                            showOtpStep = true
                                            loginError = null
                                        } else {
                                            loginError = "Enter your email or phone number first to request OTP reset."
                                        }
                                    }
                                ) {
                                    Text("Forgot password?", fontSize = 12.sp, color = FromchemPrimary, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            // Submit Sign In Button
                            Button(
                                onClick = {
                                    if (loginEmailOrPhone.isBlank()) {
                                        loginError = "Please enter your registered Email or Mobile number."
                                    } else if (loginPassword.length < 4) {
                                        loginError = "Please enter your password."
                                    } else {
                                        val user = UserProfile(
                                            fullName = if (loginEmailOrPhone.contains("contractor")) "Rajesh Patel" else loginEmailOrPhone.substringBefore("@").replaceFirstChar { it.uppercase() },
                                            email = if (loginEmailOrPhone.contains("@")) loginEmailOrPhone else "$loginEmailOrPhone@fromchem.com",
                                            phoneNumber = "+91 97277 51868",
                                            companyName = "Apex Chemical Applicators & Contractors",
                                            role = "Applicator / Contractor",
                                            gstNumber = "24AAACF1234F1Z8"
                                        )
                                        onLoginSuccess(user)
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("login_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Sign In to Portal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            // Quick OTP / Google Sign-In divider
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HorizontalDivider(modifier = Modifier.weight(1f), color = FromchemBorder)
                                Text(
                                    text = "  OR QUICK ACCESS  ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FromchemTextMuted
                                )
                                HorizontalDivider(modifier = Modifier.weight(1f), color = FromchemBorder)
                            }

                            // Mobile OTP Quick Button
                            OutlinedButton(
                                onClick = {
                                    if (loginEmailOrPhone.isNotBlank()) {
                                        showOtpStep = true
                                        loginError = null
                                    } else {
                                        loginEmailOrPhone = "+91 97277 51868"
                                        showOtpStep = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("login_otp_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(FromchemBorder))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = FromchemPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sign In with Mobile OTP", fontSize = 12.sp, color = FromchemTextPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                } else {
                    // Create Account Registration Form
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (regError != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFEE2E2),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = regError!!,
                                    fontSize = 12.sp,
                                    color = FromchemSecondaryRed,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        // Full Name
                        OutlinedTextField(
                            value = regFullName,
                            onValueChange = { regFullName = it; regError = null },
                            label = { Text("Full Name *") },
                            placeholder = { Text("e.g. Rajeshkumar Patel") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FromchemPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_name_input")
                        )

                        // Email
                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it; regError = null },
                            label = { Text("Business Email Address *") },
                            placeholder = { Text("rajesh@apexwaterproofing.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = FromchemPrimary) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_email_input")
                        )

                        // Mobile Number
                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it; regError = null },
                            label = { Text("Mobile Number (WhatsApp) *") },
                            placeholder = { Text("+91 98250 12345") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FromchemPrimary) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_phone_input")
                        )

                        // Company / Firm Name
                        OutlinedTextField(
                            value = regCompany,
                            onValueChange = { regCompany = it },
                            label = { Text("Company / Firm Name (Optional)") },
                            placeholder = { Text("e.g. Apex Construction Solutions") },
                            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = FromchemPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_company_input")
                        )

                        // Professional Role Dropdown
                        ExposedDropdownMenuBox(
                            expanded = roleDropdownExpanded,
                            onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = regRole,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Professional Category") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = FromchemPrimary) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("register_role_dropdown")
                            )

                            ExposedDropdownMenu(
                                expanded = roleDropdownExpanded,
                                onDismissRequest = { roleDropdownExpanded = false }
                            ) {
                                roles.forEach { role ->
                                    DropdownMenuItem(
                                        text = { Text(role, fontSize = 13.sp) },
                                        onClick = {
                                            regRole = role
                                            roleDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // GST Number
                        OutlinedTextField(
                            value = regGstNumber,
                            onValueChange = { regGstNumber = it.uppercase() },
                            label = { Text("GST Number (For Tax Invoice & Discounts)") },
                            placeholder = { Text("e.g. 24AAACF1234F1Z8") },
                            leadingIcon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = FromchemPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_gst_input")
                        )

                        // Password Field
                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it; regError = null },
                            label = { Text("Create Password *") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FromchemPrimary) },
                            trailingIcon = {
                                IconButton(onClick = { isRegPasswordVisible = !isRegPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isRegPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = FromchemTextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (isRegPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_password_input")
                        )

                        // Submit Registration Button
                        Button(
                            onClick = {
                                if (regFullName.isBlank()) {
                                    regError = "Please enter your full name."
                                } else if (regEmail.isBlank() || !regEmail.contains("@")) {
                                    regError = "Please enter a valid email address."
                                } else if (regPhone.isBlank()) {
                                    regError = "Please enter your mobile contact number."
                                } else if (regPassword.length < 6) {
                                    regError = "Password must be at least 6 characters long."
                                } else {
                                    val user = UserProfile(
                                        fullName = regFullName,
                                        email = regEmail,
                                        phoneNumber = regPhone,
                                        companyName = if (regCompany.isNotBlank()) regCompany else "Independent Applicator",
                                        role = regRole,
                                        gstNumber = regGstNumber
                                    )
                                    onLoginSuccess(user)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(top = 8.dp)
                                .testTag("register_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Register & Access Portal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserProfileDialog(
    user: UserProfile,
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 480.dp)
                .padding(16.dp)
                .testTag("user_profile_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = FromchemPrimaryContainer,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = user.fullName.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = FromchemPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = user.fullName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = FromchemTextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FromchemPrimaryContainer,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = user.role,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FromchemPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Profile", tint = FromchemTextSecondary)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = FromchemBorder)

                // Account Details Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = FromchemSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProfileDetailRow(icon = Icons.Default.Email, label = "Email", value = user.email)
                        ProfileDetailRow(icon = Icons.Default.Phone, label = "Mobile", value = if (user.phoneNumber.isNotBlank()) user.phoneNumber else "+91 97277 51868")
                        ProfileDetailRow(icon = Icons.Default.Business, label = "Company", value = if (user.companyName.isNotBlank()) user.companyName else "Individual Applicator")
                        if (user.gstNumber.isNotBlank()) {
                            ProfileDetailRow(icon = Icons.Default.ReceiptLong, label = "GSTIN", value = user.gstNumber)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contractor Tier Perk Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = FromchemPrimaryContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FromchemPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = FromchemPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Verified Fromchem Trade Partner",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FromchemPrimary
                            )
                            Text(
                                text = "12% Trade Discount unlocked on Polyurea & Crystalline orders.",
                                fontSize = 11.sp,
                                color = FromchemTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onLogout()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FromchemSecondaryRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FromchemSecondaryRed.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary)
                    ) {
                        Text("Close Portal", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FromchemPrimary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = FromchemTextSecondary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = FromchemTextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}
