package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SmartFarmLightColorScheme = lightColorScheme(
    primary = FarmGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = FarmGreenContainer,
    onPrimaryContainer = FarmGreenDark,
    secondary = FarmAmberAccent,
    onSecondary = Color.White,
    secondaryContainer = FarmAmberLight,
    onSecondaryContainer = FarmBrownEarth,
    background = Color(0xFFF8FAFC),
    onBackground = FarmTextPrimary,
    surface = Color.White,
    onSurface = FarmTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = FarmTextSecondary,
    outline = FarmCardBorder,
    error = FarmRedError,
    onError = Color.White
)

private val SmartFarmDarkColorScheme = darkColorScheme(
    primary = Color(0xFF4ADE80),
    onPrimary = FarmGreenDark,
    primaryContainer = FarmGreenDark,
    onPrimaryContainer = Color(0xFFDCFCE7),
    secondary = FarmAmberAccent,
    onSecondary = Color.Black,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569)
)

@Composable
fun SmartFarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SmartFarmDarkColorScheme else SmartFarmLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
