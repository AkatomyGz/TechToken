package com.example.techtokensss.Pantallas.Inicio

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techtokensss.Datos.Supabase.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.delay


@Composable
fun InicioScreen(
    onCargaTerminada: (Boolean) -> Unit
) {

    val transicion = rememberInfiniteTransition(
        label = "animacion_logo"
    )

    val escala by transicion.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,

        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000
            ),
            repeatMode = RepeatMode.Reverse
        ),

        label = "escala_logo"
    )


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


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),

        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "TechToken",

                color = MaterialTheme.colorScheme.onBackground,

                fontSize = 36.sp,

                fontWeight = FontWeight.Bold,

                modifier = Modifier.graphicsLayer {

                    scaleX = escala
                    scaleY = escala
                }
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            Text(
                text = "Tu tecnología, bajo control",

                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.7f
                ),

                fontSize = 16.sp
            )


            Spacer(
                modifier = Modifier.height(40.dp)
            )


            CircularProgressIndicator(

                color = MaterialTheme.colorScheme.primary,

                trackColor = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.12f
                ),

                strokeWidth = 4.dp
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            Text(
                text = "Cargando...",

                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.7f
                ),

                fontSize = 14.sp
            )
        }
    }
}