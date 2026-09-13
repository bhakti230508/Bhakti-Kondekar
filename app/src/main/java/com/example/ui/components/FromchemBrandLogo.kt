package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Official Chemistry Laboratory Flask mark for FROMCHEM SOLUTION:
 * Features burgundy outer contour, navy inner structure, dark blue chemical liquid,
 * and yellow/amber letter 'F' in the center.
 */
@Composable
fun FromchemFlaskMark(
    modifier: Modifier = Modifier,
    height: Dp = 44.dp
) {
    val width = height * 0.92f

    Canvas(
        modifier = modifier
            .size(width = width, height = height)
            .semantics { contentDescription = "Fromchem Laboratory Flask Logo" }
    ) {
        val w = size.width
        val h = size.height
        val centerX = w * 0.48f

        // Geometry dimensions
        val lipWidth = w * 0.36f
        val lipHeight = h * 0.08f
        val neckWidth = w * 0.24f
        val neckTopY = h * 0.07f
        val neckBottomY = h * 0.32f
        val baseLeftX = w * 0.10f
        val baseRightX = w * 0.88f
        val baseY = h * 0.92f
        val liquidSurfaceY = h * 0.54f

        val strokeWidth = (w * 0.07f).coerceAtLeast(2.5f)

        // 1. Draw Liquid Fill inside Lower Flask Body
        val liquidPath = Path().apply {
            // Calculate X coordinates at liquid surface height based on conical slope
            val leftSlope = (baseLeftX - (centerX - neckWidth / 2)) / (baseY - neckBottomY)
            val rightSlope = (baseRightX - (centerX + neckWidth / 2)) / (baseY - neckBottomY)
            val liquidLeftX = (centerX - neckWidth / 2) + leftSlope * (liquidSurfaceY - neckBottomY)
            val liquidRightX = (centerX + neckWidth / 2) + rightSlope * (liquidSurfaceY - neckBottomY)

            moveTo(liquidLeftX, liquidSurfaceY)
            lineTo(liquidRightX, liquidSurfaceY)
            lineTo(baseRightX - w * 0.04f, baseY - h * 0.04f)
            // Rounded bottom right
            quadraticTo(baseRightX, baseY, baseRightX - w * 0.08f, baseY)
            lineTo(baseLeftX + w * 0.08f, baseY)
            // Rounded bottom left
            quadraticTo(baseLeftX, baseY, baseLeftX + w * 0.04f, baseY - h * 0.04f)
            lineTo(liquidLeftX, liquidSurfaceY)
            close()
        }

        drawPath(
            path = liquidPath,
            brush = Brush.verticalGradient(
                colors = listOf(FromchemFlaskLiquid, Color(0xFF091F38)),
                startY = liquidSurfaceY,
                endY = baseY
            )
        )

        // 2. Yellow/Gold Letter 'F' inside the chemical liquid
        val fStartX = centerX - w * 0.08f
        val fTopY = h * 0.62f
        val fBottomY = h * 0.84f
        val fArmWidth = w * 0.16f
        val fMidArmWidth = w * 0.11f
        val fMidY = h * 0.72f
        val fStrokeWidth = (w * 0.08f).coerceAtLeast(3f)

        // Vertical stem of F
        drawLine(
            color = FromchemAmberGold,
            start = Offset(fStartX, fTopY),
            end = Offset(fStartX, fBottomY),
            strokeWidth = fStrokeWidth,
            cap = StrokeCap.Square
        )
        // Top horizontal arm of F
        drawLine(
            color = FromchemAmberGold,
            start = Offset(fStartX, fTopY + fStrokeWidth / 2),
            end = Offset(fStartX + fArmWidth, fTopY + fStrokeWidth / 2),
            strokeWidth = fStrokeWidth,
            cap = StrokeCap.Square
        )
        // Middle horizontal arm of F
        drawLine(
            color = FromchemAmberGold,
            start = Offset(fStartX, fMidY),
            end = Offset(fStartX + fMidArmWidth, fMidY),
            strokeWidth = fStrokeWidth,
            cap = StrokeCap.Square
        )

        // 3. Flask Outer Contour (Deep Burgundy Red)
        val outerFlaskPath = Path().apply {
            // Start at neck top left
            moveTo(centerX - neckWidth / 2, neckTopY)
            lineTo(centerX - neckWidth / 2, neckBottomY)
            lineTo(baseLeftX, baseY - h * 0.04f)
            quadraticTo(baseLeftX, baseY, baseLeftX + w * 0.08f, baseY)
            lineTo(baseRightX - w * 0.08f, baseY)
            quadraticTo(baseRightX, baseY, baseRightX, baseY - h * 0.04f)
            lineTo(centerX + neckWidth / 2, neckBottomY)
            lineTo(centerX + neckWidth / 2, neckTopY)
        }

        drawPath(
            path = outerFlaskPath,
            color = FromchemBurgundy,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 4. Flask Top Lip (Burgundy & Navy Accent)
        drawRoundRect(
            color = FromchemBurgundy,
            topLeft = Offset(centerX - lipWidth / 2, h * 0.02f),
            size = Size(lipWidth, lipHeight),
            cornerRadius = CornerRadius(lipHeight / 2, lipHeight / 2)
        )
        drawRoundRect(
            color = FromchemNavy,
            topLeft = Offset(centerX - lipWidth / 2 + strokeWidth / 2, h * 0.02f + strokeWidth / 4),
            size = Size(lipWidth - strokeWidth, lipHeight - strokeWidth / 2),
            cornerRadius = CornerRadius(lipHeight / 3, lipHeight / 3)
        )

        // 5. Inner Glass Sheen / Reflection on left wall
        drawLine(
            color = Color.White.copy(alpha = 0.55f),
            start = Offset(baseLeftX + w * 0.06f, baseY - h * 0.08f),
            end = Offset(centerX - neckWidth / 2 + w * 0.02f, neckBottomY + h * 0.04f),
            strokeWidth = strokeWidth * 0.45f,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Custom Letter 'O' containing the glowing amber chemical droplet,
 * replicating the official FROMCHEM wordmark.
 */
@Composable
fun DropletLetterO(
    size: Dp = 16.dp,
    textColor: Color = FromchemNavy
) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val strokeW = s * 0.18f
        val center = Offset(s / 2, s / 2)

        // Outer O ring in Navy Blue
        drawCircle(
            color = textColor,
            radius = (s - strokeW) / 2,
            center = center,
            style = Stroke(width = strokeW)
        )

        // Glowing Chemical Droplet / Bubble inside O
        val dropletRadius = s * 0.22f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    FromchemAmberGoldLight,
                    FromchemAmberGold,
                    Color(0xFFD97706)
                ),
                center = center - Offset(dropletRadius * 0.2f, dropletRadius * 0.2f),
                radius = dropletRadius
            ),
            radius = dropletRadius,
            center = center
        )

        // Droplet top reflection highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = dropletRadius * 0.35f,
            center = center - Offset(dropletRadius * 0.3f, dropletRadius * 0.35f)
        )
    }
}

