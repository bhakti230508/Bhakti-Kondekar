package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * 3D Chemical Molecular Node Mark as seen in the FROMCHEM SOLUTIONS brand logo.
 */
@Composable
fun MoleculeLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()

        // Center coordinates for spheres
        val centerGreen = Offset(w * 0.45f, h * 0.48f)
        val topBlue = Offset(w * 0.82f, h * 0.20f)
        val leftRed = Offset(w * 0.16f, h * 0.38f)
        val bottomYellow = Offset(w * 0.38f, h * 0.82f)

        val bondWidth = w * 0.10f

        // Draw chemical bond connectors
        drawLine(
            color = Color(0xFF10B981),
            start = centerGreen,
            end = topBlue,
            strokeWidth = bondWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFFE52521),
            start = centerGreen,
            end = leftRed,
            strokeWidth = bondWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFFF59E0B),
            start = centerGreen,
            end = bottomYellow,
            strokeWidth = bondWidth,
            cap = StrokeCap.Round
        )

        // Draw spheres with 3D gradient look
        // 1. Blue Top Sphere
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF60A5FA), Color(0xFF0251EE), Color(0xFF1E40AF)),
                center = topBlue - Offset(w * 0.05f, h * 0.05f),
                radius = w * 0.22f
            ),
            radius = w * 0.20f,
            center = topBlue
        )

        // 2. Red Left Sphere
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFF87171), Color(0xFFE52521), Color(0xFF991B1B)),
                center = leftRed - Offset(w * 0.04f, h * 0.04f),
                radius = w * 0.18f
            ),
            radius = w * 0.16f,
            center = leftRed
        )

        // 3. Yellow Bottom Sphere
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFDE047), Color(0xFFF59E0B), Color(0xFFB45309)),
                center = bottomYellow - Offset(w * 0.04f, h * 0.04f),
                radius = w * 0.18f
            ),
            radius = w * 0.16f,
            center = bottomYellow
        )

        // 4. Central Green Sphere
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF34D399), Color(0xFF10B981), Color(0xFF065F46)),
                center = centerGreen - Offset(w * 0.04f, h * 0.04f),
                radius = w * 0.18f
            ),
            radius = w * 0.16f,
            center = centerGreen
        )
    }
}

/**
 * Multi-colored Shield Icon representing waterproofing protection.
 */
@Composable
fun WaterproofShieldIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()

        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w, h * 0.25f)
            cubicTo(w, h * 0.75f, w * 0.5f, h, w * 0.5f, h)
            cubicTo(w * 0.5f, h, 0f, h * 0.75f, 0f, h * 0.25f)
            close()
        }

        // Fill white shield interior
        drawPath(path = path, color = Color.White)

        // Multi-color shield border
        drawPath(path = path, color = Color(0xFF0251EE), style = Stroke(width = w * 0.15f))

        // Center blue water droplet
        val dropPath = Path().apply {
            moveTo(w * 0.5f, h * 0.3f)
            cubicTo(w * 0.75f, h * 0.65f, w * 0.65f, h * 0.8f, w * 0.5f, h * 0.8f)
            cubicTo(w * 0.35f, h * 0.8f, w * 0.25f, h * 0.65f, w * 0.5f, h * 0.3f)
            close()
        }
        drawPath(path = dropPath, color = Color(0xFF0251EE))
    }
}

/**
 * Full Brand Logo Composable accurately reproducing the FROMCHEM SOLUTIONS logo.
 */
@Composable
fun FromchemBrandLogo(
    modifier: Modifier = Modifier,
    showSubtext: Boolean = true,
    compact: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Molecule 3D Mark
        MoleculeLogoMark(size = if (compact) 36.dp else 46.dp)

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            // Main Brand Title: "FROMCHEM"
            Text(
                text = "FROMCHEM",
                fontSize = if (compact) 16.sp else 21.sp,
                fontWeight = FontWeight.Black,
                color = FromchemPrimary,
                letterSpacing = 0.5.sp
            )

            // Second Line: Green Line + SOLUTIONS + Yellow Line
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(if (compact) 10.dp else 16.dp)
                        .height(2.5.dp)
                        .background(FromchemAccentGreen, RoundedCornerShape(2.dp))
                )

                Text(
                    text = "SOLUTIONS",
                    fontSize = if (compact) 12.sp else 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FromchemSecondaryRed,
                    letterSpacing = 0.5.sp
                )

                Box(
                    modifier = Modifier
                        .width(if (compact) 10.dp else 16.dp)
                        .height(2.5.dp)
                        .background(FromchemAccentYellow, RoundedCornerShape(2.dp))
                )
            }

            if (showSubtext) {
                Spacer(modifier = Modifier.height(2.dp))

                // Blue Horizontal Divider Rule
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(1.5.dp)
                        .background(FromchemPrimary)
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Tagline with Shield and Colored Words
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    WaterproofShieldIcon(size = if (compact) 12.dp else 14.dp)

                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = FromchemPrimary, fontWeight = FontWeight.Bold)) {
                                append("Waterproofing ")
                            }
                            withStyle(SpanStyle(color = FromchemAccentGreen, fontWeight = FontWeight.Bold)) {
                                append("& ")
                            }
                            withStyle(SpanStyle(color = FromchemSecondaryRed, fontWeight = FontWeight.Bold)) {
                                append("Construction ")
                            }
                            withStyle(SpanStyle(color = FromchemAccentYellow, fontWeight = FontWeight.Bold)) {
                                append("Chemicals")
                            }
                        },
                        fontSize = if (compact) 8.sp else 10.sp
                    )
                }
            }
        }
    }
}
