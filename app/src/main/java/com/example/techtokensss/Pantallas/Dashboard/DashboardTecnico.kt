
package com.example.techtokensss.Pantallas.Dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.techtokensss.Datos.Supabase.supabase
import com.example.techtokensss.Pantallas.CasosTecnico.CasosTecnicoRepository
import com.example.techtokensss.ui.components.*
import com.example.techtokensss.ui.theme.APP_NAME
import com.example.techtokensss.ui.theme.TechTicketTheme
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    onMenuClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onCasosClick: () -> Unit,
    onConfiguracionClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
) {
    // Lógica y suscripción Realtime
    val repository = remember { CasosTecnicoRepository() }
    val scope = rememberCoroutineScope()

    var menuAbierto by remember { mutableStateOf(false) }
    var estadisticas by remember {
        mutableStateOf(EstadisticasCasos())
    }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun cargarEstadisticas() {
        scope.launch {
            cargando = true
            error = null

            repository.obtenerEstadisticas()
                .onSuccess {
                    estadisticas = it
                }
                .onFailure {
                    error = it.message
                        ?: "No se pudieron cargar las estadísticas."
                }

            cargando = false
        }
    }

    LaunchedEffect(Unit) {
        cargarEstadisticas()

        val canal = supabase.channel("dashboard-casos")

        val cambios = canal.postgresChangeFlow<PostgresAction>(
            schema = "public"
        ) {
            table = "casos"
        }

        val trabajo = launch {
            cambios.collect {
                cargarEstadisticas()
            }
        }

        try {
            canal.subscribe()
            awaitCancellation()
        } finally {
            trabajo.cancel()
            supabase.realtime.removeChannel(canal)
        }
    }

    // Mientras carga o si hubo error, no se muestran números engañosos.
    val hayDatos = !cargando && error == null

    fun valor(n: Int): String =
        if (hayDatos) n.toString() else "–"

    val coloresEstado = TechTicketTheme.colors

    // Interfaz
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .screenInsets()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            TechTicketTopBar(
                titulo = APP_NAME,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            menuAbierto = true
                            onMenuClick()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Abrir menú"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onPerfilClick) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "Perfil"
                        )
                    }
                }
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Panel del técnico",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Resumen de tus casos asignados.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onCasosClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Outlined.Folder,
                    contentDescription = null
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Ver casos y bandeja",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Distribución de casos
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Distribución de casos",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    when {
                        cargando -> {
                            LoadingState(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            )
                        }

                        error != null -> {
                            ErrorState(
                                mensaje = error.orEmpty(),
                                modifier = Modifier.fillMaxWidth(),
                                onReintentar = {
                                    cargarEstadisticas()
                                }
                            )
                        }

                        estadisticas.total == 0 -> {
                            EmptyState(
                                titulo = "Todavía no tienes casos asignados.",
                                descripcion = "Reclama casos desde la bandeja para ver tus estadísticas.",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        else -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                GraficoDona(
                                    estadisticas = estadisticas
                                )
                            }
                        }
                    }
                }
            }

            // Cantidades por estado
            Text(
                text = "Estado de casos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaEstado(
                    titulo = "Registrados",
                    cantidad = valor(estadisticas.registrados),
                    colorIndicador = coloresEstado.registrado.solid,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                TarjetaEstado(
                    titulo = "En proceso",
                    cantidad = valor(estadisticas.enProceso),
                    colorIndicador = coloresEstado.enProceso.solid,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaEstado(
                    titulo = "Solucionados",
                    cantidad = valor(estadisticas.solucionados),
                    colorIndicador = coloresEstado.solucionado.solid,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                TarjetaEstado(
                    titulo = "Cerrados",
                    cantidad = valor(estadisticas.cerrados),
                    colorIndicador = coloresEstado.cerrado.solid,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }

        MenuLateralOverlay(
            visible = menuAbierto,
            onDismiss = {
                menuAbierto = false
            },
            onPerfilClick = {
                menuAbierto = false
                onPerfilClick()
            },
            onCasosClick = {
                menuAbierto = false
                onCasosClick()
            },
            onConfiguracionClick = {
                menuAbierto = false
                onConfiguracionClick()
            },
            onCerrarSesionClick = {
                menuAbierto = false
                onCerrarSesionClick()
            }
        )
    }
}

/** Gráfico de dona con el total de casos en el centro. */
@Composable
private fun GraficoDona(
    estadisticas: EstadisticasCasos,
    modifier: Modifier = Modifier
) {
    val colores = TechTicketTheme.colors
    val colorPista = MaterialTheme.colorScheme.surfaceVariant

    val segmentos = listOf(
        estadisticas.registrados to colores.registrado.solid,
        estadisticas.enProceso to colores.enProceso.solid,
        estadisticas.solucionados to colores.solucionado.solid,
        estadisticas.cerrados to colores.cerrado.solid
    )

    val descripcion =
        "Distribución de casos: ${estadisticas.registrados} registrados, " +
                "${estadisticas.enProceso} en proceso, " +
                "${estadisticas.solucionados} solucionados, " +
                "${estadisticas.cerrados} cerrados"

    Box(
        modifier = modifier
            .size(190.dp)
            .semantics {
                contentDescription = descripcion
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val grosor = 26.dp.toPx()
            val origen = Offset(grosor / 2f, grosor / 2f)
            val tamano = Size(
                size.width - grosor,
                size.height - grosor
            )
            val total = estadisticas.total.toFloat()

            // Anillo de fondo
            drawArc(
                color = colorPista,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = origen,
                size = tamano,
                style = Stroke(width = grosor)
            )

            if (total > 0f) {
                val visibles = segmentos.count {
                    it.first > 0
                }

                val hueco = if (visibles > 1) 3f else 0f
                var anguloInicial = -90f

                segmentos.forEach { (cantidad, color) ->
                    if (cantidad > 0) {
                        val barrido = cantidad / total * 360f

                        drawArc(
                            color = color,
                            startAngle = anguloInicial + hueco / 2f,
                            sweepAngle = barrido - hueco,
                            useCenter = false,
                            topLeft = origen,
                            size = tamano,
                            style = Stroke(width = grosor)
                        )

                        anguloInicial += barrido
                    }
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = estadisticas.total.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "asignados",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Tarjeta con una cantidad. Se mantiene pública y con los mismos
 * parámetros; colorIndicador es opcional.
 */
@Composable
fun TarjetaEstado(
    titulo: String,
    cantidad: String,
    modifier: Modifier = Modifier,
    colorIndicador: Color = Color.Unspecified
) {
    Card(
        modifier = modifier,
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (colorIndicador != Color.Unspecified) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                colorIndicador,
                                CircleShape
                            )
                    )
                }

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Text(
                text = cantidad,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
