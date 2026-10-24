package com.example.techtokensss.Pantallas.Inicio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.techtokensss.Datos.Supabase.supabase
import com.example.techtokensss.ui.components.CircuitBackground
import com.example.techtokensss.ui.components.screenInsets
import com.example.techtokensss.ui.theme.APP_NAME
import com.example.techtokensss.ui.theme.APP_SLOGAN
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.delay


@Composable
fun InicioScreen(
    onCargaTerminada: (Boolean) -> Unit
) {
    // ------------------------------------------------------------------
    // LÓGICA DE SESIÓN (sin cambios)
    // ------------------------------------------------------------------
    LaunchedEffect(Unit) {

        supabase.auth.sessionStatus.collect { estado ->

            when (estado) {

                is SessionStatus.Authenticated -> {

                    delay(3000)

                    onCargaTerminada(true)

                    return@collect
                }

                is SessionStatus.NotAuthenticated -> {

                    delay(3000)

                    onCargaTerminada(false)

                    return@collect
                }

                else -> {
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // ESTADO SOLO VISUAL
    // ------------------------------------------------------------------
    var visible by remember { mutableStateOf(false) }
    var puntos by remember { mutableStateOf(0) }
    var tardando by remember { mutableStateOf(false) }

    // Dispara la animación de entrada.
    LaunchedEffect(Unit) {
        visible = true
    }

    // Puntos suspensivos animados en "Conectando...".
    LaunchedEffect(Unit) {
        while (true) {
            delay(450)
            puntos = (puntos + 1) % 4
        }
    }

    // Aviso si la conexión tarda más de lo normal.
    LaunchedEffect(Unit) {
        delay(8000)
        tardando = true
    }

    // ------------------------------------------------------------------
    // INTERFAZ
    // ------------------------------------------------------------------
    CircuitBackground(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .screenInsets(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(700)) +
                            scaleIn(
                                animationSpec = tween(700),
                                initialScale = 0.6f
                            )
                ) {
                    LogoAnimado()
                }

                Spacer(Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(
                        animationSpec = tween(600, delayMillis = 300)
                    ) + slideInVertically(
                        animationSpec = tween(600, delayMillis = 300),
                        initialOffsetY = { it / 2 }
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = APP_NAME,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = APP_SLOGAN,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(48.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(
                        animationSpec = tween(600, delayMillis = 700)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .width(160.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onBackground
                                .copy(alpha = 0.12f)
                        )

                        Spacer(Modifier.height(12.dp))

                        // Los espacios finales mantienen el ancho fijo para que el
                        // texto no "baile" al cambiar los puntos.
                        Text(
                            text = "Conectando" +
                                    ".".repeat(puntos) +
                                    " ".repeat(3 - puntos),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                AnimatedVisibility(visible = tardando) {
                    Text(
                        text = "Esto está tardando más de lo normal. " +
                                "Revisa tu conexión a internet.",
                        modifier = Modifier.padding(
                            start = 40.dp,
                            end = 40.dp,
                            top = 24.dp
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Insignia de la marca con dos ondas que se expanden (como una señal que
 * busca conexión) y una respiración muy suave. Solo Canvas y formas.
 */
@Composable
private fun LogoAnimado() {
    val colorOnda = MaterialTheme.colorScheme.primary

    val transicion = rememberInfiniteTransition(label = "logo_inicio")

    val onda1 by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing)
        ),
        label = "onda_1"
    )

    // La segunda onda arranca a mitad de ciclo, ya en marcha.
    val onda2 by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            initialStartOffset = StartOffset(
                offsetMillis = 1200,
                offsetType = StartOffsetType.FastForward
            )
        ),
        label = "onda_2"
    )

    val respiracion by transicion.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "respiracion"
    )

    Box(
        modifier = Modifier.size(150.dp),
        contentAlignment = Alignment.Center
    ) {
        // Las ondas se leen dentro del bloque de dibujo: no recomponen la pantalla.
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radioInsignia = 44.dp.toPx()
            val radioMaximo = size.minDimension / 2f
            val grosor = Stroke(width = 2.dp.toPx())

            fun dibujarOnda(progreso: Float) {
                drawCircle(
                    color = colorOnda.copy(alpha = 0.45f * (1f - progreso)),
                    radius = radioInsignia + (radioMaximo - radioInsignia) * progreso,
                    style = grosor
                )
            }

            dibujarOnda(onda1)
            dibujarOnda(onda2)
        }

        Box(
            modifier = Modifier
                .size(88.dp)
                .graphicsLayer {
                    scaleX = respiracion
                    scaleY = respiracion
                }
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.shapes.extraLarge
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.SupportAgent,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}