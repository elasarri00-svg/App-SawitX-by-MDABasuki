package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PalmGreen60,
    onPrimary = PalmGreen10,
    primaryContainer = PalmGreen20,
    onPrimaryContainer = PalmGreenLight,
    secondary = HarvestGold,
    onSecondary = Color(0xFF3E2723),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = HarvestGoldContainer,
    tertiary = Color(0xFF38BDF8),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = StatusOpenRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PalmGreen40,
    onPrimary = Color.White,
    primaryContainer = PalmGreenContainer,
    onPrimaryContainer = PalmGreenDark,
    secondary = HarvestGold,
    onSecondary = Color.White,
    secondaryContainer = HarvestGoldContainer,
    onSecondaryContainer = HarvestGoldDark,
    tertiary = Color(0xFF0284C7),
    background = EarthWarmNeutral,
    surface = EarthCardLight,
    onBackground = SlateGrayDark,
    onSurface = SlateGrayDark,
    surfaceVariant = SlateGraySoft,
    onSurfaceVariant = SlateGrayMedium,
    error = StatusOpenRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our crisp vibrant palette theme by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.White.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

