package com.example.techtokensss.Pantallas.Casos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techtokensss.Datos.Modelos.Caso
import com.example.techtokensss.Datos.Modelos.Categoria

@Composable
fun CasosUsuarioScreen(
    onVolverClick: () -> Unit,
    onCasoClick: (String) -> Unit
) {
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

    // Cargar casos y categorías al abrir la pantalla.
    LaunchedEffect(Unit) {
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolverClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }

                Column {
                    Text(
                        "Mis casos",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Consulta tus solicitudes de soporte",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("Total de casos")
                    Text(
                        "${casos.size} solicitudes",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Casos registrados en tu cuenta",
                        fontSize = 13.sp
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResumenEstado(
                    "Registrados",
                    casos.count { it.estado == "registrado" }.toString(),
                    Modifier.weight(1f)
                )
                ResumenEstado(
                    "En proceso",
                    casos.count { it.estado == "en_proceso" }.toString(),
                    Modifier.weight(1f)
                )
                ResumenEstado(
                    "Finalizados",
                    casos.count {
                        it.estado == "cerrado" ||
                                it.estado == "solucionado"
                    }.toString(),
                    Modifier.weight(1f)
                )
            }
        }

        item {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Buscar por asunto...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                shape = RoundedCornerShape(14.dp)
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

        item {
            Text(
                "${casosFiltrados.size} casos encontrados",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }

        if (cargando) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (error != null) {
            item {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else if (casosFiltrados.isEmpty()) {
            item {
                Text(
                    "No se encontraron casos.",
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        } else {
            items(
                items = casosFiltrados,
                key = { it.idCaso }
            ) { caso ->
                TarjetaCasoReal(
                    caso = caso,
                    categoria = nombresCategorias[caso.idCategoria]
                        ?: "Categoría",
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                cantidad,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                titulo,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TarjetaCasoReal(
    caso: Caso,
    categoria: String,
    onClick: () -> Unit
) {
    val estadoVisible = when (caso.estado) {
        "registrado" -> "Registrado"
        "en_proceso" -> "En proceso"
        "solucionado" -> "Solucionado"
        "cerrado" -> "Cerrado"
        else -> caso.estado
    }

    val colorEstado = when (caso.estado) {
        "registrado" -> MaterialTheme.colorScheme.primaryContainer
        "en_proceso" -> MaterialTheme.colorScheme.tertiaryContainer
        "solucionado" -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val colorTextoEstado = when (caso.estado) {
        "registrado" -> MaterialTheme.colorScheme.onPrimaryContainer
        "en_proceso" -> MaterialTheme.colorScheme.onTertiaryContainer
        "solucionado" -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "CASO #${caso.idCaso.take(8)}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            Text(
                caso.asunto,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                caso.descripcion,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        categoria,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        caso.fechaCreacion.take(10),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            colorEstado,
                            RoundedCornerShape(50)
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                ) {
                    Text(
                        estadoVisible,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorTextoEstado
                    )
                }
            }
        }
    }
}