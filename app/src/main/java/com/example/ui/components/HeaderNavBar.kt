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

@Composable
fun HeaderNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onGetQuoteClicked: () -> Unit,
    isDesktopMode: Boolean,
    onToggleDesktopMode: () -> Unit,
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
            // Brand Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onTabSelected("Services") }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(FromchemPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Fromchem Logo",
                        tint = FromchemPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Fromchem Solution",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextPrimary
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

                // Get Quote Button
                Button(
                    onClick = onGetQuoteClicked,
                    modifier = Modifier
                        .testTag("get_quote_button")
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FromchemPrimaryContainer,
                        contentColor = FromchemPrimary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
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
