package com.example.techtokensss.ui.components

import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.hypot
import kotlin.random.Random

/**
 * Fondo con trazas tipo circuito impreso y pulsos de luz que las recorren.
 * - Dibujado solo con Canvas y los colores del tema (sin imágenes).
 * - Muchos pulsos a la vez, cada uno con su propia velocidad, fase y sentido.
 * - Las trazas nacen sobre todo en los bordes, para que se vean fuera de la tarjeta.
 * - Si el usuario desactiva las animaciones del sistema, queda estático.
 *
 * Uso: CircuitBackground(Modifier.fillMaxSize()) { ...contenido de la pantalla... }
 */
@Composable
fun CircuitBackground(
    modifier: Modifier = Modifier,
    animado: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val fondo = MaterialTheme.colorScheme.background
    val color = MaterialTheme.colorScheme.primary
    val intensidad = if (fondo.luminance() < 0.5f) 1f else 0.75f

    // Respeta "Escala de duración de animación = desactivada" del sistema.
    val contexto = LocalContext.current
    val animacionesActivas = remember {
        Settings.Global.getFloat(
            contexto.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) != 0f
    }
    val animar = animado && animacionesActivas

    // Un solo reloj de 60 s. Cada traza lo recorre un número entero de veces
    // (ciclos), por eso nunca hay un salto cuando el reloj se reinicia.
    val transicion = rememberInfiniteTransition(label = "circuitos")
    val progreso by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulsos_circuito"
    )

    val densidad = LocalDensity.current

    BoxWithConstraints(modifier = modifier.background(fondo)) {
        val celda = with(densidad) { 28.dp.toPx() }
        val columnas = (with(densidad) { maxWidth.toPx() } / celda).toInt() + 1
        val filas = (with(densidad) { maxHeight.toPx() } / celda).toInt() + 1

        // Se generan una vez por tamaño de pantalla; con semilla fija el dibujo es estable.
        val trazas = remember(columnas, filas) {
            generarTrazas(columnas, filas, celda, semilla = 7)
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val grosor = 1.5.dp.toPx()
            val estilo = Stroke(
                width = grosor,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
            val radioNodo = 4.dp.toPx()
            val paso = 7.dp.toPx()
            val recorridoExtra = 60.dp.toPx()

            trazas.forEach { traza ->
                drawPath(
                    path = traza.camino,
                    color = color.copy(alpha = 0.14f * intensidad),
                    style = estilo
                )

                // Nodos en los extremos, como los pads de un circuito.
                drawCircle(
                    color = color.copy(alpha = 0.28f * intensidad),
                    radius = radioNodo,
                    center = traza.puntos.first(),
                    style = estilo
                )
                drawCircle(
                    color = color.copy(alpha = 0.28f * intensidad),
                    radius = radioNodo * 0.45f,
                    center = traza.puntos.last()
                )

                // Pulso de luz con estela corta. Se lee `progreso` aquí (fase de
                // dibujo) para no recomponer la pantalla en cada fotograma.
                if (animar && traza.conPulso) {
                    val fase = (progreso * traza.ciclos + traza.desfase) % 1f
                    val avance = fase * (traza.total + recorridoExtra)

                    for (k in 0..5) {
                        val distancia = avance - k * paso
                        if (distancia < 0f || distancia > traza.total) continue

                        val posicion = traza.posicion(
                            if (traza.invertido) traza.total - distancia else distancia
                        )
                        val brillo = traza.brillo * intensidad

                        if (k == 0) {
                            drawCircle(
                                color = color.copy(alpha = 0.12f * brillo),
                                radius = 8.dp.toPx(),
                                center = posicion
                            )
                        }

                        drawCircle(
                            color = color.copy(
                                alpha = ((0.65f - k * 0.1f) * brillo)
                                    .coerceAtLeast(0f)
                            ),
                            radius = (3f - k * 0.4f).dp.toPx(),
                            center = posicion
                        )
                    }
                }
            }

            // Viñeta suave: atenúa el centro para que el contenido se lea limpio.
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(fondo.copy(alpha = 0.55f), Color.Transparent),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.maxDimension * 0.5f
                )
            )
        }

        content()
    }
}

// ---------------------------------------------------------------------
// Generación de las trazas (rejilla con giros de 45° y 90°)
// ---------------------------------------------------------------------

