
package com.example.techtokensss.Pantallas.Dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techtokensss.Pantallas.CasosTecnico.CasosTecnicoRepository
import kotlinx.coroutines.launch
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.awaitCancellation
import com.example.techtokensss.Datos.Supabase.supabase
import io.github.jan.supabase.realtime.realtime

private val ColorRegistrado = Color(0xFF4F8EF7)
private val ColorEnProceso = Color(0xFFFFB74D)
private val ColorSolucionado = Color(0xFF36A879)
private val ColorCerrado = Color(0xFF9B8AFB)

@Composable
fun DashboardScreen(
    onMenuClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onCasosClick: () -> Unit,
    onConfiguracionClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
) {
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { menuAbierto = true }) {
                    Text("☰")
                }

                Text(
                    text = "TechToken",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(onClick = onPerfilClick) {
                    Text("👤")
                }
            }

            Spacer(Modifier.height(30.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Hola 👋",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Resumen de tus casos",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            // Gráfico pastel
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Distribución de casos",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(16.dp))

                    when {
                        cargando -> {
                            CircularProgressIndicator()
                        }

                        error != null -> {
                            Text(
                                text = error!!,
                                color = MaterialTheme.colorScheme.error
                            )

                            Spacer(Modifier.height(8.dp))

                            Button(onClick = { cargarEstadisticas() }) {
                                Text("Reintentar")
                            }
                        }

                        estadisticas.total == 0 -> {
                            Text(
                                text = "Todavía no tienes casos asignados.",
                                color = MaterialTheme.colorScheme.onSurface
                                    .copy(alpha = 0.7f)
                            )
                        }


                        else -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                GraficoPastel(
                                    estadisticas = estadisticas,
                                    modifier = Modifier.weight(1f)
                                )

                                Column(
                                    modifier = Modifier.weight(1.2f),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    LeyendaEstado(
                                        titulo = "Registrados",
                                        color = ColorRegistrado
                                    )

                                    LeyendaEstado(
                                        titulo = "En proceso",
                                        color = ColorEnProceso
                                    )

                                    LeyendaEstado(
                                        titulo = "Solucionados",
                                        color = ColorSolucionado
                                    )

                                    LeyendaEstado(
                                        titulo = "Cerrados",
                                        color = ColorCerrado
                                    )
                                }
                            }
                        }

                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Estado de casos",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaEstado(
                    titulo = "Registrados",
                    cantidad = estadisticas.registrados.toString(),
                    modifier = Modifier.weight(1f)
                )

                TarjetaEstado(
                    titulo = "En proceso",
                    cantidad = estadisticas.enProceso.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaEstado(
                    titulo = "Cerrados",
                    cantidad = estadisticas.cerrados.toString(),
                    modifier = Modifier.weight(1f)
                )

                TarjetaEstado(
                    titulo = "Solucionados",
                    cantidad = estadisticas.solucionados.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            if (!cargando && error == null) {
                Text(
                    text = "Total de casos asignados: ${estadisticas.total}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (menuAbierto) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000))
                    .clickable { menuAbierto = false }
            )

            MenuLateral(
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
}

@Composable
private fun GraficoPastel(
    estadisticas: EstadisticasCasos,
    modifier: Modifier = Modifier
) {
    val segmentos = listOf(
        estadisticas.registrados to ColorRegistrado,
        estadisticas.enProceso to ColorEnProceso,
        estadisticas.solucionados to ColorSolucionado,
        estadisticas.cerrados to ColorCerrado
    )

    Canvas(
        modifier = modifier
            .size(140.dp)
            .padding(4.dp)
    ) {
        var anguloInicial = -90f
        val total = estadisticas.total.toFloat()

        if (total > 0f) {
            segmentos.forEach { (cantidad, color) ->
                if (cantidad > 0) {
                    val barrido = cantidad / total * 360f

                    drawArc(
                        color = color,
                        startAngle = anguloInicial,
                        sweepAngle = barrido,
                        useCenter = true
                    )

                    anguloInicial += barrido
                }
            }
        }
    }
}

@Composable
private fun LeyendaEstado(
    titulo: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color = color)
        }

        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
fun TarjetaEstado(
    titulo: String,
    cantidad: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(78.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            Text(
                text = cantidad,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
