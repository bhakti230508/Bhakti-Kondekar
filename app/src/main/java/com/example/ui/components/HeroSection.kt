package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Verified
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
    onExploreSolutionsClicked: () -> Unit,
    onViewCaseStudiesClicked: () -> Unit,
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
                    HeroTextContent(
                        onExploreSolutionsClicked = onExploreSolutionsClicked,
                        onViewCaseStudiesClicked = onViewCaseStudiesClicked
                    )
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
                HeroTextContent(
                    onExploreSolutionsClicked = onExploreSolutionsClicked,
                    onViewCaseStudiesClicked = onViewCaseStudiesClicked
                )
                HeroWorkerGraphic()
            }
        }
    }
}

@Composable
private fun HeroTextContent(
    onExploreSolutionsClicked: () -> Unit,
    onViewCaseStudiesClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top ISO Badge
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
                    text = "ISO CERTIFIED CHEMICALS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemPrimary,
                    letterSpacing = 0.5.sp
                )
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

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Primary Explore Solutions Button
            Button(
                onClick = onExploreSolutionsClicked,
                modifier = Modifier
                    .testTag("explore_solutions_button")
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FromchemPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Explore Solutions",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Arrow Forward",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Secondary View Case Studies Button
            OutlinedButton(
                onClick = onViewCaseStudiesClicked,
                modifier = Modifier
                    .testTag("view_case_studies_button")
                    .height(46.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = FromchemTextPrimary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                Text(
                    text = "View Case Studies",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
