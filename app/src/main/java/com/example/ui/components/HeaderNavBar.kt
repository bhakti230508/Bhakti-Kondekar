package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

import androidx.compose.material.icons.filled.AutoAwesome

import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person

@Composable
fun HeaderNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onGetQuoteClicked: () -> Unit,
    isDesktopMode: Boolean,
    onToggleDesktopMode: () -> Unit,
    currentUser: UserProfile? = null,
    onAccountClicked: () -> Unit,
    onAiCameraClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Official FROMCHEM Brand Logo
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onTabSelected("Services") }
                    .padding(vertical = 2.dp)
            ) {
                FromchemBrandLogo(
                    compact = !isDesktopMode,
                    showSubtext = isDesktopMode
                )
            }

            // Desktop Nav Navigation Items
            if (isDesktopMode) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(FromchemSurfaceVariant)
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val navItems = listOf("Services", "Projects", "About", "Contact")
                    navItems.forEach { item ->
                        val isSelected = selectedTab == item
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) FromchemPrimaryContainer else Color.Transparent)
                                .clickable { onTabSelected(item) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = item,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) FromchemPrimary else FromchemTextSecondary
                            )
                        }
                    }
                }
            }

            // Right Action Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                // AI Leak Camera Scanner Button
                IconButton(
                    onClick = onAiCameraClicked,
                    modifier = Modifier
                        .testTag("ai_camera_header_button")
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(FromchemPrimaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "AI Leak Detector Camera",
                        tint = FromchemPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Desktop / Mobile Mode Switcher
                IconButton(
                    onClick = onToggleDesktopMode,
                    modifier = Modifier
                        .testTag("toggle_mode_button")
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(FromchemSurfaceVariant)
                ) {
                    Icon(
                        imageVector = if (isDesktopMode) Icons.Default.Smartphone else Icons.Default.Computer,
                        contentDescription = "Toggle Desktop/Android View",
                        tint = FromchemPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // User Account / Login Button
                if (currentUser != null) {
                    Surface(
                        onClick = onAccountClicked,
                        shape = RoundedCornerShape(20.dp),
                        color = FromchemPrimaryContainer,
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("header_account_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = FromchemPrimary,
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = currentUser.fullName.take(1).uppercase(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentUser.fullName.substringBefore(" "),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FromchemPrimary
                            )
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = onAccountClicked,
                        modifier = Modifier
                            .testTag("header_login_button")
                            .height(38.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FromchemPrimary),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(FromchemPrimary)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Sign In",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sign In",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Get Quote Button
                Button(
                    onClick = onGetQuoteClicked,
                    modifier = Modifier
                        .testTag("get_quote_button")
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FromchemPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Get Quote",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
