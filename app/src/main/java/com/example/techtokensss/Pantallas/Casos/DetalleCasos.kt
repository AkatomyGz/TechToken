
package com.example.techtokensss.Pantallas.Casos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techtokensss.Datos.Modelos.Caso
import com.example.techtokensss.Datos.Modelos.Categoria
import kotlinx.coroutines.launch

@Composable
fun DetalleCasoScreen(
    idCaso: String,
    onVolverClick: () -> Unit
) {
    val repository = remember { CasosRepository() }
    val scope = rememberCoroutineScope()

    var caso by remember { mutableStateOf<Caso?>(null) }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(idCaso) {
        cargando = true
        error = null

        repository.obtenerCasoPorId(idCaso)
            .onSuccess { resultado ->
                caso = resultado
                if (resultado == null) {
                    error = "No se encontró el caso o no tienes permiso para verlo."
                }
            }
            .onFailure {
                error = it.message ?: "No se pudo cargar el caso."
            }

        repository.obtenerCategorias()
            .onSuccess { categorias = it }

        cargando = false
    }

    val nombreCategoria = categorias
        .find { it.idCategoria == caso?.idCategoria }
        ?.nombre ?: "Sin categoría"

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                    contentDescription = "Volver"
                )
            }

            Text(
                text = "Detalle del caso",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onVolverClick) {
                Text("Volver a mis casos")
            }
        } else {
            caso?.let { actual ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
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
                        DatoCaso("Fecha de creación", actual.fechaCreacion)
                        DatoCaso(
                            "Fecha de cierre",
                            actual.fechaCierre ?: "Todavía no se ha cerrado"
                        )

                        DatoCaso(
                            "ID del caso",
                            actual.idCaso
                        )
                    }
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
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
