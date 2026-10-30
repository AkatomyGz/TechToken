package com.example.techtokensss.Pantallas.Casos

import android.graphics.BitmapFactory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AssignmentInd
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.techtokensss.Datos.MondoDBAtlas.TechTokenApi
import com.example.techtokensss.Datos.Supabase.AuthSupa
import com.example.techtokensss.ui.components.EmptyState
import com.example.techtokensss.ui.components.ErrorState
import com.example.techtokensss.ui.components.FeedbackBanner
import com.example.techtokensss.ui.components.LoadingState
import com.example.techtokensss.ui.components.TechTicketTopBar
import com.example.techtokensss.ui.components.TipoMensaje
import com.example.techtokensss.ui.components.formatearEstado
import com.example.techtokensss.ui.components.formatearFecha
import com.example.techtokensss.ui.components.screenInsets
import org.json.JSONObject

private data class EventoHistorialUI(
    val tipoActividad: String,
    val descripcion: String,
    val actorRol: String,
    val actorNombre: String,
    val fecha: String,
    val cambios: JSONObject?,
    val idArchivo: String?,
    val nombreArchivo: String?
)

@Composable
fun HistorialCasoScreen(
    idCaso: String,
    onVolverClick: () -> Unit
) {
    // ------------------------------------------------------------------
    // LÓGICA (igual que antes; `recarga` solo permite reintentar la consulta)
    // ------------------------------------------------------------------
    val api = remember { TechTokenApi() }
    val authSupa = remember { AuthSupa() }

    var eventos by remember {
        mutableStateOf<List<EventoHistorialUI>>(emptyList())
    }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var recarga by remember { mutableStateOf(0) }

    LaunchedEffect(idCaso, recarga) {
        cargando = true
        error = null

        val token = authSupa.obtenerTokenActual()

        if (token.isNullOrBlank()) {
            error = "No se encontró una sesión activa. Inicia sesión nuevamente."
        } else {
            api.obtenerHistorial(idCaso, token)
                .onSuccess { respuesta ->
                    try {
                        val json = JSONObject(respuesta)

                        if (json.has("detail")) {
                            error = json.optString(
                                "detail",
                                "No se pudo consultar el historial."
                            )
                        } else {
                            val lista = json.optJSONArray("historial")
                            val resultado = mutableListOf<EventoHistorialUI>()

                            if (lista != null) {
                                for (i in 0 until lista.length()) {
                                    val evento = lista.getJSONObject(i)
                                    val cambios = evento.optJSONObject("cambios")

                                    // Acepta los nombres habituales de los campos
                                    // por si el backend los devuelve con otra convención.
                                    val idArchivo = cambios?.optString("idArchivo")
                                        ?.takeIf { it.isNotBlank() && it != "null" }
                                        ?: cambios?.optString("id_archivo")
                                            ?.takeIf { it.isNotBlank() && it != "null" }
                                        ?: cambios?.optString("archivoId")
                                            ?.takeIf { it.isNotBlank() && it != "null" }

                                    val nombreArchivo = cambios?.optString("nombreArchivo")
                                        ?.takeIf { it.isNotBlank() && it != "null" }
                                        ?: cambios?.optString("nombre_archivo")
                                            ?.takeIf { it.isNotBlank() && it != "null" }

                                    resultado.add(
                                        EventoHistorialUI(
                                            tipoActividad = evento.optString(
                                                "tipoActividad",
                                                "actividad"
                                            ),
                                            descripcion = evento.optString(
                                                "descripcion",
                                                "Sin descripción"
                                            ),
                                            actorRol = formatearRol(
                                                evento.optString("actorRol", "desconocido")
                                            ),
                                            actorNombre = evento.optString(
                                                "actorNombre",
                                                "Usuario desconocido"
                                            ).ifBlank { "Usuario desconocido" },
                                            fecha = formatearFechaHistorial(
                                                evento.optString("fecha", "")
                                            ),
                                            cambios = cambios,
                                            idArchivo = idArchivo,
                                            nombreArchivo = nombreArchivo
                                        )
                                    )
                                }
                            }

                            eventos = resultado
                        }
                    } catch (e: Exception) {
                        error = "No se pudo interpretar la respuesta del historial."
                    }
                }
                .onFailure { excepcion ->
                    error = excepcion.message
                        ?: "No se pudo consultar el historial."
                }
        }

        cargando = false
    }

    // ------------------------------------------------------------------
    // INTERFAZ
    // ------------------------------------------------------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .screenInsets()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TechTicketTopBar(
            titulo = "Historial del caso",
            subtitulo = "Caso #${idCaso.take(8)}",
            onVolverClick = onVolverClick
        )

        when {
            cargando -> {
                LoadingState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    mensaje = "Consultando historial..."
                )
            }

            error != null -> {
                ErrorState(
                    mensaje = error.orEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onReintentar = { recarga++ }
                )

                OutlinedButton(
                    onClick = onVolverClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Volver al caso")
                }
            }

            eventos.isEmpty() -> {
                EmptyState(
                    titulo = "Todavía no hay actividades registradas.",
                    descripcion = "Cuando se registren eventos del caso, aparecerán aquí.",
                    icono = Icons.Filled.History,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            else -> {
                Text(
                    text = if (eventos.size == 1) {
                        "1 actividad registrada"
                    } else {
                        "${eventos.size} actividades registradas"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    itemsIndexed(eventos) { indice, evento ->
                        EventoLineaTiempo(
                            evento = evento,
                            esUltimo = indice == eventos.lastIndex,
                            api = api,
                            idCaso = idCaso
                        )
                    }
                }
            }
        }
    }
}

