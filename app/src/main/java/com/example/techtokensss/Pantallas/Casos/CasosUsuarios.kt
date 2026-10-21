package com.example.techtokensss.Pantallas.Casos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class CasoDemo(
    val id: String,
    val asunto: String,
    val descripcion: String,
    val categoria: String,
    val estado: String,
    val fecha: String
)

private val casosBase = listOf(
    CasoDemo(
        "001",
        "Problema con mi computador",
        "El equipo se apaga inesperadamente después de unos minutos de uso.",
        "Hardware",
        "En proceso",
        "15 oct 2026"
    ),
    CasoDemo(
        "002",
        "Error al conectarme al Wi-Fi",
        "Mi portátil detecta la red, pero no logra conectarse a Internet.",
        "Conectividad",
        "Solucionado",
        "12 oct 2026"
    ),
    CasoDemo(
        "003",
        "No puedo abrir Microsoft Office",
        "Al intentar abrir Word aparece un mensaje de error.",
        "Software",
        "Registrado",
        "10 oct 2026"
    ),
    CasoDemo(
        "004",
        "Problema de acceso a mi cuenta",
        "No puedo ingresar con mis credenciales habituales.",
        "Cuenta",
        "Cerrado",
        "08 oct 2026"
    )
)

// Datos ficticios para probar el desplazamiento de la pantalla.
private val casosDemo = List(24) { index ->
    val casoBase = casosBase[index % casosBase.size]

    casoBase.copy(
        id = "%03d".format(index + 1),
        asunto = if (index < casosBase.size) {
            casoBase.asunto
        } else {
            "${casoBase.asunto} (${index + 1})"
        }
    )
}

@Composable
fun CasosUsuarioScreen(
    onVolverClick: () -> Unit,
    onCasoClick: (String) -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    val casosFiltrados = casosDemo.filter { caso ->
        val coincideBusqueda =
            caso.asunto.contains(busqueda, ignoreCase = true)

        val coincideEstado = when (filtroSeleccionado) {
            "Finalizados" ->
                caso.estado == "Cerrado" ||
                        caso.estado == "Solucionado"

            "Registrados" ->
                caso.estado == "Registrado"

            "En proceso" ->
                caso.estado == "En proceso"

            else -> true
        }

        coincideBusqueda && coincideEstado
    }

    // Una sola lista controla el desplazamiento de toda la pantalla.
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 12.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cabecera
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolverClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mis casos",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Consulta tus solicitudes de soporte",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Resumen general
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Total de casos",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "${casosDemo.size} solicitudes",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "Revisa el estado de tus solicitudes",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Contadores por estado
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResumenEstado(
                    titulo = "Registrados",
                    cantidad = casosDemo.count {
                        it.estado == "Registrado"
                    }.toString(),
                    modifier = Modifier.weight(1f)
                )

                ResumenEstado(
                    titulo = "En proceso",
                    cantidad = casosDemo.count {
                        it.estado == "En proceso"
                    }.toString(),
                    modifier = Modifier.weight(1f)
                )

                ResumenEstado(
                    titulo = "Finalizados",
                    cantidad = casosDemo.count {
                        it.estado == "Cerrado" ||
                                it.estado == "Solucionado"
                    }.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Buscador
        item {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text("Buscar por asunto...")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Filtros existentes
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
                        onClick = {
                            filtroSeleccionado = filtro
                        },
                        label = {
                            Text(filtro)
                        }
                    )
                }
            }
        }

        // Cantidad encontrada
        item {
            Text(
                text = "${casosFiltrados.size} casos encontrados",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Casos: forman parte de la misma lista desplazable.
        items(
            items = casosFiltrados,
            key = { it.id }
        ) { caso ->
            TarjetaCasoUsuario(
                caso = caso,
                onClick = {
                    onCasoClick(caso.id)
                }
            )
        }

        // Mensaje si la búsqueda no encuentra resultados.
        if (casosFiltrados.isEmpty()) {
            item {
                Text(
                    text = "No se encontraron casos.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = cantidad,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = titulo,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TarjetaCasoUsuario(
    caso: CasoDemo,
    onClick: () -> Unit
) {
    val colorEstado = when (caso.estado) {
        "Registrado" -> MaterialTheme.colorScheme.primary
        "En proceso" -> MaterialTheme.colorScheme.tertiary
        "Solucionado" -> MaterialTheme.colorScheme.secondary
        "Cerrado" -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.primary
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CASO #${caso.id}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = caso.fecha,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = caso.asunto,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = caso.descripcion,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = caso.categoria,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = colorEstado,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                ) {
                    Text(
                        text = caso.estado,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}