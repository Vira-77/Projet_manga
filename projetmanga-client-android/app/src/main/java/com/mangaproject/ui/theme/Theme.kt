package com.mangaproject.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF4444),           // Rouge vif
    onPrimary = Color.White,
    primaryContainer = Color(0xFF8B0000),  // Rouge foncé
    onPrimaryContainer = Color(0xFFFFCDD2),

    secondary = Color(0xFFE53935),         // Rouge secondaire
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF5D1F1F),
    onSecondaryContainer = Color(0xFFFFCDD2),

    tertiary = Color(0xFFFF6B6B),          // Rouge clair
    onTertiary = Color.White,

    background = Color(0xFF121212),        // Noir doux
    onBackground = Color(0xFFE0E0E0),

    surface = Color(0xFF1E1E1E),           // Gris très foncé
    onSurface = Color(0xFFE0E0E0),

    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),

    error = Color(0xFFCF6679),
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD32F2F),           // Rouge principal
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFCDD2),
    onPrimaryContainer = Color(0xFF8B0000),

    secondary = Color(0xFFE57373),         // Rouge clair
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEBEE),
    onSecondaryContainer = Color(0xFF5D1F1F),

    tertiary = Color(0xFFFF8A80),          // Accent rouge
    onTertiary = Color.White,

    background = Color(0xE4DC0243),        // Gris très clair
    onBackground = Color(0xFF212121),

    surface = Color(0xFFFFFFFF),           // Blanc
    onSurface = Color(0xFF212121),

    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF5E5E5E),

    error = Color(0xFFB00020),
    onError = Color.White
)

@Composable
fun MyMangaProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}