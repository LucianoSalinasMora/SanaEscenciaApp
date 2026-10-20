package com.sanaesencia.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val DarkColorScheme = darkColorScheme(
    primary = Green40,
    secondary = GreenGrey40,
    tertiary = Tea40,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceDark,
    primaryContainer = Green40,
    secondaryContainer = GreenGrey40,

    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = GreenGrey40,
    onSurface = Green40,
    onSurfaceVariant = TextDark,
    onPrimaryContainer = Color.White,
    onSecondaryContainer = Color.White
)


private val LightColorScheme = lightColorScheme(
    primary = Green40,
    secondary = GreenGrey80,
    tertiary = Green40,
    background = BackgroundLight,
    surface = SurfaceLight,

    surfaceVariant = Color(0xFFF5F5F5),
    primaryContainer = Green40,
    secondaryContainer = Green80,

    onPrimary = TextLight,
    onSecondary = Green40,
    onBackground = TextLight,
    onSurface = TextLight,
    onSurfaceVariant = TextLight,
    onPrimaryContainer = TextLight,
    onSecondaryContainer = TextLight
)


@Composable
fun SanaesenciaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    // Aplicación del tema Material 3 al contenido hijo
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}