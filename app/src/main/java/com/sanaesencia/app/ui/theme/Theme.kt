package com.sanaesencia.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// ==========================================
// Configuración de Esquema de Colores Oscuro
// ==========================================
private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    secondary = GreenGrey80,
    tertiary = Tea80
)

// ==========================================
// Configuración de Esquema de Colores Claro
// ==========================================
private val LightColorScheme = lightColorScheme(
    primary = Green40,
    secondary = GreenGrey40,
    tertiary = Tea40
)

// ==========================================
// Composable del Tema Principal de Sanaesencia
// ==========================================
@Composable
fun SanaesenciaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Selección dinámica o estática de la paleta según versión de Android y preferencia de tema
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
        content = content
    )
}