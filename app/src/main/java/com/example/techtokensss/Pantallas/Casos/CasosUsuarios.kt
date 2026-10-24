package com.example.techtokensss.Pantallas.Casos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.techtokensss.Datos.Modelos.Caso
import com.example.techtokensss.Datos.Modelos.Categoria
import com.example.techtokensss.ui.components.*
import com.example.techtokensss.ui.theme.TechTicketTheme

@Composable
fun CasosUsuarioScreen(
    onVolverClick: () -> Unit,
    onCasoClick: (String) -> Unit
) {
    // ------------------------------------------------------------------
    // LÓGICA (igual que antes; `recarga` solo permite reintentar la carga)
    // ------------------------------------------------------------------
    val repository = remember { CasosRepository() }

    var casos by remember { mutableStateOf<List<Caso>>(emptyList()) }
    var categorias by remember {
        mutableStateOf<List<Categoria>>(emptyList())
    }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    var busqueda by remember { mutableStateOf("") }
    var filtroSeleccionado by remember {
        mutableStateOf("Todos")
    }
    var recarga by remember { mutableStateOf(0) }

    // Cargar casos y categorías al abrir la pantalla (o al reintentar).
    LaunchedEffect(recarga) {
        cargando = true
        error = null

        repository.obtenerMisCasos()
            .onSuccess { resultado ->
                casos = resultado
            }
            .onFailure { excepcion ->
                error = excepcion.message
                    ?: "No se pudieron cargar los casos."
            }

        repository.obtenerCategorias()
            .onSuccess { resultado ->
                categorias = resultado
            }
            .onFailure { excepcion ->
                if (error == null) {
                    error = "No se pudieron cargar las categorías: " +
                            (excepcion.message ?: "error desconocido")
                }
            }

        cargando = false
    }

    val nombresCategorias = categorias.associate {
        it.idCategoria to it.nombre
    }

    val casosFiltrados = casos.filter { caso ->
        val coincideBusqueda =
            caso.asunto.contains(busqueda, ignoreCase = true)

        val coincideEstado = when (filtroSeleccionado) {
            "Finalizados" ->
                caso.estado == "cerrado" ||
                        caso.estado == "solucionado"

            "Registrados" ->
                caso.estado == "registrado"

            "En proceso" ->
                caso.estado == "en_proceso"

            else -> true
        }

        coincideBusqueda && coincideEstado
    }

    val cantRegistrados = casos.count { it.estado == "registrado" }
    val cantEnProceso = casos.count { it.estado == "en_proceso" }
    val cantFinalizados = casos.count {
        it.estado == "cerrado" || it.estado == "solucionado"
    }

    fun numero(n: Int): String = if (cargando) "–" else n.toString()

    val coloresEstado = TechTicketTheme.colors

    // ------------------------------------------------------------------
    // INTERFAZ
    // ------------------------------------------------------------------
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .screenInsets(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 12.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TechTicketTopBar(
                titulo = "Mis casos",
                subtitulo = "Consulta tus solicitudes de soporte",
                onVolverClick = onVolverClick
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Total de casos",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (cargando) {
                            "–"
                        } else if (casos.size == 1) {
                            "1 solicitud"
                        } else {
                            "${casos.size} solicitudes"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Casos registrados en tu cuenta",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResumenEstado(
                    titulo = "Registrados",
                    cantidad = numero(cantRegistrados),
                    colorIndicador = coloresEstado.registrado.solid,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                ResumenEstado(
                    titulo = "En proceso",
                    cantidad = numero(cantEnProceso),
                    colorIndicador = coloresEstado.enProceso.solid,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                ResumenEstado(
                    titulo = "Finalizados",
                    cantidad = numero(cantFinalizados),
                    colorIndicador = coloresEstado.solucionado.solid,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }

        item {
            TechTicketTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = "Buscar caso",
                placeholder = "Buscar por asunto",
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
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Todos",
                    "Registrados",
                    "En proceso",
                    "Finalizados"
                ).forEach { filtro ->
                    FilterChip(
                        selected = filtroSeleccionado == filtro,
                        onClick = { filtroSeleccionado = filtro },
                        label = { Text(filtro) }
                    )
                }
            }
        }

        if (!cargando && error == null) {
            item {
                Text(
                    text = if (casosFiltrados.size == 1) {
                        "1 caso encontrado"
                    } else {
                        "${casosFiltrados.size} casos encontrados"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (cargando) {
            item {
                LoadingState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    mensaje = "Cargando casos..."
                )
            }
        } else if (error != null) {
            item {
                ErrorState(
                    mensaje = error.orEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    onReintentar = { recarga++ }
                )
            }
        } else if (casosFiltrados.isEmpty()) {
            item {
                if (casos.isEmpty()) {
                    EmptyState(
                        titulo = "Aún no tienes casos registrados.",
                        descripcion = "Crea un caso desde el panel principal.",
                        icono = Icons.Outlined.Inbox,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    EmptyState(
                        titulo = "No se encontraron casos",
                        descripcion = "Prueba con otro filtro o búsqueda.",
                        icono = Icons.Outlined.SearchOff,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            items(
                items = casosFiltrados,
                key = { it.idCaso }
            ) { caso ->
                CaseCard(
                    caso = caso,
                    nombreCategoria = nombresCategorias[caso.idCategoria]
                        ?: "Sin categoría",
                    onClick = { onCasoClick(caso.idCaso) }
                )
            }
        }
    }
}

@Composable
private fun ResumenEstado(
    titulo: String,
    cantidad: String,
    colorIndicador: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = cantidad,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(colorIndicador, CircleShape)
                )
            }

            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}