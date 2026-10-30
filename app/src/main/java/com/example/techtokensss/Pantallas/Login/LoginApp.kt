package com.example.techtokensss.Pantallas.Login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.techtokensss.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginClick: suspend (String, String) -> Result<Unit>,
    onRegisClick: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    CircuitBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .screenInsets()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BrandHeader(
                subtitulo = "Bienvenido de nuevo"
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            AuthCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = "Ingresa tus datos para continuar",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                TechTicketTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        mensajeError = null
                    },
                    label = "Correo electrónico",
                    enabled = !cargando,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                TechTicketPasswordField(
                    value = contraseña,
                    onValueChange = {
                        contraseña = it
                        mensajeError = null
                    },
                    label = "Contraseña",
                    enabled = !cargando
                )

                mensajeError?.let { mensaje ->
                    FeedbackBanner(
                        mensaje = mensaje,
                        tipo = TipoMensaje.Error
                    )
                }

                Button(
                    onClick = {
                        if (!cargando) {
                            scope.launch {
                                cargando = true
                                mensajeError = null

                                try {
                                    val resultado = onLoginClick(
                                        correo,
                                        contraseña
                                    )

                                    resultado.onFailure {
                                        mensajeError =
                                            "Correo o contraseña incorrectos."
                                    }
                                } catch (e: Exception) {
                                    mensajeError =
                                        "No se pudo iniciar sesión. Inténtalo nuevamente."
                                } finally {
                                    cargando = false
                                }
                            }
                        }
                    },
                    enabled = !cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = LocalContentColor.current
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = "Iniciando sesión...",
                            style = MaterialTheme.typography.labelLarge
                        )
                    } else {
                        Text(
                            text = "Iniciar sesión",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                OutlinedButton(
                    onClick = onRegisClick,
                    enabled = !cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Crear una cuenta",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            AuthFooter()
        }
    }
}