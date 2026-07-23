package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FromchemColorScheme = lightColorScheme(
    primary = FromchemPrimary,
    onPrimary = Color.White,
    primaryContainer = FromchemPrimaryContainer,
    onPrimaryContainer = FromchemOnPrimaryContainer,
    secondary = FromchemPrimaryDark,
    onSecondary = Color.White,
    background = FromchemBackground,
    onBackground = FromchemTextPrimary,
    surface = FromchemSurface,
    onSurface = FromchemTextPrimary,
    surfaceVariant = FromchemSurfaceVariant,
    onSurfaceVariant = FromchemTextSecondary,
    outline = FromchemBorder
)

@Composable
fun FromchemTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FromchemColorScheme,
        typography = Typography,
        content = content
    )
}
