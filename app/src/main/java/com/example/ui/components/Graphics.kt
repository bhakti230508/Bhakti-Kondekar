package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Custom Canvas & Layered Composables simulating high-fidelity visuals matching the design images.
 */

@Composable
fun HeroWorkerGraphic(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFE2E8F0))
            .border(1.dp, FromchemBorder, RoundedCornerShape(24.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Open Cloudy Sky Gradient Background
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFCBD5E1), // Soft overcast sky top
                        Color(0xFFE2E8F0), // Light cloud mid
                        Color(0xFFF1F5F9)  // Horizon haze
                    )
                ),
                size = Size(w, h * 0.45f)
            )

            // Distant soft clouds
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = w * 0.3f,
                center = Offset(w * 0.8f, h * 0.15f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = w * 0.25f,
                center = Offset(w * 0.2f, h * 0.18f)
            )

            // 2. Distant Horizon Line - Trees & Industrial Structures
            val horizonY = h * 0.42f
            drawRect(
                color = Color(0xFF64748B).copy(alpha = 0.3f), // Distant buildings
                topLeft = Offset(w * 0.55f, horizonY - 14f),
                size = Size(w * 0.2f, 14f)
            )
            // Distant green tree line silhouette
            val treePath = Path().apply {
                moveTo(w * 0.7f, horizonY)
                lineTo(w * 0.72f, horizonY - 20f)
                lineTo(w * 0.75f, horizonY - 32f)
                lineTo(w * 0.78f, horizonY - 15f)
                lineTo(w * 0.82f, horizonY - 28f)
                lineTo(w * 0.86f, horizonY - 35f)
                lineTo(w * 0.90f, horizonY - 18f)
                lineTo(w * 0.95f, horizonY - 25f)
                lineTo(w, horizonY)
                close()
            }
            drawPath(path = treePath, color = Color(0xFF334155).copy(alpha = 0.4f))

            // 3. Rooftop Concrete Deck & Chemical Coating Perspective
            // Uncoated concrete deck base
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8))
                ),
                topLeft = Offset(0f, horizonY),
                size = Size(w, h - horizonY)
            )

            // Fresh Grey/White Liquid Waterproof Coating Area
            val coatingPath = Path().apply {
                moveTo(0f, horizonY + 12f)
                lineTo(w, horizonY + 8f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = coatingPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFC), // Fresh wet glossy elastomeric chemical coat
                        Color(0xFFE2E8F0)
                    )
                )
            )

            // Parapet Wall Border on Right
            val parapetPath = Path().apply {
                moveTo(w * 0.85f, h)
                lineTo(w, h * 0.82f)
                lineTo(w, h)
                close()
            }
            drawPath(path = parapetPath, color = Color(0xFFDC2626).copy(alpha = 0.7f)) // Terracotta edge cap

            // Parapet top edge cap
            drawLine(
                color = Color(0xFFB91C1C),
                start = Offset(w * 0.85f, h),
                end = Offset(w, h * 0.82f),
                strokeWidth = 10f
            )

            // 4. Worker Workboot / Foot on Left (Standing on Deck)
            drawOval(
                color = Color(0xFF334155),
                topLeft = Offset(0f, h * 0.65f),
                size = Size(w * 0.15f, h * 0.12f)
            )
            drawOval(
                color = Color(0xFF1E293B),
                topLeft = Offset(-10f, h * 0.67f),
                size = Size(w * 0.12f, h * 0.09f)
            )

            // 5. Long-Handled Chemical Paint Roller Applicator Tool
            // Roller Contact Position on rooftop floor
            val rollerX = w * 0.48f
            val rollerY = h * 0.76f

            // A. Long Extension Pole (Yellow Shaft)
            val poleStart = Offset(w * 0.18f, h * 0.02f) // Handled from top left
            val poleEnd = Offset(w * 0.38f, h * 0.52f)

            drawLine(
                color = Color(0xFFEAB308), // Bright Yellow Extension Pole
                start = poleStart,
                end = poleEnd,
                strokeWidth = 14f,
                cap = StrokeCap.Round
            )
            // Pole highlight line
            drawLine(
                color = Color(0xFFFEF08A),
                start = poleStart,
                end = poleEnd,
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )

            // B. Bright Orange Connector Grip Sleeve
            val orangeGripStart = Offset(w * 0.37f, h * 0.50f)
            val orangeGripEnd = Offset(w * 0.41f, h * 0.60f)
            drawLine(
                color = Color(0xFFEA580C), // Industrial Orange Collar
                start = orangeGripStart,
                end = orangeGripEnd,
                strokeWidth = 22f,
                cap = StrokeCap.Round
            )

            // C. Black Steel Roller Frame Rod
            val frameJoint = Offset(w * 0.42f, h * 0.64f)
            val rollerAxle = Offset(rollerX - 18f, rollerY)

            drawLine(
                color = Color(0xFF1E293B),
                start = orangeGripEnd,
                end = frameJoint,
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFF1E293B),
                start = frameJoint,
                end = rollerAxle,
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )

            // D. Plush Grey Chemical Applicator Roller Cylinder
            val rollerWidth = w * 0.22f
            val rollerHeight = h * 0.09f

            // Shadow under roller
            drawOval(
                color = Color(0xFF475569).copy(alpha = 0.4f),
                topLeft = Offset(rollerX - 10f, rollerY + 12f),
                size = Size(rollerWidth + 20f, rollerHeight)
            )

            // Roller Cylinder Body (Light Grey Texture)
            drawOval(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF1F5F9), // Top highlight
                        Color(0xFFCBD5E1), // Main nap fabric
                        Color(0xFF64748B)  // Bottom shadow
                    )
                ),
                topLeft = Offset(rollerX, rollerY - rollerHeight / 2f),
                size = Size(rollerWidth, rollerHeight)
            )

            // Roller End Cap detail
            drawCircle(
                color = Color(0xFF94A3B8),
                radius = rollerHeight / 2.2f,
                center = Offset(rollerX, rollerY)
            )
            drawCircle(
                color = Color(0xFF475569),
                radius = rollerHeight / 4f,
                center = Offset(rollerX, rollerY)
            )

            // Chemical Liquid Wet Track line left behind roller
            drawRect(
                color = Color.White.copy(alpha = 0.4f),
                topLeft = Offset(rollerX + 20f, rollerY + 10f),
                size = Size(w * 0.25f, 8f)
            )
        }

        // Overlay Stat Badge (Bottom Left)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(FromchemAccentGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% Seamless Elastomeric Membrane",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FromchemPrimary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Professional Rooftop Liquid Coating Application",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FromchemTextPrimary
                )
            }
        }
    }
}

