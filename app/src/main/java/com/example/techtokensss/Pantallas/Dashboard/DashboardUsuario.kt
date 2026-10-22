package com.example.techtokensss.Pantallas.Dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.techtokensss.Datos.Modelos.Caso
import com.example.techtokensss.Pantallas.Casos.CasosRepository
import com.example.techtokensss.ui.components.*
import com.example.techtokensss.ui.theme.APP_NAME

@Composable
fun DashboardUsuarioScreen(
    onMenuClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onCasosClick: () -> Unit,
    onConfiguracionClick: () -> Unit,
    onCerrarSesionClick: () -> Unit,
    onCrearCasoClick: () -> Unit
) {
    // ------------------------------------------------------------------
    // LÓGICA (sin cambios)
    // ------------------------------------------------------------------
    var menuAbierto by remember { mutableStateOf(false) }

    var casos by remember {
        mutableStateOf(emptyList<Caso>())
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

    // ------------------------------------------------------------------
    // INTERFAZ
    // ------------------------------------------------------------------
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

            // Saludo
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Hola, $nombreUsuario",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Aquí tienes un resumen de tus casos.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Acción principal
            Button(
                onClick = onCrearCasoClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Crear nuevo caso",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Últimos casos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 12.dp,
                        top = 12.dp,
                        bottom = 12.dp
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mis casos",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        TextButton(onClick = onCasosClick) {
                            Text("Ver todos")
                        }
                    }

                    when {
                        cargando -> {
                            LoadingState(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            )
                        }

                        errorCarga != null -> {
                            FeedbackBanner(
                                mensaje = errorCarga.orEmpty(),
                                tipo = TipoMensaje.Error,
                                modifier = Modifier
                                    .padding(top = 8.dp, end = 8.dp, bottom = 8.dp)
                            )
                        }

                        casos.isEmpty() -> {
                            EmptyState(
                                titulo = "Aún no tienes casos registrados.",
                                descripcion = "Crea tu primer caso para recibir soporte.",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        else -> {
                            casos.forEachIndexed { index, caso ->
                                CasoUsuario(
                                    asunto = caso.asunto,
                                    estado = caso.estado
                                )

                                if (index < casos.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(end = 8.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Menú lateral
        MenuLateralOverlay(
            visible = menuAbierto,
            onDismiss = { menuAbierto = false },
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

@Composable
private fun CasoUsuario(
    asunto: String,
    estado: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = asunto,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2
        )

        Spacer(modifier = Modifier.width(12.dp))

        StatusChip(estado = estado)
    }
}