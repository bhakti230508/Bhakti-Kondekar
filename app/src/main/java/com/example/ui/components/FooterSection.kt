package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
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

@Composable
fun FooterSection(
    onLinkClick: (String) -> Unit,
    onEmailClick: () -> Unit,
    onPhoneClick: () -> Unit,
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            HorizontalDivider(color = FromchemBorder, thickness = 1.dp)

            Spacer(modifier = Modifier.height(20.dp))

            if (isWideScreen) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Brand & Copyright
                    Column {
                        FromchemBrandLogo(compact = true, showSubtext = true)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "© 2024 Fromchem Solutions. All rights reserved.",
                            fontSize = 11.sp,
                            color = FromchemTextSecondary
                        )
                    }

                    // Center Links
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Privacy Policy", "Terms of Service", "FAQ", "LinkedIn").forEach { link ->
                            Text(
                                text = link,
                                fontSize = 12.sp,
                                color = FromchemTextSecondary,
                                modifier = Modifier.clickable { onLinkClick(link) }
                            )
                        }
                    }

                    // Right Social Action Buttons & Phone Number
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = onPhoneClick,
                            shape = CircleShape,
                            color = FromchemPrimaryContainer,
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call Us",
                                    tint = FromchemPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "+91 97277 51868",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FromchemPrimary
                                )
                            }
                        }

                        Surface(
                            onClick = onEmailClick,
                            shape = CircleShape,
                            color = FromchemPrimaryContainer,
                            shadowElevation = 2.dp,
                            modifier = Modifier.testTag("footer_email_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email Us",
                                    tint = FromchemPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "fromchem6@gmail.com",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FromchemPrimary
                                )
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FromchemBrandLogo(compact = true, showSubtext = true)

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Privacy Policy", "Terms of Service", "FAQ", "LinkedIn").forEach { link ->
                            Text(
                                text = link,
                                fontSize = 11.sp,
                                color = FromchemTextSecondary,
                                modifier = Modifier.clickable { onLinkClick(link) }
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = onPhoneClick,
                            shape = CircleShape,
                            color = FromchemPrimaryContainer,
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call Us",
                                    tint = FromchemPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "+91 97277 51868",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FromchemPrimary
                                )
                            }
                        }

                        Surface(
                            onClick = onEmailClick,
                            shape = CircleShape,
                            color = FromchemPrimaryContainer,
                            shadowElevation = 2.dp,
                            modifier = Modifier.testTag("footer_email_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email Us",
                                    tint = FromchemPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "fromchem6@gmail.com",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FromchemPrimary
                                )
                            }
                        }
                    }

                    Text(
                        text = "© 2024 Fromchem Solution. All rights reserved.",
                        fontSize = 11.sp,
                        color = FromchemTextMuted
                    )
                }
            }
        }
    }
}
