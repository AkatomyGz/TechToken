package com.example.techtokensss.Pantallas.Dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardUsuarioScreen(
    onMenuClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onCasosClick: () -> Unit,
    onConfiguracionClick: () -> Unit,
    onCerrarSesionClick: () -> Unit,
    onCrearCasoClick: () -> Unit
) {

    var menuAbierto by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // Barra superior
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        menuAbierto = true
                        onMenuClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Abrir menú"
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "TechToken",
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Saludo
            Text(
                text = "Hola, Juan 👋",
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Aquí tienes un resumen de tus casos.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Tarjeta de casos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Mis casos",
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CasoUsuario(
                        asunto = "Problema con mi computador",
                        estado = "En proceso"
                    )

                    Divider()

                    CasoUsuario(
                        asunto = "No puedo conectarme al WiFi",
                        estado = "Solucionado"
                    )

                    Divider()

                    CasoUsuario(
                        asunto = "Error en Microsoft Office",
                        estado = "Registrado"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón crear caso
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onCrearCasoClick()
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "+ Crear nuevo caso",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Menú lateral
        if (menuAbierto) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.background.copy(
                            alpha = 0.5f
                        )
                    )
                    .clickable {
                        menuAbierto = false
                    }
            )

            Card(
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(
                    topEnd = 20.dp,
                    bottomEnd = 20.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(20.dp)
                ) {

                    Text(
                        text = "TechToken",
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Inicio",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                menuAbierto = false
                            }
                            .padding(vertical = 14.dp)
                    )

                    Text(
                        text = "Perfil",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                menuAbierto = false
                                onPerfilClick()
                            }
                            .padding(vertical = 14.dp)
                    )

                    Text(
                        text = "Mis casos",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                menuAbierto = false
                                onCasosClick()
                            }
                            .padding(vertical = 14.dp)
                    )

                    Text(
                        text = "Configuración",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                menuAbierto = false
                                onConfiguracionClick()
                            }
                            .padding(vertical = 14.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Divider()

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Cerrar sesión",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                menuAbierto = false
                                onCerrarSesionClick()
                            }
                            .padding(vertical = 14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CasoUsuario(
    asunto: String,
    estado: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = asunto,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = estado,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
