package com.example.techtokensss.Pantallas.RegistroUser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.techtokensss.ui.components.*
import kotlinx.coroutines.launch

@Composable
fun RegistroScreen(
    onRegistroClick: () -> Unit,
    onVolverClick: () -> Unit
) {
    // ------------------------------------------------------------------
    // LÓGICA Y VALIDACIONES (sin cambios)
    // ------------------------------------------------------------------
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var numeroContacto by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var confirmarContraseña by remember { mutableStateOf("") }

    var errorNombre by remember { mutableStateOf("") }
    var errorApellido by remember { mutableStateOf("") }
    var errorCorreo by remember { mutableStateOf("") }
    var errorNumeroContacto by remember { mutableStateOf("") }
    var errorContraseña by remember { mutableStateOf("") }
    var errorConfirmarContraseña by remember { mutableStateOf("") }
    var errorRegistro by remember { mutableStateOf("") }

    var cargando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val repository = remember {
        RegistroRepository()
    }

    fun validarFormulario(): Boolean {

        var formularioValido = true

        errorNombre = ""
        errorApellido = ""
        errorCorreo = ""
        errorNumeroContacto = ""
        errorContraseña = ""
        errorConfirmarContraseña = ""
        errorRegistro = ""

        if (nombre.isBlank()) {
            errorNombre = "Ingresa tu nombre"
            formularioValido = false
        }

        if (apellido.isBlank()) {
            errorApellido = "Ingresa tu apellido"
            formularioValido = false
        }

        if (correo.isBlank()) {
            errorCorreo = "Ingresa tu correo"
            formularioValido = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            errorCorreo = "Ingresa un correo válido"
            formularioValido = false
        }

        if (numeroContacto.isBlank()) {
            errorNumeroContacto = "Ingresa tu número de contacto"
            formularioValido = false
        }

        if (contraseña.isBlank()) {
            errorContraseña = "Ingresa una contraseña"
            formularioValido = false
        } else if (contraseña.length < 8) {
            errorContraseña = "Mínimo 8 caracteres"
            formularioValido = false
        } else if (!contraseña.any { it.isUpperCase() }) {
            errorContraseña = "Debe contener al menos una mayúscula"
            formularioValido = false
        } else if (!contraseña.any { it.isLowerCase() }) {
            errorContraseña = "Debe contener al menos una minúscula"
            formularioValido = false
        } else if (!contraseña.any { it.isDigit() }) {
            errorContraseña = "Debe contener al menos un número"
            formularioValido = false
        } else if (!contraseña.any { !it.isLetterOrDigit() }) {
            errorContraseña = "Debe contener al menos un carácter especial"
            formularioValido = false
        }

        if (confirmarContraseña.isBlank()) {
            errorConfirmarContraseña = "Confirma tu contraseña"
            formularioValido = false
        } else if (contraseña != confirmarContraseña) {
            errorConfirmarContraseña = "Las contraseñas no coinciden"
            formularioValido = false
        }

        return formularioValido
    }

    // ------------------------------------------------------------------
    // INTERFAZ
    // ------------------------------------------------------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .screenInsets()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BrandHeader(subtitulo = "Crea tu cuenta")

        Spacer(Modifier.height(28.dp))

        AuthCard {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Registrarse",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Completa tus datos para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TechTicketTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    errorNombre = ""
                },
                label = "Nombre",
                errorText = errorNombre.takeIf { it.isNotEmpty() },
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            TechTicketTextField(
                value = apellido,
                onValueChange = {
                    apellido = it
                    errorApellido = ""
                },
                label = "Apellido",
                errorText = errorApellido.takeIf { it.isNotEmpty() },
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            TechTicketTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    errorCorreo = ""
                },
                label = "Correo electrónico",
                errorText = errorCorreo.takeIf { it.isNotEmpty() },
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            TechTicketTextField(
                value = numeroContacto,
                onValueChange = {
                    if (it.all { caracter -> caracter.isDigit() }) {
                        numeroContacto = it
                        errorNumeroContacto = ""
                    }
                },
                label = "Número de contacto",
                errorText = errorNumeroContacto.takeIf { it.isNotEmpty() },
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TechTicketPasswordField(
                    value = contraseña,
                    onValueChange = {
                        contraseña = it
                        errorContraseña = ""
                    },
                    label = "Contraseña",
                    errorText = errorContraseña.takeIf { it.isNotEmpty() },
                    enabled = !cargando
                )

                if (errorContraseña.isEmpty()) {
                    Text(
                        text = "Mínimo 8 caracteres, con mayúscula, minúscula, número y carácter especial.",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            TechTicketPasswordField(
                value = confirmarContraseña,
                onValueChange = {
                    confirmarContraseña = it
                    errorConfirmarContraseña = ""
                },
                label = "Confirmar contraseña",
                errorText = errorConfirmarContraseña.takeIf { it.isNotEmpty() },
                enabled = !cargando
            )

            if (errorRegistro.isNotEmpty()) {
                FeedbackBanner(mensaje = errorRegistro, tipo = TipoMensaje.Error)
            }

            Button(
                onClick = {

                    if (!cargando && validarFormulario()) {

                        cargando = true
                        errorRegistro = ""


                        scope.launch {

                            val resultado = repository.registrar(
                                nombres = nombre,
                                apellidos = apellido,
                                correo = correo,
                                numeroContacto = numeroContacto,
                                contraseña = contraseña
                            )

                            cargando = false

                            resultado.onSuccess {
                                onRegistroClick()
                            }

                            resultado.onFailure { error ->
                                errorRegistro =
                                    error.message
                                        ?: "No se pudo crear la cuenta"
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
                    Spacer(Modifier.width(8.dp))
                }

                Text(
                    text = if (cargando) {
                        "Creando cuenta..."
                    } else {
                        "Crear cuenta"
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }

            TextButton(
                onClick = onVolverClick,
                enabled = !cargando,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Ya tengo una cuenta")
            }
        }

        Spacer(Modifier.height(24.dp))

        AuthFooter()
    }
}