package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.LarkThemeMode

fun getLarkColorScheme(themeMode: LarkThemeMode) = when (themeMode) {
    LarkThemeMode.LARK_CYAN -> darkColorScheme(
        primary = LarkCyan,
        onPrimary = Color.Black,
        primaryContainer = LarkElectricBlue,
        onPrimaryContainer = Color.White,
        secondary = LarkElectricBlue,
        onSecondary = Color.White,
        tertiary = LarkViolet,
        background = LarkDarkBg,
        onBackground = TextPrimary,
        surface = LarkSurfaceDark,
        onSurface = TextPrimary,
        surfaceVariant = LarkSurfaceCard,
        onSurfaceVariant = TextSecondary,
        outline = LarkBorder
    )
    LarkThemeMode.CYBERPUNK -> darkColorScheme(
        primary = LarkMagenta,
        onPrimary = Color.White,
        primaryContainer = LarkViolet,
        onPrimaryContainer = Color.White,
        secondary = LarkCyan,
        onSecondary = Color.Black,
        tertiary = LarkElectricBlue,
        background = Color(0xFF0D0614),
        onBackground = TextPrimary,
        surface = Color(0xFF160B24),
        onSurface = TextPrimary,
        surfaceVariant = Color(0xFF221138),
        onSurfaceVariant = TextSecondary,
        outline = Color(0xFF3B1E60)
    )
    LarkThemeMode.SUNSET_AMBER -> darkColorScheme(
        primary = LarkAmber,
        onPrimary = Color.Black,
        primaryContainer = Color(0xFFB45309),
        onPrimaryContainer = Color.White,
        secondary = Color(0xFFFB8500),
        onSecondary = Color.White,
        tertiary = LarkMagenta,
        background = Color(0xFF120E0A),
        onBackground = TextPrimary,
        surface = Color(0xFF1E1711),
        onSurface = TextPrimary,
        surfaceVariant = Color(0xFF2A2017),
        onSurfaceVariant = TextSecondary,
        outline = Color(0xFF453424)
    )
    LarkThemeMode.EMERALD_BASS -> darkColorScheme(
        primary = LarkEmerald,
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF0F766E),
        onPrimaryContainer = Color.White,
        secondary = Color(0xFF06D6A0),
        onSecondary = Color.Black,
        tertiary = LarkCyan,
        background = Color(0xFF051111),
        onBackground = TextPrimary,
        surface = Color(0xFF0B1F1F),
        onSurface = TextPrimary,
        surfaceVariant = Color(0xFF122E2E),
        onSurfaceVariant = TextSecondary,
        outline = Color(0xFF1D4747)
    )
    LarkThemeMode.AMOLED_BLACK -> darkColorScheme(
        primary = LarkCyan,
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF1E293B),
        onPrimaryContainer = Color.White,
        secondary = Color(0xFF38BDF8),
        onSecondary = Color.Black,
        tertiary = LarkViolet,
        background = AmoledBg,
        onBackground = Color.White,
        surface = AmoledSurface,
        onSurface = Color.White,
        surfaceVariant = AmoledCard,
        onSurfaceVariant = Color(0xFFCBD5E1),
        outline = Color(0xFF27272A)
    )
}

@Composable
fun LarkMusicTheme(
    themeMode: LarkThemeMode = LarkThemeMode.LARK_CYAN,
    content: @Composable () -> Unit
) {
    val colorScheme = getLarkColorScheme(themeMode)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
