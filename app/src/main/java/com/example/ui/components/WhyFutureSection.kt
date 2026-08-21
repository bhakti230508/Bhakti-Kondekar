package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class FeatureItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun WhyFutureSection(
    onFeatureClicked: (String) -> Unit,
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
    val features = listOf(
        FeatureItem("Nano-Tech Formulas", Icons.Default.Science),
        FeatureItem("Eco-Friendly", Icons.Default.Eco),
        FeatureItem("Quick Curing", Icons.Default.Timer),
        FeatureItem("15yr Warranty", Icons.Default.Security)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        if (isWideScreen) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left 2x2 Grid
                Box(modifier = Modifier.weight(1f)) {
                    FeatureGrid2x2(features = features, onFeatureClicked = onFeatureClicked)
                }

                // Right Text & Bullets
                Column(modifier = Modifier.weight(1.2f)) {
                    WhyFutureTextContent()
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                FeatureGrid2x2(features = features, onFeatureClicked = onFeatureClicked)
                WhyFutureTextContent()
            }
        }
    }
}

@Composable
private fun FeatureGrid2x2(
    features: List<FeatureItem>,
    onFeatureClicked: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureCard(feature = features[0], onClick = { onFeatureClicked(features[0].title) }, modifier = Modifier.weight(1f))
            FeatureCard(feature = features[1], onClick = { onFeatureClicked(features[1].title) }, modifier = Modifier.weight(1f))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureCard(feature = features[2], onClick = { onFeatureClicked(features[2].title) }, modifier = Modifier.weight(1f))
            FeatureCard(feature = features[3], onClick = { onFeatureClicked(features[3].title) }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun FeatureCard(
    feature: FeatureItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .testTag("feature_card_${feature.title}")
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(FromchemPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.title,
                    tint = FromchemPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = feature.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = FromchemTextPrimary
            )
        }
    }
}

@Composable
private fun WhyFutureTextContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Why Chemical Waterproofing is the Future ? .",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = FromchemTextPrimary,
            lineHeight = 34.sp
        )

        Text(
            text = "Unlike traditional bitumine membranes, our chemical solutions integrate directly with the structural substrate, creating a monolithic seal that never peels or cracks.",
            fontSize = 13.sp,
            color = FromchemTextSecondary,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bullet 1: Zero-Seam Application
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(FromchemPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Zero-Seam",
                    tint = FromchemPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Column {
                Text(
                    text = "Zero-Seam Application",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Continuous membrane means no joints for water to penetrate.",
                    fontSize = 12.sp,
                    color = FromchemTextSecondary
                )
            }
        }

        // Bullet 2: Self-Healing Properties
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(FromchemPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Self-Healing",
                    tint = FromchemPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Column {
                Text(
                    text = "Self-Healing Properties",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Active crystalline molecules re-seal hairline cracks automatically.",
                    fontSize = 12.sp,
                    color = FromchemTextSecondary
                )
            }
        }
    }
}
