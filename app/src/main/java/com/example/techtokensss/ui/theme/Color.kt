package com.example.techtokensss.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// =====================================================================
// PALETA TECHTICKET
// Valores marcados "(base)" vienen de la paleta aprobada.
// Valores marcados "(derivado)" fueron calculados para completar los
// roles de Material 3 que la paleta base no define.
// =====================================================================

// ---------------------------- MODO CLARO -----------------------------
internal val LightPrimary = Color(0xFF1F5C99)              // (base)
internal val LightOnPrimary = Color(0xFFFFFFFF)            // (base)
internal val LightPrimaryContainer = Color(0xFFD6E6F7)     // (base)
internal val LightOnPrimaryContainer = Color(0xFF0B2A47)   // (derivado)
internal val LightSecondary = Color(0xFF4A5D70)            // (base)
internal val LightOnSecondary = Color(0xFFFFFFFF)          // (derivado)
internal val LightSecondaryContainer = Color(0xFFDCE5EE)   // (derivado)
internal val LightOnSecondaryContainer = Color(0xFF14202B) // (derivado)
internal val LightTertiary = Color(0xFFF2A900)             // (base: acento ámbar)
internal val LightOnTertiary = Color(0xFF2B1D00)           // (derivado)
internal val LightTertiaryContainer = Color(0xFFFFEFC2)    // (derivado)
internal val LightOnTertiaryContainer = Color(0xFF5C4000)  // (derivado)
internal val LightBackground = Color(0xFFF4F6F8)           // (base)
internal val LightOnBackground = Color(0xFF14202B)         // (base)
internal val LightSurface = Color(0xFFFFFFFF)              // (base)
internal val LightOnSurface = Color(0xFF14202B)            // (base)
internal val LightSurfaceVariant = Color(0xFFE8EDF2)       // (derivado)
internal val LightOnSurfaceVariant = Color(0xFF4A5D70)     // (derivado)
internal val LightOutline = Color(0xFF7D8A97)              // (derivado: borde de campos, contraste >= 3:1)
internal val LightOutlineVariant = Color(0xFFC5CCD3)       // (base "Outline": divisores y bordes suaves)
internal val LightError = Color(0xFFBA1A1A)                // (derivado)
internal val LightOnError = Color(0xFFFFFFFF)
internal val LightErrorContainer = Color(0xFFFFDAD6)
internal val LightOnErrorContainer = Color(0xFF410002)
internal val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
internal val LightSurfaceContainerLow = Color(0xFFF8FAFB)
internal val LightSurfaceContainer = Color(0xFFF0F3F6)
internal val LightSurfaceContainerHigh = Color(0xFFEAEEF2)
internal val LightSurfaceContainerHighest = Color(0xFFE4E9EE)
internal val LightInverseSurface = Color(0xFF263340)
internal val LightInverseOnSurface = Color(0xFFE4E9EE)
internal val LightInversePrimary = Color(0xFF8EBBEB)

