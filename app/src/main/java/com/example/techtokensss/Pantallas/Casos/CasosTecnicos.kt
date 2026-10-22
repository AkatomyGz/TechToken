package com.example.techtokensss.Pantallas.CasosTecnico

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.techtokensss.Datos.Modelos.Caso
import com.example.techtokensss.Datos.Modelos.Categoria
import com.example.techtokensss.Pantallas.Casos.CasosRepository
import com.example.techtokensss.ui.components.*
import kotlinx.coroutines.launch


private enum class SeccionCasosTecnico {
    MIS_CASOS,
    BANDEJA
}

@Composable
fun CasosTecnicoScreen(
    onVolverClick: () -> Unit,
    onCasoClick: (String) -> Unit
) {
    // ------------------------------------------------------------------
    // LÓGICA (sin cambios respecto a la versión anterior)
    // ------------------------------------------------------------------
    val scope = rememberCoroutineScope()
    val repository = remember { CasosTecnicoRepository() }
    val casosRepository = remember { CasosRepository() }

    var seccion by remember { mutableStateOf(SeccionCasosTecnico.MIS_CASOS) }
    var misCasos by remember { mutableStateOf<List<Caso>>(emptyList()) }
    var bandeja by remember { mutableStateOf<List<Caso>>(emptyList()) }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }

    var cargando by remember { mutableStateOf(false) }
    var casoReclamando by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var busqueda by remember { mutableStateOf("") }

    fun cargarCasos() {
        scope.launch {
            cargando = true
            error = null
            mensaje = null

            repository.obtenerMisCasos()
                .onSuccess { misCasos = it }
                .onFailure {
                    error = it.message ?: "No se pudieron cargar tus casos."
                }

            repository.obtenerBandeja()
                .onSuccess { bandeja = it }
                .onFailure {
                    if (error == null) {
                        error = it.message ?: "No se pudo cargar la bandeja."
                    }
                }

            casosRepository.obtenerCategorias()
                .onSuccess { categorias = it }

            cargando = false
        }
    }

    LaunchedEffect(Unit) {
        cargarCasos()
    }

    val listaActual = when (seccion) {
        SeccionCasosTecnico.MIS_CASOS -> misCasos
        SeccionCasosTecnico.BANDEJA -> bandeja
    }

    val casosFiltrados = listaActual.filter { caso ->
        caso.asunto.contains(busqueda, ignoreCase = true) ||
                caso.descripcion.contains(busqueda, ignoreCase = true) ||
                caso.idCaso.contains(busqueda, ignoreCase = true)
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
            titulo = "Casos de soporte",
            subtitulo = "Gestiona tus casos y reclama nuevas solicitudes.",
            onVolverClick = onVolverClick,
            actions = {
                IconButton(
                    onClick = { cargarCasos() },
                    enabled = !cargando
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Actualizar lista"
                    )
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = seccion == SeccionCasosTecnico.MIS_CASOS,
                onClick = {
                    seccion = SeccionCasosTecnico.MIS_CASOS
                    busqueda = ""
                    error = null
                    mensaje = null
                },
                label = {
                    Text(
                        text = "Mis casos (${misCasos.size})",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = seccion == SeccionCasosTecnico.BANDEJA,
                onClick = {
                    seccion = SeccionCasosTecnico.BANDEJA
                    busqueda = ""
                    error = null
                    mensaje = null
                },
                label = {
                    Text(
                        text = "Bandeja (${bandeja.size})",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        TechTicketTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = "Buscar caso",
            placeholder = "Asunto, descripción o ID",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            },
            trailingIcon = if (busqueda.isNotEmpty()) {
                {
                    IconButton(onClick = { busqueda = "" }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Limpiar búsqueda"
                        )
                    }
                }
            } else {
                null
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader(
                titulo = if (seccion == SeccionCasosTecnico.BANDEJA) {
                    "Casos disponibles para reclamar"
                } else {
                    "Casos asignados a ti"
                },
                modifier = Modifier.weight(1f)
            )

            if (!cargando) {
                Text(
                    text = if (casosFiltrados.size == 1) {
                        "1 caso"
                    } else {
                        "${casosFiltrados.size} casos"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        mensaje?.let {
            FeedbackBanner(mensaje = it, tipo = TipoMensaje.Exito)
        }

        if (error != null && listaActual.isNotEmpty()) {
            FeedbackBanner(mensaje = error.orEmpty(), tipo = TipoMensaje.Error)
        }

        when {
            cargando -> {
                LoadingState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    mensaje = "Cargando casos..."
                )
            }

            error != null && listaActual.isEmpty() -> {
                ErrorState(
                    mensaje = error.orEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onReintentar = { cargarCasos() }
                )
            }

            casosFiltrados.isEmpty() -> {
                if (busqueda.isNotBlank()) {
                    EmptyState(
                        titulo = "No se encontraron casos",
                        descripcion = "Prueba con otro asunto, descripción o ID.",
                        icono = Icons.Outlined.SearchOff,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                } else {
                    EmptyState(
                        titulo = when (seccion) {
                            SeccionCasosTecnico.MIS_CASOS ->
                                "Todavía no tienes casos asignados."
                            SeccionCasosTecnico.BANDEJA ->
                                "No hay casos disponibles para reclamar."
                        },
                        icono = Icons.Outlined.Inbox,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(casosFiltrados, key = { it.idCaso }) { caso ->
                        val nombreCategoria = categorias
                            .find { it.idCategoria == caso.idCategoria }
                            ?.nombre ?: "Sin categoría"

                        if (seccion == SeccionCasosTecnico.BANDEJA) {
                            // Los casos de la bandeja no abren detalle: solo se reclaman.
                            CaseCard(
                                caso = caso,
                                nombreCategoria = nombreCategoria,
                                accion = {
                                    BotonReclamar(
                                        reclamando = casoReclamando == caso.idCaso,
                                        onClick = {
                                            scope.launch {
                                                casoReclamando = caso.idCaso
                                                error = null
                                                mensaje = null

                                                repository.reclamarCaso(caso.idCaso)
                                                    .onSuccess {
                                                        mensaje = "Caso reclamado correctamente."
                                                        cargarCasos()
                                                    }
                                                    .onFailure {
                                                        error = it.message
                                                            ?: "No se pudo reclamar el caso."
                                                    }

                                                casoReclamando = null
                                            }
                                        }
                                    )
                                }
                            )
                        } else {
                            CaseCard(
                                caso = caso,
                                nombreCategoria = nombreCategoria,
                                onClick = { onCasoClick(caso.idCaso) },
                                accion = { IndicadorVerDetalle() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BotonReclamar(
    reclamando: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !reclamando,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        if (reclamando) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = LocalContentColor.current
            )
            Spacer(Modifier.width(8.dp))
            Text("Reclamando...")
        } else {
            Text("Reclamar caso")
        }
    }
}

@Composable
private fun IndicadorVerDetalle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Ver detalle",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
    }
}