package com.example.techtokensss.ui.components

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Utilidades de presentación compartidas por todas las pantallas.
// Reemplazan a las funciones privadas duplicadas en DetalleCasos.kt y DashboardUsuario.kt
// (se eliminarán de esos archivos en sus etapas).

private val zonaLocal: ZoneId = ZoneId.of("America/Bogota")
private val localeEs: Locale = Locale.forLanguageTag("es-CO")
private val formatoCompleto = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", localeEs)
private val formatoCorto = DateTimeFormatter.ofPattern("dd/MM/yyyy", localeEs)

/** Convierte el valor guardado en Supabase ("en_proceso") a texto legible ("En proceso"). */
fun formatearEstado(estado: String): String = when (estado) {
    "registrado" -> "Registrado"
    "en_proceso" -> "En proceso"
    "solucionado" -> "Solucionado"
    "cerrado" -> "Cerrado"
    else -> estado.replace("_", " ").replaceFirstChar { it.uppercase() }
}

/** Fecha y hora en zona de Bogotá: 22/10/2026 14:30. */
fun formatearFecha(fecha: String?): String {
    if (fecha.isNullOrBlank()) return "Sin fecha"

    return runCatching {
        OffsetDateTime.parse(fecha)
            .atZoneSameInstant(zonaLocal)
            .format(formatoCompleto)
    }.getOrElse { fecha }
}

/** Solo la fecha en zona de Bogotá: 22/10/2026. Pensada para tarjetas de lista. */
fun formatearFechaCorta(fecha: String?): String {
    if (fecha.isNullOrBlank()) return "Sin fecha"

    return runCatching {
        OffsetDateTime.parse(fecha)
            .atZoneSameInstant(zonaLocal)
            .format(formatoCorto)
    }.getOrElse { fecha.take(10) }
}