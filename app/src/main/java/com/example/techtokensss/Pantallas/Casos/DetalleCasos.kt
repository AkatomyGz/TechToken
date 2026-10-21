
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
import com.example.techtokensss.Datos.Modelos.Caso
import com.example.techtokensss.Datos.Modelos.Categoria

@Composable
fun DetalleCasoScreen(
    idCaso: String,
    onVolverClick: () -> Unit,
    onHistorialClick: (String) -> Unit
) {
    val repository = remember { CasosRepository() }

    var caso by remember { mutableStateOf<Caso?>(null) }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var nombreEncargado by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(idCaso) {
        cargando = true
        error = null
        caso = null
        nombreEncargado = null

        // Primero cargamos el caso.
        repository.obtenerCasoPorId(idCaso)
            .onSuccess { resultado ->
                caso = resultado

                if (resultado == null) {
                    error = "No se encontró el caso o no tienes permiso para verlo."
                } else {
                    // Después consultamos al encargado, si existe.
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
                    imageVector = Icons.Default.ArrowBack,
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
                Text("Volver a mis casos")
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

                        Text(actual.descripcion)

                        HorizontalDivider()

                        DatoCaso("Estado", actual.estado)
                        DatoCaso("Categoría", nombreCategoria)

                        DatoCaso(
                            "Encargado",
                            when {
                                actual.idEncargado == null -> "Sin asignar"
                                nombreEncargado != null -> nombreEncargado!!
                                else -> "No se pudo cargar el perfil del encargado"
                            }
                        )

                        DatoCaso(
                            "Fecha de creación",
                            actual.fechaCreacion
                        )

                        DatoCaso(
                            "Fecha de cierre",
                            actual.fechaCierre
                                ?: "Todavía no se ha cerrado"
                        )

                        DatoCaso("ID del caso", actual.idCaso)
                    }
                }

                OutlinedButton(
                    onClick = { onHistorialClick(actual.idCaso) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))

                    Text("Ver historial")
                }
            }
        }
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
