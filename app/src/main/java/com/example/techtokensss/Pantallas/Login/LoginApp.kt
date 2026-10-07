package com.example.techtokensss.Pantallas.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text

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

import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    onLoginClick: suspend (String, String) -> Result<Unit>,
    onRegisClick: () -> Unit
){

    var correo by remember {
        mutableStateOf("")
    }

    var contraseña by remember {
        mutableStateOf("")
    }

    var mensajeError by remember {
        mutableStateOf<String?>(null)
    }

    val scope = rememberCoroutineScope()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),

        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
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

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Bienvenido de nuevo",

                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.7f
                ),

                fontSize = 16.sp
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )


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
                        text = "Iniciar sesión",

                        color = MaterialTheme.colorScheme.onSurface,

                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Ingresa tus datos para continuar",

                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.7f
                        ),

                        fontSize = 14.sp
                    )

                    Spacer(
                        modifier = Modifier.height(28.dp)
                    )


                    OutlinedTextField(
                        value = correo,

                        onValueChange = {
                            correo = it
                            mensajeError = null
                        },

                        modifier = Modifier.fillMaxWidth(),

                        label = {
                            Text("Correo electrónico")
                        },

                        singleLine = true,

                        shape = RoundedCornerShape(16.dp),

                        colors = OutlinedTextFieldDefaults.colors(

                            focusedTextColor =
                                MaterialTheme.colorScheme.onSurface,

                            unfocusedTextColor =
                                MaterialTheme.colorScheme.onSurface,

                            focusedBorderColor =
                                MaterialTheme.colorScheme.primary,

                            unfocusedBorderColor =
                                MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.4f
                                ),

                            focusedLabelColor =
                                MaterialTheme.colorScheme.primary,

                            unfocusedLabelColor =
                                MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.7f
                                ),

                            cursorColor =
                                MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )


                    OutlinedTextField(
                        value = contraseña,

                        onValueChange = {
                            contraseña = it
                            mensajeError = null
                        },

                        modifier = Modifier.fillMaxWidth(),

                        label = {
                            Text("Contraseña")
                        },

                        singleLine = true,

                        visualTransformation =
                            PasswordVisualTransformation(),

                        shape = RoundedCornerShape(16.dp),

                        colors = OutlinedTextFieldDefaults.colors(

                            focusedTextColor =
                                MaterialTheme.colorScheme.onSurface,

                            unfocusedTextColor =
                                MaterialTheme.colorScheme.onSurface,

                            focusedBorderColor =
                                MaterialTheme.colorScheme.primary,

                            unfocusedBorderColor =
                                MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.4f
                                ),

                            focusedLabelColor =
                                MaterialTheme.colorScheme.primary,

                            unfocusedLabelColor =
                                MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.7f
                                ),

                            cursorColor =
                                MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(
                        modifier = Modifier.height(28.dp)
                    )


                    Button(
                        onClick = {

                            scope.launch {

                                val resultado = onLoginClick(
                                    correo,
                                    contraseña
                                )

                                resultado.onFailure {
                                    mensajeError =
                                        "Correo o contraseña incorrectos."
                                }
                            }
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        shape = RoundedCornerShape(16.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                MaterialTheme.colorScheme.primary
                        )
                    ) {

                        Text(
                            text = "Iniciar sesión",

                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,

                            color =
                                MaterialTheme.colorScheme.onPrimary
                        )
                    }


                    if (mensajeError != null) {

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = mensajeError!!,

                            color = MaterialTheme.colorScheme.error,

                            fontSize = 14.sp
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    Button(
                        onClick = onRegisClick,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        shape = RoundedCornerShape(16.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {

                        Text(
                            text = "Crear una cuenta",

                            fontSize = 16.sp,

                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "TechToken • Tu tecnología, bajo control",

                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.5f
                ),

                fontSize = 12.sp
            )
        }
    }
}