
package com.example.techtokensss.Pantallas.Casos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.techtokensss.Datos.Modelos.Caso
import com.example.techtokensss.Datos.Modelos.Categoria
import com.example.techtokensss.Pantallas.CasosTecnico.CasosTecnicoRepository
import com.example.techtokensss.ui.components.*
import kotlinx.coroutines.launch
import com.example.techtokensss.Datos.MondoDBAtlas.TechTokenApi
import com.example.techtokensss.Datos.Supabase.AuthSupa


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
    val api = remember { TechTokenApi() }
    val authSupa = remember { AuthSupa() }
    var caso by remember { mutableStateOf<Caso?>(null) }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var guardando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var mensajeEsExito by remember { mutableStateOf(false) }
    var nombreEncargado by remember { mutableStateOf<String?>(null) }

    var categoriaSeleccionada by remember { mutableStateOf("") }
    var estadoSeleccionado by remember { mutableStateOf("") }

    var categoriaMenuAbierto by remember { mutableStateOf(false) }
    var estadoMenuAbierto by remember { mutableStateOf(false) }

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
            .background(MaterialTheme.colorScheme.background)
            .screenInsets()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TechTicketTopBar(
            titulo = "Detalle del caso",
            subtitulo = caso?.let {
                "Caso #${it.idCaso.take(8)}"
            },
            onVolverClick = onVolverClick
        )

        if (cargando) {
            LoadingState(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                mensaje = "Cargando caso..."
            )
        } else if (error != null) {
            ErrorState(
                mensaje = error.orEmpty(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = onVolverClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Volver")
            }
        } else {
            caso?.let { actual ->

                // Resumen del caso
                TarjetaSeccion {
                    StatusChip(estado = actual.estado)

                    Text(
                        text = actual.asunto,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    InfoRow(
                        titulo = "Descripción",
                        valor = actual.descripcion
                    )
                }

                // Información general
                TarjetaSeccion(titulo = "Información del caso") {
                    InfoRow(
                        titulo = "Categoría actual",
                        valor = nombreCategoria
                    )

                    InfoRow(
                        titulo = "Encargado",
                        valor = if (actual.idEncargado == null) {
                            "Sin asignar"
                        } else {
                            nombreEncargado
                                ?: "No se pudo cargar el perfil del encargado"
                        }
                    )

                    InfoRow(
                        titulo = "Fecha de creación",
                        valor = formatearFecha(actual.fechaCreacion)
                    )

                    InfoRow(
                        titulo = "Fecha de cierre",
                        valor = actual.fechaCierre?.let {
                            formatearFecha(it)
                        } ?: "Todavía no se ha cerrado"
                    )

                    InfoRow(
                        titulo = "ID del caso",
                        valor = actual.idCaso
                    )
                }

                // Gestión exclusiva del técnico
                if (esTecnico) {
                    TarjetaSeccion(titulo = "Gestionar caso") {
                        Text(
                            text = "Actualiza la categoría y el estado del caso.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        CampoSelector(
                            etiqueta = "Categoría",
                            valor = nombreCategoriaSeleccionada,
                            expandido = categoriaMenuAbierto,
                            onExpandidoChange = {
                                categoriaMenuAbierto = it
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

                        CampoSelector(
                            etiqueta = "Estado",
                            valor = formatearEstado(estadoSeleccionado),
                            expandido = estadoMenuAbierto,
                            onExpandidoChange = {
                                estadoMenuAbierto = it
                            }
                        ) {
                            estadosDisponibles.forEach { estado ->
                                DropdownMenuItem(
                                    text = {
                                        StatusChip(estado = estado)
                                    },
                                    onClick = {
                                        estadoSeleccionado = estado
                                        estadoMenuAbierto = false
                                        mensaje = null
                                    }
                                )
                            }
                        }

                        Button(

                            onClick = {
                                scope.launch {
                                    guardando = true
                                    mensaje = null
                                    mensajeEsExito = false

                                    // Conservar los valores anteriores para el historial.
                                    val estadoAnterior = actual.estado
                                    val categoriaAnterior = actual.idCategoria

                                    val estadoCambio = estadoAnterior != estadoSeleccionado
                                    val categoriaCambio = categoriaAnterior != categoriaSeleccionada

                                    if (!estadoCambio && !categoriaCambio) {
                                        mensaje = "No hay cambios para guardar."
                                        mensajeEsExito = false
                                        guardando = false
                                        return@launch
                                    }

                                    val resultado = tecnicoRepository.actualizarCaso(
                                        idCaso = actual.idCaso,
                                        idCategoria = categoriaSeleccionada,
                                        estado = estadoSeleccionado
                                    )

                                    if (resultado.isFailure) {
                                        mensaje = resultado.exceptionOrNull()?.message
                                            ?: "No se pudieron guardar los cambios."
                                        mensajeEsExito = false
                                        guardando = false
                                        return@launch
                                    }

                                    // Los cambios ya se guardaron en Supabase.
                                    // Ahora intentamos registrar la novedad.
                                    val cambios = mutableMapOf<String, String>()
                                    val descripciones = mutableListOf<String>()

                                    if (estadoCambio) {
                                        cambios["estado_anterior"] = estadoAnterior
                                        cambios["estado_nuevo"] = estadoSeleccionado

                                        descripciones.add(
                                            "Estado: ${formatearEstado(estadoAnterior)} → " +
                                                    formatearEstado(estadoSeleccionado)
                                        )
                                    }

                                    if (categoriaCambio) {
                                        val nombreAnterior = categorias
                                            .find { it.idCategoria == categoriaAnterior }
                                            ?.nombre ?: categoriaAnterior

                                        val nombreNuevo = categorias
                                            .find { it.idCategoria == categoriaSeleccionada }
                                            ?.nombre ?: categoriaSeleccionada

                                        cambios["categoria_anterior"] = nombreAnterior
                                        cambios["categoria_nueva"] = nombreNuevo

                                        descripciones.add(
                                            "Categoría: $nombreAnterior → $nombreNuevo"
                                        )
                                    }

                                    val token = authSupa.obtenerTokenActual()

                                    val resultadoHistorial = if (!token.isNullOrBlank()) {
                                        api.registrarEvento(
                                            idCaso = actual.idCaso,
                                            token = token,
                                            tipoActividad = "caso_actualizado",
                                            descripcion = descripciones.joinToString(". "),
                                            cambios = cambios
                                        )
                                    } else {
                                        Result.failure(
                                            Exception("No se encontró la sesión para registrar el historial.")
                                        )
                                    }

                                    // Actualizar la pantalla con los datos guardados.
                                    repository.obtenerCasoPorId(idCaso)
                                        .onSuccess { actualizado ->
                                            if (actualizado != null) {
                                                caso = actualizado
                                                categoriaSeleccionada = actualizado.idCategoria
                                                estadoSeleccionado = actualizado.estado
                                            }
                                        }

                                    if (resultadoHistorial.isSuccess) {
                                        mensaje = "Cambios guardados y novedad registrada en el historial."
                                        mensajeEsExito = true
                                    } else {
                                        mensaje = "Los cambios se guardaron, pero no se pudo registrar " +
                                                "la novedad en el historial. " +
                                                (resultadoHistorial.exceptionOrNull()?.message ?: "")
                                        mensajeEsExito = false
                                    }

                                    guardando = false
                                }
                            },

                            ) {
                            if (guardando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = LocalContentColor.current
                                )

                                Spacer(Modifier.width(8.dp))
                                Text("Guardando...")
                            } else {
                                Text("Guardar cambios")
                            }
                        }

                        mensaje?.let {
                            FeedbackBanner(
                                mensaje = it,
                                tipo = if (mensajeEsExito) {
                                    TipoMensaje.Exito
                                } else {
                                    TipoMensaje.Error
                                }
                            )
                        }
                    }
                }

                // Abrir el historial real del caso
                OutlinedButton(
                    onClick = {
                        onHistorialClick(actual.idCaso)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))
                    Text("Ver historial")
                }
            }
        }
    }
}

/** Tarjeta contenedora de una sección del detalle. */
@Composable
private fun TarjetaSeccion(
    modifier: Modifier = Modifier,
    titulo: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (titulo != null) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            content()
        }
    }
}

/** Selector desplegable con etiqueta. */
@Composable
private fun CampoSelector(
    etiqueta: String,
    valor: String,
    expandido: Boolean,
    onExpandidoChange: (Boolean) -> Unit,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    onExpandidoChange(true)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                ),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = valor,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = expandido,
                onDismissRequest = {
                    onExpandidoChange(false)
                },
                content = contenido
            )
        }
    }
}
