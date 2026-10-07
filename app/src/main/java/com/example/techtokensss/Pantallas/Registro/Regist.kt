package com.example.techtokensss.Pantallas.Registro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.launch

@Composable
fun RegistroScreen(
    onRegistroClick: () -> Unit,
    onVolverClick: () -> Unit
) {
    var nombre by remember {
        mutableStateOf("")
    }

    var apellido by remember {
        mutableStateOf("")
    }

    var correo by remember {
        mutableStateOf("")
    }

    var numeroContacto by remember {
        mutableStateOf("")
    }

    var contraseña by remember {
        mutableStateOf("")
    }

    var confirmarContraseña by remember {
        mutableStateOf("")
    }

    var errorNombre by remember {
        mutableStateOf("")
    }

    var errorApellido by remember {
        mutableStateOf("")
    }

    var errorCorreo by remember {
        mutableStateOf("")
    }

    var errorNumeroContacto by remember {
        mutableStateOf("")
    }

    var errorContraseña by remember {
        mutableStateOf("")
    }

    var errorConfirmarContraseña by remember {
        mutableStateOf("")
    }

    var errorRegistro by remember {
        mutableStateOf("")
    }

    var cargando by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()


    val repository = remember {
        RegistroRepository()
    }

    val colorBorde = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "TechToken",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Crea tu cuenta",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Registrarse",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        errorNombre = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Nombre")
                    },
                    singleLine = true,
                    isError = errorNombre.isNotEmpty(),
                    supportingText = {
                        if (errorNombre.isNotEmpty()) {
                            Text(errorNombre)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampos(colorBorde)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = apellido,
                    onValueChange = {
                        apellido = it
                        errorApellido = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Apellido")
                    },
                    singleLine = true,
                    isError = errorApellido.isNotEmpty(),
                    supportingText = {
                        if (errorApellido.isNotEmpty()) {
                            Text(errorApellido)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampos(colorBorde)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        errorCorreo = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Correo electrónico")
                    },
                    singleLine = true,
                    isError = errorCorreo.isNotEmpty(),
                    supportingText = {
                        if (errorCorreo.isNotEmpty()) {
                            Text(errorCorreo)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampos(colorBorde)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = numeroContacto,
                    onValueChange = {
                        if (it.all { caracter -> caracter.isDigit() }) {
                            numeroContacto = it
                            errorNumeroContacto = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Número de contacto")
                    },
                    singleLine = true,
                    isError = errorNumeroContacto.isNotEmpty(),
                    supportingText = {
                        if (errorNumeroContacto.isNotEmpty()) {
                            Text(errorNumeroContacto)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampos(colorBorde)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = contraseña,
                    onValueChange = {
                        contraseña = it
                        errorContraseña = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Contraseña")
                    },
                    singleLine = true,
                    isError = errorContraseña.isNotEmpty(),
                    supportingText = {
                        if (errorContraseña.isNotEmpty()) {
                            Text(errorContraseña)
                        }
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampos(colorBorde)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmarContraseña,
                    onValueChange = {
                        confirmarContraseña = it
                        errorConfirmarContraseña = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Confirmar contraseña")
                    },
                    singleLine = true,
                    isError = errorConfirmarContraseña.isNotEmpty(),
                    supportingText = {
                        if (errorConfirmarContraseña.isNotEmpty()) {
                            Text(errorConfirmarContraseña)
                        }
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampos(colorBorde)
                )

                if (errorRegistro.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = errorRegistro,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

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
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {

                    Text(
                        text = if (cargando) {
                            "Creando cuenta..."
                        } else {
                            "Crear cuenta"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onVolverClick,
                    enabled = !cargando
                ) {
                    Text(
                        text = "Ya tengo una cuenta",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "TechToken • Tu tecnología, bajo control",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun coloresCampos(
    colorBorde: androidx.compose.ui.graphics.Color
) = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = colorBorde,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
    cursorColor = MaterialTheme.colorScheme.primary
)