/** Un evento del historial: icono y línea a la izquierda, tarjeta a la derecha. */
@Composable
private fun EventoLineaTiempo(
    evento: EventoHistorialUI,
    esUltimo: Boolean,
    api: TechTokenApi,
    idCaso: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Línea de tiempo
        Column(
            modifier = Modifier
                .width(36.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoActividad(evento.tipoActividad),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (!esUltimo) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(2.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        // Tarjeta del evento
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (esUltimo) 0.dp else 12.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = formatearTipoActividad(evento.tipoActividad),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = evento.fecha,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = evento.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = "${evento.actorNombre} · ${evento.actorRol}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Mostrar los cambios de forma legible,
                // nunca como un JSON completo.
                evento.cambios?.let { cambios ->
                    val lineas = obtenerCambiosLegibles(cambios)

                    if (lineas.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.shapes.medium
                                )
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Cambios realizados",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            lineas.forEach { linea ->
                                Text(
                                    text = "• $linea",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // La vista previa aparece cuando el evento
                // contiene el identificador de una evidencia.
                if (!evento.idArchivo.isNullOrBlank()) {
                    EvidenciaPreview(
                        api = api,
                        idCaso = idCaso,
                        idArchivo = evento.idArchivo,
                        nombreArchivo = evento.nombreArchivo
                            ?: "Evidencia adjunta"
                    )
                }
            }
        }
    }
}

@Composable
private fun EvidenciaPreview(
    api: TechTokenApi,
    idCaso: String,
    idArchivo: String,
    nombreArchivo: String
) {
    var cargandoImagen by remember(idArchivo) {
        mutableStateOf(true)
    }
    var imagenBytes by remember(idArchivo) {
        mutableStateOf<ByteArray?>(null)
    }
    var errorImagen by remember(idArchivo) {
        mutableStateOf<String?>(null)
    }

    val authSupa = remember { AuthSupa() }

    LaunchedEffect(idCaso, idArchivo) {
        cargandoImagen = true
        errorImagen = null

        val token = authSupa.obtenerTokenActual()

        if (token.isNullOrBlank()) {
            errorImagen = "Inicia sesión para consultar la evidencia."
        } else {
            api.obtenerEvidencia(
                idCaso = idCaso,
                idArchivo = idArchivo,
                token = token
            ).onSuccess {
                imagenBytes = it
            }.onFailure {
                errorImagen = "No se pudo cargar la vista previa."
            }
        }

        cargandoImagen = false
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = nombreArchivo,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        when {
            cargandoImagen -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )

                    Text(
                        text = "Cargando evidencia...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            imagenBytes != null -> {
                val bitmap = remember(imagenBytes) {
                    imagenBytes?.let {
                        BitmapFactory.decodeByteArray(it, 0, it.size)
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Vista previa de $nombreArchivo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    FeedbackBanner(
                        mensaje = "El archivo recibido no es una imagen compatible.",
                        tipo = TipoMensaje.Error
                    )
                }
            }

            errorImagen != null -> {
                FeedbackBanner(
                    mensaje = errorImagen.orEmpty(),
                    tipo = TipoMensaje.Error
                )
            }
        }
    }
}

private fun iconoActividad(tipo: String): ImageVector {
    return when (tipo.lowercase()) {
        "caso_creado" -> Icons.Outlined.AddCircleOutline
        "caso_actualizado" -> Icons.Outlined.Edit
        "evidencia_subida" -> Icons.Outlined.AttachFile
        "caso_reclamado" -> Icons.Outlined.AssignmentInd
        else -> Icons.Outlined.Info
    }
}

private fun obtenerCambiosLegibles(
    cambios: JSONObject
): List<String> {
    val resultado = mutableListOf<String>()

    val estadoAnterior = cambios.optString("estado_anterior")
    val estadoNuevo = cambios.optString("estado_nuevo")

    if (estadoAnterior.isNotBlank() && estadoNuevo.isNotBlank()) {
        resultado.add(
            "Estado: ${formatearEstadoHistorial(estadoAnterior)} → " +
                    formatearEstadoHistorial(estadoNuevo)
        )
    }

    val categoriaAnterior = cambios.optString("categoria_anterior")
    val categoriaNueva = cambios.optString("categoria_nueva")

    if (categoriaAnterior.isNotBlank() && categoriaNueva.isNotBlank()) {
        resultado.add(
            "Categoría: $categoriaAnterior → $categoriaNueva"
        )
    }

    val nombreArchivo = cambios.optString("nombreArchivo")
        .ifBlank { cambios.optString("nombre_archivo") }

    if (nombreArchivo.isNotBlank() && nombreArchivo != "null") {
        resultado.add("Archivo adjunto: $nombreArchivo")
    }

    // Si hay otros campos que todavía no tienen un formato amigable,
    // se muestran con etiquetas entendibles en lugar de imprimir el JSON.
    val camposConocidos = setOf(
        "estado_anterior",
        "estado_nuevo",
        "categoria_anterior",
        "categoria_nueva",
        "idArchivo",
        "id_archivo",
        "archivoId",
        "nombreArchivo",
        "nombre_archivo",
        "contentType",
        "tamanoBytes"
    )

    val claves = cambios.keys()
    while (claves.hasNext()) {
        val clave = claves.next()

        if (clave !in camposConocidos) {
            val valor = cambios.optString(clave)
            if (valor.isNotBlank() && valor != "null") {
                resultado.add("${etiquetaCampo(clave)}: $valor")
            }
        }
    }

    return resultado.distinct()
}

private fun etiquetaCampo(campo: String): String {
    return campo
        .replace(Regex("([a-z])([A-Z])"), "$1 $2")
        .replace("_", " ")
        .replaceFirstChar { it.uppercase() }
}

private fun formatearTipoActividad(tipo: String): String {
    return when (tipo.lowercase()) {
        "caso_actualizado" -> "Caso actualizado"
        "evidencia_subida" -> "Evidencia adjuntada"
        "caso_creado" -> "Caso creado"
        "caso_reclamado" -> "Caso asignado"
        else -> tipo
            .replace("_", " ")
            .replaceFirstChar { it.uppercase() }
    }
}

// Usa el formateador compartido para que los estados se lean igual en toda la app.
private fun formatearEstadoHistorial(estado: String): String {
    return formatearEstado(estado.lowercase())
}

private fun formatearRol(rol: String): String {
    return when (rol.lowercase()) {
        "tecnico" -> "Técnico"
        "usuario" -> "Usuario"
        "admin" -> "Administrador"
        else -> rol.replaceFirstChar { it.uppercase() }
    }
}

// Si la fecha trae zona horaria, se muestra como en el resto de la app
// (dd/MM/yyyy HH:mm, hora de Bogotá). Si no se puede interpretar, se usa
// el formato anterior.
private fun formatearFechaHistorial(fecha: String): String {
    if (fecha.isBlank()) return "Fecha no disponible"

    val formateada = formatearFecha(fecha)

    return if (formateada != fecha) {
        formateada
    } else {
        fecha
            .replace("T", " ")
            .removeSuffix("Z")
            .substringBefore(".")
    }
}