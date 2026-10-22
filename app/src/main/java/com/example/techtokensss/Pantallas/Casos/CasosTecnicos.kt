
package com.example.techtokensss.Pantallas.CasosTecnico

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techtokensss.Datos.Modelos.Caso
import com.example.techtokensss.Datos.Modelos.Categoria
import com.example.techtokensss.Pantallas.Casos.CasosRepository
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121318))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Casos de soporte",
                modifier = Modifier.weight(1f),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onVolverClick) {
                Text("Volver", color = Color.White)
            }
        }

        Text(
            text = "Gestiona tus casos y reclama nuevas solicitudes.",
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
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
                label = { Text("Mis casos (${misCasos.size})") },
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
                label = { Text("Bandeja (${bandeja.size})") },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Buscar caso") },
            placeholder = { Text("Asunto, descripción o ID") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF8AB4F8),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF8AB4F8),
                unfocusedLabelColor = Color.LightGray,
                cursorColor = Color.White
            )
        )

        if (seccion == SeccionCasosTecnico.BANDEJA) {
            Text(
                text = "Casos disponibles para reclamar",
                color = Color(0xFF8AB4F8),
                fontWeight = FontWeight.SemiBold
            )
        } else {
            Text(
                text = "Casos asignados a ti",
                color = Color(0xFF8AB4F8),
                fontWeight = FontWeight.SemiBold
            )
        }

        if (cargando) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else if (error != null && listaActual.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(error.orEmpty(), color = Color(0xFFFF8A80))
                Button(onClick = { cargarCasos() }) {
                    Text("Reintentar")
                }
            }
        } else if (casosFiltrados.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (seccion) {
                        SeccionCasosTecnico.MIS_CASOS ->
                            "Todavía no tienes casos asignados."
                        SeccionCasosTecnico.BANDEJA ->
                            "No hay casos disponibles para reclamar."
                    },
                    color = Color.LightGray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(casosFiltrados, key = { it.idCaso }) { caso ->
                    val categoria = categorias
                        .find { it.idCategoria == caso.idCategoria }
                        ?.nombre ?: "Sin categoría"

                    TarjetaCasoTecnico(
                        caso = caso,
                        nombreCategoria = categoria,
                        mostrarBotonReclamar =
                            seccion == SeccionCasosTecnico.BANDEJA,
                        reclamando = casoReclamando == caso.idCaso,
                        onClick = {
                            if (seccion == SeccionCasosTecnico.MIS_CASOS) {
                                onCasoClick(caso.idCaso)
                            }
                        },
                        onReclamarClick = {
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
            }
        }

        if (mensaje != null) {
            Text(mensaje.orEmpty(), color = Color(0xFF81C995))
        }

        if (error != null && listaActual.isNotEmpty()) {
            Text(error.orEmpty(), color = Color(0xFFFF8A80))
        }

        OutlinedButton(
            onClick = { cargarCasos() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar lista")
        }
    }
}

@Composable
private fun TarjetaCasoTecnico(
    caso: Caso,
    nombreCategoria: String,
    mostrarBotonReclamar: Boolean,
    reclamando: Boolean,
    onClick: () -> Unit,
    onReclamarClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (!mostrarBotonReclamar) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFF202127),
            contentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = caso.asunto,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = caso.descripcion,
                color = Color(0xFFD0D0D5),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )

            HorizontalDivider(color = Color(0xFF414149))

            Text(
                text = "Categoría: $nombreCategoria",
                color = Color(0xFFB8CFFF),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Estado: ${caso.estado}",
                color = Color(0xFFB8CFFF),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Fecha: ${caso.fechaCreacion}",
                color = Color.LightGray,
                style = MaterialTheme.typography.bodySmall
            )

            if (mostrarBotonReclamar) {
                Button(
                    onClick = onReclamarClick,
                    enabled = !reclamando,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (reclamando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Reclamando...")
                    } else {
                        Text("Reclamar caso")
                    }
                }
            } else {
                Text(
                    text = "Toca para ver el detalle",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
