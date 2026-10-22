
package com.example.techtokensss.Pantallas.Casos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.techtokensss.Datos.Modelos.Caso
import com.example.techtokensss.Datos.Modelos.Categoria
import com.example.techtokensss.Pantallas.CasosTecnico.CasosTecnicoRepository
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun DetalleCasoScreen(
    idCaso: String,
    esTecnico: Boolean = false,
    onVolverClick: () -> Unit,
    onHistorialClick: (String) -> Unit
) {
    val repository = remember { CasosRepository() }
    val tecnicoRepository = remember { CasosTecnicoRepository() }
    val scope = rememberCoroutineScope()

    var caso by remember { mutableStateOf<Caso?>(null) }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var guardando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var nombreEncargado by remember { mutableStateOf<String?>(null) }

    var categoriaSeleccionada by remember { mutableStateOf("") }
    var estadoSeleccionado by remember { mutableStateOf("") }

    var categoriaMenuAbierto by remember { mutableStateOf(false) }
    var estadoMenuAbierto by remember { mutableStateOf(false) }

    var mostrarHistorialProximamente by remember {
        mutableStateOf(false)
    }

    val estadosDisponibles = listOf(
        "registrado",
        "en_proceso",
        "solucionado"
    )

    LaunchedEffect(idCaso) {
        cargando = true
        error = null
        mensaje = null
        caso = null
        nombreEncargado = null

        repository.obtenerCasoPorId(idCaso)
            .onSuccess { resultado ->
                caso = resultado

                if (resultado == null) {
                    error = "No se encontró el caso o no tienes permiso para verlo."
                } else {
                    categoriaSeleccionada = resultado.idCategoria
                    estadoSeleccionado = resultado.estado

                    resultado.idEncargado?.let { id ->
                        repository.obtenerEncargado(id)
                            .onSuccess { nombre ->
                                nombreEncargado = nombre
                            }
                    }
                }
            }
            .onFailure { excepcion ->
                error = excepcion.message
                    ?: "No se pudo cargar el caso."
            }

        repository.obtenerCategorias()
            .onSuccess { resultado ->
                categorias = resultado
            }

        cargando = false
    }

    val nombreCategoria = categorias
        .find { it.idCategoria == caso?.idCategoria }
        ?.nombre ?: "Sin categoría"

    val nombreCategoriaSeleccionada = categorias
        .find { it.idCategoria == categoriaSeleccionada }
        ?.nombre ?: "Selecciona una categoría"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121318))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onVolverClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Text(
                text = "Detalle del caso",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.White
            )
        } else if (error != null) {
            Text(
                text = error!!,
                color = Color(0xFFFF8A80)
            )

            Button(onClick = onVolverClick) {
                Text("Volver")
            }
        } else {
            caso?.let { actual ->

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color(0xFFF5F5F7),
                        contentColor = Color(0xFF202124)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = actual.asunto,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        HorizontalDivider()

                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = actual.descripcion,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        HorizontalDivider()

                        DatoCaso(
                            titulo = "Estado actual",
                            valor = formatearEstado(actual.estado)
                        )

                        DatoCaso(
                            titulo = "Categoría actual",
                            valor = nombreCategoria
                        )

                        DatoCaso(
                            titulo = "Encargado",
                            valor = when {
                                actual.idEncargado == null ->
                                    "Sin asignar"

                                nombreEncargado != null ->
                                    nombreEncargado!!

                                else ->
                                    "No se pudo cargar el perfil del encargado"
                            }
                        )

                        DatoCaso(
                            titulo = "Fecha de creación",
                            valor = formatearFecha(actual.fechaCreacion)
                        )

                        DatoCaso(
                            titulo = "Fecha de cierre",
                            valor = actual.fechaCierre?.let {
                                formatearFecha(it)
                            } ?: "Todavía no se ha cerrado"
                        )

                        DatoCaso(
                            titulo = "ID del caso",
                            valor = actual.idCaso
                        )

                        // Solo los técnicos pueden editar.
                        if (esTecnico) {
                            HorizontalDivider()

                            Text(
                                text = "Gestionar caso",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Categoría",
                                style = MaterialTheme.typography.labelLarge
                            )

                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = {
                                        categoriaMenuAbierto = true
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = nombreCategoriaSeleccionada,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                DropdownMenu(
                                    expanded = categoriaMenuAbierto,
                                    onDismissRequest = {
                                        categoriaMenuAbierto = false
                                    }
                                ) {
                                    categorias.forEach { categoria ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(categoria.nombre)
                                            },
                                            onClick = {
                                                categoriaSeleccionada =
                                                    categoria.idCategoria
                                                categoriaMenuAbierto = false
                                                mensaje = null
                                            }
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Estado",
                                style = MaterialTheme.typography.labelLarge
                            )

                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = {
                                        estadoMenuAbierto = true
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(formatearEstado(estadoSeleccionado))
                                }

                                DropdownMenu(
                                    expanded = estadoMenuAbierto,
                                    onDismissRequest = {
                                        estadoMenuAbierto = false
                                    }
                                ) {
                                    estadosDisponibles.forEach { estado ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(formatearEstado(estado))
                                            },
                                            onClick = {
                                                estadoSeleccionado = estado
                                                estadoMenuAbierto = false
                                                mensaje = null
                                            }
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        guardando = true
                                        mensaje = null

                                        val resultado =
                                            tecnicoRepository.actualizarCaso(
                                                idCaso = actual.idCaso,
                                                idCategoria = categoriaSeleccionada,
                                                estado = estadoSeleccionado
                                            )

                                        if (resultado.isSuccess) {
                                            repository.obtenerCasoPorId(idCaso)
                                                .onSuccess { actualizado ->
                                                    if (actualizado != null) {
                                                        caso = actualizado
                                                        categoriaSeleccionada =
                                                            actualizado.idCategoria
                                                        estadoSeleccionado =
                                                            actualizado.estado
                                                    }
                                                }

                                            mensaje = "Cambios guardados correctamente."
                                        } else {
                                            mensaje = resultado.exceptionOrNull()
                                                ?.message
                                                ?: "No se pudieron guardar los cambios."
                                        }

                                        guardando = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !guardando &&
                                        categoriaSeleccionada.isNotBlank() &&
                                        estadoSeleccionado in estadosDisponibles
                            ) {
                                if (guardando) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Guardando...")
                                } else {
                                    Text("Guardar cambios")
                                }
                            }

                            mensaje?.let {
                                Text(
                                    text = it,
                                    color = if (
                                        it.startsWith("Cambios guardados")
                                    ) {
                                        Color(0xFF2E7D32)
                                    } else {
                                        Color(0xFFB3261E)
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        mostrarHistorialProximamente = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Ver historial")
                }
            }
        }
    }

    if (mostrarHistorialProximamente) {
        AlertDialog(
            onDismissRequest = {
                mostrarHistorialProximamente = false
            },
            title = {
                Text(text = "Historial")
            },
            text = {
                Text(
                    text = "¡Esta funcionalidad estará disponible próximamente!"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarHistorialProximamente = false
                    }
                ) {
                    Text(text = "Entendido")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

}



@Composable
private fun DatoCaso(
    titulo: String,
    valor: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF555B66)
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF202124)
        )
    }
}

private fun formatearFecha(fecha: String?): String {
    if (fecha.isNullOrBlank()) return "Sin fecha"

    return runCatching {
        val formato = DateTimeFormatter.ofPattern(
            "dd/MM/yyyy HH:mm",
            Locale("es", "CO")
        )

        OffsetDateTime.parse(fecha)
            .atZoneSameInstant(ZoneId.of("America/Bogota"))
            .format(formato)
    }.getOrElse {
        fecha
    }
}

private fun formatearEstado(estado: String): String {
    return when (estado) {
        "registrado" -> "Registrado"
        "en_proceso" -> "En proceso"
        "solucionado" -> "Solucionado"
        "cerrado" -> "Cerrado"
        else -> estado
            .replace("_", " ")
            .replaceFirstChar { it.uppercase() }
    }


}

