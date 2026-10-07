package com.example.techtokensss.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TechTokenPurple,
    secondary = TechTokenGray,
    background = TechTokenBackground,
    surface = TechTokenCard,
    onPrimary = TechTokenLightCard,
    onBackground = TechTokenLightCard,
    onSurface = TechTokenLightCard
)

private val LightColorScheme = lightColorScheme(
    primary = TechTokenPurple,
    secondary = TechTokenGray,
    background = TechTokenLightBackground,
    surface = TechTokenLightCard,
    onPrimary = TechTokenLightCard,
    onBackground = TechTokenLightText,
    onSurface = TechTokenLightText
)

@Composable
fun TechTokensssTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}