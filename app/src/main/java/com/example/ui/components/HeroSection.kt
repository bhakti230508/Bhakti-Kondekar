package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HeroSection(
    onViewCaseStudiesClicked: () -> Unit = {},
    onScanLeakClicked: () -> Unit = {},
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
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
                // Left Text Content
                Column(modifier = Modifier.weight(1f)) {
                    HeroTextContent(onScanLeakClicked = onScanLeakClicked)
                }
                // Right Hero Graphic
                Box(modifier = Modifier.weight(1f)) {
                    HeroWorkerGraphic()
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HeroTextContent(onScanLeakClicked = onScanLeakClicked)
                HeroWorkerGraphic()
            }
        }
    }
}

@Composable
private fun HeroTextContent(
    onScanLeakClicked: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top ISO Badge
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(FromchemPrimaryContainer)
                    .border(1.dp, FromchemBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "ISO Certified",
                        tint = FromchemPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "ISO 9001:2015 CERTIFIED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FromchemPrimary,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Headline with highlighted "Waterproofing"
        val headlineText = buildAnnotatedString {
            append("Advanced\nChemical\n")
            withStyle(
                style = SpanStyle(
                    color = FromchemPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
            ) {
                append("Waterproofing\n")
            }
            append("Solutions")
        }

        Text(
            text = headlineText,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = FromchemTextPrimary,
            lineHeight = 42.sp
        )

        // Subtitle
        Text(
            text = "Protecting your structures with cutting-edge chemical technology. From industrial basements to residential rooftops, we provide impenetrable barriers.",
            fontSize = 14.sp,
            color = FromchemTextSecondary,
            lineHeight = 22.sp
        )

        // AI Leak Detector Quick Action
        Surface(
            onClick = onScanLeakClicked,
            shape = RoundedCornerShape(16.dp),
            color = FromchemPrimary,
            shadowElevation = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clickable { onScanLeakClicked() }
                .testTag("hero_leak_detector_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "AI Leak Detector",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Scan wall / ceiling for defect diagnosis",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
