package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val StudioColorScheme = darkColorScheme(
    primary = NeonViolet,
    onPrimary = StudioDarkBg,
    primaryContainer = NeonVioletDark,
    onPrimaryContainer = TextPrimary,
    secondary = CyberCyan,
    onSecondary = StudioDarkBg,
    secondaryContainer = StudioSurfaceVariant,
    onSecondaryContainer = CyberCyanGlow,
    tertiary = RomanticRose,
    onTertiary = TextPrimary,
    tertiaryContainer = Color(0xFF4C0519),
    onTertiaryContainer = RomanticRoseSoft,
    background = StudioDarkBg,
    onBackground = TextPrimary,
    surface = StudioCardBg,
    onSurface = TextPrimary,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = StudioCardStroke
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force creative dark theme for Taruni Studio
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = StudioDarkBg.toArgb()
                window.navigationBarColor = StudioDarkBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = StudioColorScheme,
        typography = Typography,
        content = content
    )
}
