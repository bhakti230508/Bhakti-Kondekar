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
    ) {
        // Background architectural window grid & wall
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Room background wall gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFF1F5F9), Color(0xFFCBD5E1))
                )
            )

            // Grid window lines in background
            val gridColor = Color(0x3394A3B8)
            for (x in 0..10) {
                drawLine(
                    color = gridColor,
                    start = Offset(x * w / 10f, 0f),
                    end = Offset(x * w / 10f, h),
                    strokeWidth = 2f
                )
            }
            for (y in 0..6) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y * h / 6f),
                    end = Offset(w, y * h / 6f),
                    strokeWidth = 2f
                )
            }

            // Blue Waterproofing Coating area on right wall
            val blueCoatingPath = Path().apply {
                moveTo(w * 0.45f, 0f)
                lineTo(w, 0f)
                lineTo(w, h)
                lineTo(w * 0.35f, h)
                cubicTo(w * 0.4f, h * 0.6f, w * 0.3f, h * 0.3f, w * 0.45f, 0f)
            }
            drawPath(
                path = blueCoatingPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB))
                )
            )

            // Scaffolding / Platform
            drawRect(
                color = Color(0xFF64748B),
                topLeft = Offset(w * 0.1f, h * 0.75f),
                size = Size(w * 0.7f, 16f)
            )
            // Ladder supports
            drawLine(
                color = Color(0xFF475569),
                start = Offset(w * 0.2f, h * 0.75f),
                end = Offset(w * 0.15f, h),
                strokeWidth = 6f
            )
            drawLine(
                color = Color(0xFF475569),
                start = Offset(w * 0.6f, h * 0.75f),
                end = Offset(w * 0.55f, h),
                strokeWidth = 6f
            )

            // Worker Figure Silhouette with Roller
            // Body / Uniform (Blue & Gray)
            drawCircle(
                color = Color(0xFF1E293B),
                radius = 18f,
                center = Offset(w * 0.42f, h * 0.38f) // Head
            )
            drawRect(
                color = Color(0xFF2563EB), // Blue vest
                topLeft = Offset(w * 0.38f, h * 0.42f),
                size = Size(w * 0.08f, h * 0.22f)
            )
            // Roller arm extended to blue coating
            drawLine(
                color = Color(0xFF0F172A),
                start = Offset(w * 0.44f, h * 0.48f),
                end = Offset(w * 0.55f, h * 0.35f),
                strokeWidth = 8f
            )
            // Paint Roller
            drawRect(
                color = Color(0xFF1D4ED8),
                topLeft = Offset(w * 0.54f, h * 0.3f),
                size = Size(w * 0.04f, 24f)
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
                Text(
                    text = "25+",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemPrimary
                )
                Text(
                    text = "Years of structural\nprotection expertise",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = FromchemTextSecondary,
                    lineHeight = 14.sp
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
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // White rooftop surface with sky gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF93C5FD), Color(0xFFE2E8F0))
                ),
                size = Size(w, h * 0.4f)
            )
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
                ),
                topLeft = Offset(0f, h * 0.4f),
                size = Size(w, h * 0.6f)
            )

            // Roof vent / structure details
            drawRect(
                color = Color(0xFFCBD5E1),
                topLeft = Offset(w * 0.7f, h * 0.25f),
                size = Size(w * 0.2f, h * 0.3f)
            )
            drawCircle(
                color = Color(0xFF94A3B8),
                radius = 12f,
                center = Offset(w * 0.8f, h * 0.25f)
            )
        }

        // Project Spec Label Badge on Roof
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.92f))
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
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
                        text = "Project: Commercial Rooftop",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = FromchemTextPrimary
                    )
                }
                Text(
                    text = "Coating: Crystalline & Elastomeric",
                    fontSize = 8.sp,
                    color = FromchemTextSecondary
                )
                Text(
                    text = "Status: 100% Sealed & ISO Verified",
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