@Composable
fun RoofingSystemGraphic(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE2E8F0))
            .border(1.dp, FromchemBorder, RoundedCornerShape(16.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Sky & Clouds
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFCBD5E1), Color(0xFFF1F5F9))
                ),
                size = Size(w, h * 0.45f)
            )

            // Rooftop Floor & Coating
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
                ),
                topLeft = Offset(0f, h * 0.45f),
                size = Size(w, h * 0.55f)
            )

            // Roller Application
            val rx = w * 0.52f
            val ry = h * 0.72f

            // Yellow Pole
            drawLine(
                color = Color(0xFFEAB308),
                start = Offset(w * 0.22f, 0f),
                end = Offset(w * 0.42f, h * 0.52f),
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )
            // Orange Collar
            drawLine(
                color = Color(0xFFEA580C),
                start = Offset(w * 0.42f, h * 0.52f),
                end = Offset(w * 0.46f, h * 0.6f),
                strokeWidth = 12f,
                cap = StrokeCap.Round
            )
            // Black Frame
            drawLine(
                color = Color(0xFF1E293B),
                start = Offset(w * 0.46f, h * 0.6f),
                end = Offset(rx - 10f, ry),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )

            // Roller Cylinder
            drawOval(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFF1F5F9), Color(0xFF94A3B8))
                ),
                topLeft = Offset(rx, ry - 12f),
                size = Size(w * 0.22f, 24f)
            )
        }

        // Project Spec Label Badge on Roof
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(FromchemAccentGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Rooftop Elastomeric Roller Coat",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = FromchemTextPrimary
                    )
                }
                Text(
                    text = "Seamless Liquid Chemical Membrane",
                    fontSize = 8.sp,
                    color = FromchemPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun BasementTankingGraphic(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Dark masonry basement background
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF334155), Color(0xFF0F172A)),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.6f
                )
            )

            // Masonry brick grid lines
            for (y in 0..5) {
                drawLine(
                    color = Color(0x22FFFFFF),
                    start = Offset(0f, y * h / 5f),
                    end = Offset(w, y * h / 5f),
                    strokeWidth = 1f
                )
            }

            // Chemical Tanking Bucket
            drawRect(
                color = Color(0xFFE2E8F0),
                topLeft = Offset(w * 0.45f, h * 0.65f),
                size = Size(w * 0.1f, h * 0.25f)
            )

            // Bright Floodlight spotlight beams
            val beamLeft = Path().apply {
                moveTo(w * 0.1f, h * 0.8f)
                lineTo(w * 0.45f, 0f)
                lineTo(w * 0.65f, 0f)
            }
            drawPath(
                path = beamLeft,
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x44FFFFFF), Color(0x00FFFFFF)),
                    center = Offset(w * 0.1f, h * 0.8f),
                    radius = w * 0.5f
                )
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.8f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Heavy-Duty Chemical Tanking",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF93C5FD)
            )
        }
    }
}

@Composable
fun IndustrialCoatingGraphic(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF334155))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // High-gloss warehouse floor perspective
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E293B), Color(0xFF475569))
                )
            )

            // Perspective grid on floor
            for (i in -5..15) {
                drawLine(
                    color = Color(0x22FFFFFF),
                    start = Offset(w * 0.5f, h * 0.2f),
                    end = Offset(i * w / 10f, h),
                    strokeWidth = 2f
                )
            }

            // Glossy reflections on floor
            for (j in 1..4) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x55FFFFFF), Color(0x00FFFFFF))
                    ),
                    radius = 35f,
                    center = Offset(j * w / 5f, h * 0.7f)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White.copy(alpha = 0.85f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "High-Gloss Polyurea Coating",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = FromchemTextPrimary
            )
        }
    }
}