private class Traza(
    val puntos: List<Offset>,
    private val acumulado: FloatArray,
    val total: Float,
    val camino: Path,
    val conPulso: Boolean,
    val desfase: Float,
    /** Vueltas completas que da el pulso en 60 s: más ciclos = más rápido. */
    val ciclos: Int,
    val brillo: Float,
    /** Si es true, el pulso recorre la traza de fin a inicio. */
    val invertido: Boolean
) {
    /** Punto situado a [distancia] píxeles del inicio de la traza. */
    fun posicion(distancia: Float): Offset {
        val d = distancia.coerceIn(0f, total)
        var i = 1
        while (i < acumulado.size - 1 && acumulado[i] < d) i++

        val inicio = acumulado[i - 1]
        val largo = acumulado[i] - inicio
        val f = if (largo > 0f) (d - inicio) / largo else 0f

        val a = puntos[i - 1]
        val b = puntos[i]
        return Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)
    }
}

// Las 8 direcciones en orden circular: girar ±1 = 45°, ±2 = 90°.
private val DIRECCIONES = listOf(
    1 to 0, 1 to 1, 0 to 1, -1 to 1,
    -1 to 0, -1 to -1, 0 to -1, 1 to -1
)

// Velocidades posibles (vueltas por 60 s): de 3 (lento, 20 s) a 8 (rápido, 7,5 s).
private val CICLOS_POSIBLES = listOf(3, 4, 5, 6, 8)

/** Punto de partida en franjas superior, inferior o laterales (fuera de la tarjeta). */
private fun puntoInicial(azar: Random, columnas: Int, filas: Int): Pair<Int, Int> =
    when (azar.nextInt(3)) {
        0 -> azar.nextInt(columnas + 1) to azar.nextInt(0, (filas * 0.3f).toInt() + 1)
        1 -> azar.nextInt(columnas + 1) to azar.nextInt((filas * 0.7f).toInt(), filas + 1)
        else -> {
            val x = if (azar.nextBoolean()) {
                azar.nextInt(0, 3)
            } else {
                azar.nextInt(maxOf(columnas - 2, 0), columnas + 1)
            }
            x to azar.nextInt(filas + 1)
        }
    }

private fun generarTrazas(
    columnas: Int,
    filas: Int,
    celda: Float,
    semilla: Int
): List<Traza> {
    val azar = Random(semilla)
    val cantidad = ((columnas * filas) / 12).coerceIn(24, 60)
    val resultado = mutableListOf<Traza>()
    var intentos = 0

    // Se reintenta cuando una traza sale demasiado corta o choca con el borde.
    while (resultado.size < cantidad && intentos < cantidad * 6) {
        intentos++

        var (x, y) = puntoInicial(azar, columnas, filas)
        var direccion = azar.nextInt(8)

        val puntos = mutableListOf(Offset(x * celda, y * celda))
        val segmentos = azar.nextInt(3, 7)

        for (s in 0 until segmentos) {
            val pasos = azar.nextInt(2, 7)
            val (dx, dy) = DIRECCIONES[direccion]
            val nx = x + dx * pasos
            val ny = y + dy * pasos

            if (nx !in 0..columnas || ny !in 0..filas) break

            x = nx
            y = ny
            puntos.add(Offset(x * celda, y * celda))

            val giro = if (azar.nextBoolean()) 1 else -1
            direccion = (direccion + giro * azar.nextInt(1, 3) + 8) % 8
        }

        if (puntos.size < 3) continue

        val acumulado = FloatArray(puntos.size)
        for (i in 1 until puntos.size) {
            acumulado[i] = acumulado[i - 1] + hypot(
                puntos[i].x - puntos[i - 1].x,
                puntos[i].y - puntos[i - 1].y
            )
        }

        if (acumulado.last() < celda * 6) continue

        val camino = Path().apply {
            moveTo(puntos[0].x, puntos[0].y)
            for (i in 1 until puntos.size) {
                lineTo(puntos[i].x, puntos[i].y)
            }
        }

        resultado.add(
            Traza(
                puntos = puntos,
                acumulado = acumulado,
                total = acumulado.last(),
                camino = camino,
                conPulso = azar.nextFloat() < 0.8f,
                desfase = azar.nextFloat(),
                ciclos = CICLOS_POSIBLES[azar.nextInt(CICLOS_POSIBLES.size)],
                brillo = 0.6f + azar.nextFloat() * 0.4f,
                invertido = azar.nextBoolean()
            )
        )
    }

    return resultado
}