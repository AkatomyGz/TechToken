package com.example.techtokensss.Pantallas.Configs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfiguracionScreen(
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit,
    onVolverClick: () -> Unit
) {

    val fondo = MaterialTheme.colorScheme.background
    val tarjeta = MaterialTheme.colorScheme.surface
    val morado = MaterialTheme.colorScheme.primary
    val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    var notificacionesActivas by remember {
        mutableStateOf(true)
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondo)
            .padding(20.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onVolverClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Configuración",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Personaliza tu experiencia en TechToken",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )


        Text(
            text = "CUENTA",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 13.sp
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        TarjetaConfiguracion(
            titulo = "Mi cuenta",
            descripcion = "Correo y datos personales",
            icono = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = morado
                )
            },
            colorTarjeta = tarjeta
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )


        Text(
            text = "PREFERENCIAS",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 13.sp
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )


        TarjetaConfiguracion(
            titulo = "Notificaciones",
            descripcion = "Recibir alertas de la aplicación",
            icono = {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = morado
                )
            },
            colorTarjeta = tarjeta,
            contenidoExtra = {

                Switch(
                    checked = notificacionesActivas,

                    onCheckedChange = {
                        notificacionesActivas = it
                    },

                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = morado
                    )
                )
            }
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        TarjetaConfiguracion(
            titulo = "Modo oscuro",
            descripcion = "Apariencia de la aplicación",

            icono = {
                Icon(
                    Icons.Default.DarkMode,
                    contentDescription = null,
                    tint = morado
                )
            },

            colorTarjeta = tarjeta,

            contenidoExtra = {

                Switch(

                    checked = modoOscuro,

                    onCheckedChange = onModoOscuroChange,

                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = morado
                    )
                )
            }
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "INFORMACIÓN",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 13.sp
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        TarjetaConfiguracion(
            titulo = "Acerca de TechToken",
            descripcion = "Versión 1.0.0",
            icono = {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = morado
                )
            },
            colorTarjeta = tarjeta
        )
    }
}

@Composable
fun TarjetaConfiguracion(
    titulo: String,
    descripcion: String,
    icono: @Composable () -> Unit,
    colorTarjeta: Color,
    contenidoExtra: @Composable (() -> Unit)? = null
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = colorTarjeta
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            icono()

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = titulo,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = descripcion,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 12.sp
                )
            }

            contenidoExtra?.invoke()
        }
    }
}