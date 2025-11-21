package com.example.smartenvironment.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import androidx.tv.material3.lightColorScheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SmartEnvironmentTheme(
    isInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isInDarkTheme) {
        darkColorScheme(
            primary = secondColor,    // medium turquoise
            secondary = thirdColor,     // dark turquoise
            tertiary = fourthColor      // bright green
        )
    } else {
        lightColorScheme(
            primary = thirdColor,       // dark turquoise
            secondary = secondColor,    // medium turquoise
            tertiary = fourthColor,     // bright green
            background = firstColor,    // light cyan
            surface = firstColor
        )
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}