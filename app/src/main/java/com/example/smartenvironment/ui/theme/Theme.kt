package com.example.smartenvironment.ui.theme

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

// Paleta de colores para versiones anteriores a Android 12
private val AppDarkColorScheme = darkColorScheme(
    primary = secondColor,    // medium turquoise
    secondary = thirdColor,     // dark turquoise
    tertiary = fourthColor,     // bright green
    background = Color(0xFF1C1B1F), // Fondo oscuro estándar
    surface = Color(0xFF1C1B1F)
)

private val AppLightColorScheme = lightColorScheme(
    primary = thirdColor,       // dark turquoise
    secondary = secondColor,    // medium turquoise
    tertiary = fourthColor,     // bright green
    background = firstColor,    // light cyan
    surface = firstColor
)

@Composable
fun SmartEnvironmentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AppDarkColorScheme
        else -> AppLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
