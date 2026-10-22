package com.example.techtokensss.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Esquinas: 4 / 8 / 12 / 16 / 24 dp. Las tarjetas usan "large" (16 dp).
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Tokens de espaciado para usar en lugar de números sueltos.
object TechTicketSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp   // padding horizontal estándar de pantalla
    val xxl = 24.dp
}