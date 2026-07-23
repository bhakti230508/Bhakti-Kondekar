package com.example.ui.components

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class InquiryFormState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val serviceType: String = "Roofing Systems",
    val timeline: String = "Immediate (1-7 Days)",
    val message: String = "",
    val fullNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val messageError: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsSection(
    isWideScreen: Boolean,
    onInquirySubmitted: (InquiryFormState, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var formState by remember { mutableStateOf(InquiryFormState()) }
    var isSubmitting by remember { mutableStateOf(false) }
    var submittedTicketId by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val serviceOptions = listOf(
        "Roofing Systems",
        "Basement Tanking",
        "Industrial Floor Coating",
        "Crystalline Sealer",
        "General Consultation"
    )

    val timelineOptions = listOf(
        "Immediate (1-7 Days)",
        "Within 1 Month",
        "Planning Phase (>1 Month)"
    )

    fun validateForm(): Boolean {
        var isValid = true

        val nameErr = when {
            formState.fullName.isBlank() -> "Full name is required."
            formState.fullName.trim().length < 2 -> "Name must be at least 2 characters."
            else -> null
        }

        val emailErr = when {
            formState.email.isBlank() -> "Email address is required."
            !Patterns.EMAIL_ADDRESS.matcher(formState.email.trim()).matches() -> "Please enter a valid email address (e.g. user@domain.com)."
            else -> null
        }

        val phoneErr = when {
            formState.phone.isNotBlank() && formState.phone.replace(Regex("[^0-9]"), "").length < 7 -> "Please enter a valid phone number."
            else -> null
        }

        val msgErr = when {
            formState.message.isBlank() -> "Project details are required."
            formState.message.trim().length < 10 -> "Please provide at least 10 characters describing your project."
            else -> null
        }

        if (nameErr != null || emailErr != null || phoneErr != null || msgErr != null) {
            isValid = false
        }

        formState = formState.copy(
            fullNameError = nameErr,
            emailError = emailErr,
            phoneError = phoneErr,
            messageError = msgErr
        )

        return isValid
    }

    fun submitInquiry() {
        if (!validateForm()) return

        isSubmitting = true
        coroutineScope.launch {
            delay(1200) // Simulate network submission to Fromchem backend
            val ticketId = "FC-" + (100000..999999).random()
            submittedTicketId = ticketId
            isSubmitting = false
            onInquirySubmitted(formState, ticketId)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .testTag("contact_us_section_card"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(FromchemPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Contact Us",
                        tint = FromchemPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = "Contact Fromchem Solution",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FromchemTextPrimary
                    )
                    Text(
                        text = "Speak with a Certified Chemical Waterproofing Engineer",
                        fontSize = 12.sp,
                        color = FromchemTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Direct Call & Email Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("contact_info_badge"),
                shape = RoundedCornerShape(16.dp),
                color = FromchemPrimaryContainer.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, FromchemPrimary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(FromchemPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Phone",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Direct Helpline",
                                    fontSize = 11.sp,
                                    color = FromchemTextSecondary
                                )
                                Text(
                                    text = "+91 97277 51868",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FromchemPrimary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = FromchemPrimary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Call Now",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = FromchemPrimary.copy(alpha = 0.15f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FromchemPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Owner Email",
                                fontSize = 11.sp,
                                color = FromchemTextSecondary
                            )
                            Text(
                                text = "fromchem6@gmail.com",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FromchemPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (submittedTicketId != null) {
                // Success Confirmation Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_success_card"),
                    colors = CardDefaults.cardColors(containerColor = FromchemPrimaryContainer.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, FromchemPrimaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(FromchemAccentGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Success",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = "Inquiry Sent Successfully!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FromchemTextPrimary
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Inquiry ID: ",
                                    fontSize = 12.sp,
                                    color = FromchemTextSecondary
                                )
                                Text(
                                    text = submittedTicketId ?: "",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FromchemPrimary
                                )
                            }
                        }

                        Text(
                            text = "Thank you, ${formState.fullName}! Our chemical engineering technical team has received your inquiry regarding ${formState.serviceType}. We will review your project details and contact you at ${formState.email} within 2 business hours.",
                            fontSize = 13.sp,
                            color = FromchemTextSecondary,
                            lineHeight = 18.sp
                        )

                        HorizontalDivider(color = FromchemBorder.copy(alpha = 0.5f))

                        Button(
                            onClick = {
                                formState = InquiryFormState()
                                submittedTicketId = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("contact_reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Submit Another Inquiry")
                        }
                    }
                }
            } else {
                // Form Fields
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isWideScreen) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Name
                            OutlinedTextField(
                                value = formState.fullName,
                                onValueChange = {
                                    formState = formState.copy(fullName = it, fullNameError = null)
                                },
                                label = { Text("Full Name *") },
                                placeholder = { Text("e.g. John Doe") },
                                isError = formState.fullNameError != null,
                                supportingText = {
                                    formState.fullNameError?.let {
                                        Text(it, color = FromchemAccentRed, fontSize = 11.sp)
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = FromchemTextMuted)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("contact_name_input"),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )

                            // Email
                            OutlinedTextField(
                                value = formState.email,
                                onValueChange = {
                                    formState = formState.copy(email = it, emailError = null)
                                },
                                label = { Text("Email Address *") },
                                placeholder = { Text("e.g. john@company.com") },
                                isError = formState.emailError != null,
                                supportingText = {
                                    formState.emailError?.let {
                                        Text(it, color = FromchemAccentRed, fontSize = 11.sp)
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = FromchemTextMuted)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("contact_email_input"),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )
                        }
                    } else {
                        // Name Mobile
                        OutlinedTextField(
                            value = formState.fullName,
                            onValueChange = {
                                formState = formState.copy(fullName = it, fullNameError = null)
                            },
                            label = { Text("Full Name *") },
                            placeholder = { Text("e.g. John Doe") },
                            isError = formState.fullNameError != null,
                            supportingText = {
                                formState.fullNameError?.let {
                                    Text(it, color = FromchemAccentRed, fontSize = 11.sp)
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = FromchemTextMuted)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_name_input"),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        // Email Mobile
                        OutlinedTextField(
                            value = formState.email,
                            onValueChange = {
                                formState = formState.copy(email = it, emailError = null)
                            },
                            label = { Text("Email Address *") },
                            placeholder = { Text("e.g. john@company.com") },
                            isError = formState.emailError != null,
                            supportingText = {
                                formState.emailError?.let {
                                    Text(it, color = FromchemAccentRed, fontSize = 11.sp)
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = FromchemTextMuted)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_email_input"),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )
                    }

                    // Phone Number
                    OutlinedTextField(
                        value = formState.phone,
                        onValueChange = {
                            formState = formState.copy(phone = it, phoneError = null)
                        },
                        label = { Text("Phone Number (Optional)") },
                        placeholder = { Text("e.g. +1 (555) 019-2834") },
                        isError = formState.phoneError != null,
                        supportingText = {
                            formState.phoneError?.let {
                                Text(it, color = FromchemAccentRed, fontSize = 11.sp)
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = FromchemTextMuted)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contact_phone_input"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    // Service Selection Chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Required Service / Solution:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FromchemTextPrimary
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            serviceOptions.take(3).forEach { option ->
                                val isSelected = formState.serviceType == option
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { formState = formState.copy(serviceType = option) },
                                    label = { Text(option, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = FromchemPrimaryContainer,
                                        selectedLabelColor = FromchemPrimary
                                    )
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            serviceOptions.drop(3).forEach { option ->
                                val isSelected = formState.serviceType == option
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { formState = formState.copy(serviceType = option) },
                                    label = { Text(option, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = FromchemPrimaryContainer,
                                        selectedLabelColor = FromchemPrimary
                                    )
                                )
                            }
                        }
                    }

                    // Timeline Selection
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Project Start Timeline:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FromchemTextPrimary
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            timelineOptions.forEach { t ->
                                val selected = formState.timeline == t
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { formState = formState.copy(timeline = t) }
                                        .padding(vertical = 4.dp, horizontal = 4.dp)
                                ) {
                                    RadioButton(
                                        selected = selected,
                                        onClick = { formState = formState.copy(timeline = t) },
                                        colors = RadioButtonDefaults.colors(selectedColor = FromchemPrimary)
                                    )
                                    Text(
                                        text = t.split(" ").first(),
                                        fontSize = 11.sp,
                                        color = if (selected) FromchemPrimary else FromchemTextSecondary,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // Message / Project Details
                    OutlinedTextField(
                        value = formState.message,
                        onValueChange = {
                            formState = formState.copy(message = it, messageError = null)
                        },
                        label = { Text("Project Details / Inquiry Message *") },
                        placeholder = { Text("Describe structural conditions, surface area, current dampness or leak symptoms...") },
                        isError = formState.messageError != null,
                        supportingText = {
                            formState.messageError?.let {
                                Text(it, color = FromchemAccentRed, fontSize = 11.sp)
                            } ?: Text("Min 10 characters", fontSize = 10.sp, color = FromchemTextMuted)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Build, contentDescription = null, tint = FromchemTextMuted)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("contact_message_input"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Submit Action Button
                    Button(
                        onClick = { submitInquiry() },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("contact_submit_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FromchemPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Submitting Inquiry...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Send Inquiry to Chemical Engineer",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsDialog(
    onDismiss: () -> Unit,
    onInquirySubmitted: (InquiryFormState, String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("contact_dialog_close")
            ) {
                Text("Close", color = FromchemTextSecondary)
            }
        },
        title = null,
        text = {
            Box(modifier = Modifier.fillMaxWidth()) {
                ContactUsSection(
                    isWideScreen = false,
                    onInquirySubmitted = { form, id ->
                        onInquirySubmitted(form, id)
                    }
                )
            }
        },
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(24.dp)
    )
}