// ---------------------------- MODO OSCURO ----------------------------
internal val DarkPrimary = Color(0xFF8EBBEB)               // (base)
internal val DarkOnPrimary = Color(0xFF0A2A47)             // (base)
internal val DarkPrimaryContainer = Color(0xFF0F3A63)      // (base)
internal val DarkOnPrimaryContainer = Color(0xFFD6E6F7)    // (derivado)
internal val DarkSecondary = Color(0xFFB4C3D3)             // (base)
internal val DarkOnSecondary = Color(0xFF1B2A38)           // (derivado)
internal val DarkSecondaryContainer = Color(0xFF2A3947)    // (derivado)
internal val DarkOnSecondaryContainer = Color(0xFFD5E1EE)  // (derivado)
internal val DarkTertiary = Color(0xFFF5BC3A)              // (base: acento ámbar)
internal val DarkOnTertiary = Color(0xFF3A2800)            // (derivado)
internal val DarkTertiaryContainer = Color(0xFF4A3600)     // (derivado)
internal val DarkOnTertiaryContainer = Color(0xFFFFE2A0)   // (derivado)
internal val DarkBackground = Color(0xFF0E1318)            // (base)
internal val DarkOnBackground = Color(0xFFE4E9EE)          // (base)
internal val DarkSurface = Color(0xFF151C23)               // (base)
internal val DarkOnSurface = Color(0xFFE4E9EE)             // (base)
internal val DarkSurfaceVariant = Color(0xFF1E2832)        // (derivado)
internal val DarkOnSurfaceVariant = Color(0xFFB4C3D3)      // (derivado)
internal val DarkOutline = Color(0xFF5E6E7E)               // (derivado: borde de campos)
internal val DarkOutlineVariant = Color(0xFF33404C)        // (base "Outline": divisores y bordes suaves)
internal val DarkError = Color(0xFFFFB4AB)                 // (derivado)
internal val DarkOnError = Color(0xFF690005)
internal val DarkErrorContainer = Color(0xFF93000A)
internal val DarkOnErrorContainer = Color(0xFFFFDAD6)
internal val DarkSurfaceContainerLowest = Color(0xFF0B1015)
internal val DarkSurfaceContainerLow = Color(0xFF12181E)
internal val DarkSurfaceContainer = Color(0xFF151C23)
internal val DarkSurfaceContainerHigh = Color(0xFF1B242D)
internal val DarkSurfaceContainerHighest = Color(0xFF222D38)
internal val DarkInverseSurface = Color(0xFFE4E9EE)
internal val DarkInverseOnSurface = Color(0xFF14202B)
internal val DarkInversePrimary = Color(0xFF1F5C99)

// =====================================================================
// COLORES SEMÁNTICOS DE ESTADO (no existen en Material 3)
// Se accede con TechTicketTheme.colors
// =====================================================================

/**
 * Colores de un estado de caso.
 * [container] y [onContainer] sirven para chips/etiquetas.
 * [solid] sirve para gráficos, puntos e indicadores.
 */
@Immutable
data class EstadoColors(
    val container: Color,
    val onContainer: Color,
    val solid: Color
)

@Immutable
data class TechTicketColors(
    val registrado: EstadoColors,
    val enProceso: EstadoColors,
    val solucionado: EstadoColors,
    val cerrado: EstadoColors,
    /** Texto o icono de operación exitosa sobre superficie. */
    val success: Color
) {
    /** Recibe el valor crudo guardado en Supabase ("en_proceso", etc.). */
    fun paraEstado(estado: String): EstadoColors = when (estado) {
        "registrado" -> registrado
        "en_proceso" -> enProceso
        "solucionado" -> solucionado
        "cerrado" -> cerrado
        else -> cerrado
    }
}

internal val LightTechTicketColors = TechTicketColors(
    registrado = EstadoColors(
        container = Color(0xFFD6E6F7),
        onContainer = Color(0xFF174A7C),
        solid = Color(0xFF1F5C99)
    ),
    enProceso = EstadoColors(
        container = Color(0xFFFFEFC2),
        onContainer = Color(0xFF6B4A00),
        solid = Color(0xFFF2A900)
    ),
    solucionado = EstadoColors(
        container = Color(0xFFD3F1E2),
        onContainer = Color(0xFF14633F),
        solid = Color(0xFF2E9E6B)
    ),
    cerrado = EstadoColors(
        container = Color(0xFFE3E8ED),
        onContainer = Color(0xFF4A5D70),
        solid = Color(0xFF7B8896)
    ),
    success = Color(0xFF1E7A50)
)

internal val DarkTechTicketColors = TechTicketColors(
    registrado = EstadoColors(
        container = Color(0xFF0F3A63),
        onContainer = Color(0xFF8EBBEB),
        solid = Color(0xFF8EBBEB)
    ),
    enProceso = EstadoColors(
        container = Color(0xFF4A3600),
        onContainer = Color(0xFFF5BC3A),
        solid = Color(0xFFF5BC3A)
    ),
    solucionado = EstadoColors(
        container = Color(0xFF0F3F2B),
        onContainer = Color(0xFF6FD7A5),
        solid = Color(0xFF6FD7A5)
    ),
    cerrado = EstadoColors(
        container = Color(0xFF27323C),
        onContainer = Color(0xFFB4C3D3),
        solid = Color(0xFF8795A4)
    ),
    success = Color(0xFF6FD7A5)
)