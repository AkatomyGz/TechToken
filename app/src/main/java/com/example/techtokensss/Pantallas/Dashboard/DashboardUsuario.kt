package com.example.techtokensss.Pantallas.Dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techtokensss.Pantallas.Casos.CasosRepository

@Composable
fun DashboardUsuarioScreen(
    onMenuClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onCasosClick: () -> Unit,
    onConfiguracionClick: () -> Unit,
    onCerrarSesionClick: () -> Unit,
    onCrearCasoClick: () -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }

    var casos by remember {
        mutableStateOf(emptyList<com.example.techtokensss.Datos.Modelos.Caso>())
    }

    var cargando by remember { mutableStateOf(true) }
    var errorCarga by remember { mutableStateOf<String?>(null) }

    val repository = remember { CasosRepository() }

    var nombreUsuario by remember {
        mutableStateOf("Usuario")
    }

    LaunchedEffect(Unit) {
        cargando = true
        errorCarga = null

        repository.obtenerUltimosCasos()
            .onSuccess { resultado ->
                casos = resultado
            }
            .onFailure { error ->
                errorCarga = error.message
                    ?: "No fue posible cargar tus casos."
            }

        repository.obtenerNombreUsuario()
            .onSuccess { nombre ->
                nombreUsuario = nombre
            }

        cargando = false
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
                        contentDescription = "Abrir menú",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "TechToken",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Saludo
            Text(
                text = "Hola, $nombreUsuario ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Aquí tienes un resumen de tus casos.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Resumen de los últimos tres casos
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
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    when {
                        cargando -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        errorCarga != null -> {
                            Text(
                                text = errorCarga!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                        }

                        casos.isEmpty() -> {
                            Text(
                                text = "Aún no tienes casos registrados.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }

                        else -> {
                            casos.forEachIndexed { index, caso ->
                                CasoUsuario(
                                    asunto = caso.asunto,
                                    estado = formatearEstado(caso.estado)
                                )

                                if (index < casos.lastIndex) {
                                    Divider()
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para crear un caso
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
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
                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f)
                    )
                    .clickable {
                        menuAbierto = false
                    }
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
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = estado,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun formatearEstado(estado: String): String {
    return when (estado) {
        "registrado" -> "Registrado"
        "en_proceso" -> "En proceso"
        "solucionado" -> "Solucionado"
        "cerrado" -> "Cerrado"
        else -> estado
    }
}