/**
 * Official Full Brand Logo for FROMCHEM SOLUTION matching the user-provided identity:
 * - Laboratory chemistry flask on left with yellow 'F' and blue fluid
 * - "FROMCHEM" with amber droplet in 'O'
 * - Burgundy divider shelf bar
 * - "SOLUTION" in navy blue
 */
@Composable
fun FromchemBrandLogo(
    modifier: Modifier = Modifier,
    showSubtext: Boolean = true,
    compact: Boolean = false
) {
    val flaskHeight = if (compact) 34.dp else 46.dp
    val titleFontSize = if (compact) 16.sp else 21.sp
    val subtitleFontSize = if (compact) 10.sp else 13.sp
    val shelfHeight = if (compact) 2.5.dp else 3.5.dp
    val dropletSize = if (compact) 13.dp else 17.dp

    Row(
        modifier = modifier.semantics { contentDescription = "Fromchem Solution Brand Logo" },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Chemistry Flask Mark
        FromchemFlaskMark(height = flaskHeight)

        Spacer(modifier = Modifier.width(4.dp))

        // 2. Wordmark Stack: FROMCHEM + Burgundy Shelf + SOLUTION
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            // Main Line: "FROMCHEM" with droplet in 'O'
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "FR",
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Black,
                    color = FromchemNavy,
                    letterSpacing = 0.5.sp
                )

                DropletLetterO(
                    size = dropletSize,
                    textColor = FromchemNavy
                )

                Text(
                    text = "MCHEM",
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Black,
                    color = FromchemNavy,
                    letterSpacing = 0.5.sp
                )
            }

            // Burgundy Red Divider Shelf Bar (connecting from flask to wordmark edge)
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (compact) 0.92f else 0.96f)
                    .height(shelfHeight)
                    .clip(
                        GenericShape { size, _ ->
                            moveTo(0f, 0f)
                            lineTo(size.width - size.height, 0f)
                            lineTo(size.width, size.height)
                            lineTo(0f, size.height)
                            close()
                        }
                    )
                    .background(FromchemBurgundy)
            )

            Spacer(modifier = Modifier.height(1.dp))

            // Subtitle Line: "SOLUTION"
            Text(
                text = "SOLUTION",
                fontSize = subtitleFontSize,
                fontWeight = FontWeight.ExtraBold,
                color = FromchemNavy,
                letterSpacing = if (compact) 2.sp else 2.5.sp
            )
        }
    }
}
