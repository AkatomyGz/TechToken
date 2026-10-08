package com.example.techtokensss.Pantallas.RegistroCaso

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearCasoScreen(
    onVolverClick: () -> Unit,
    onCrearCasoClick: (
        asunto: String,
        descripcion: String,
        categoria: String
    ) -> Unit
) {

    var asunto by remember {
        mutableStateOf("")
    }

    var descripcion by remember {
        mutableStateOf("")
    }

    var categoriaSeleccionada by remember {
        mutableStateOf("")
    }

    var menuAbierto by remember {
        mutableStateOf(false)
    }


    val categorias = listOf(
        "Software" to
                "Problemas relacionados con programas instalados o herramientas digitales.",

        "Hardware" to
                "Problemas relacionados con componentes físicos del equipo o dispositivos.",

        "Cuenta" to
                "Problemas relacionados con cuentas, contraseñas, permisos o acceso de usuarios.",

        "Aplicación" to
                "Problemas relacionados con el funcionamiento o uso de una aplicación específica.",

        "Conectividad" to
                "Problemas relacionados con Internet, Wi-Fi, redes o conexión entre dispositivos.",

        "Otro" to
                "Problemas que no corresponden a ninguna de las categorías anteriores."
    )


    val descripcionCategoria = categorias
        .find {
            it.first == categoriaSeleccionada
        }
        ?.second


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        // Barra superior

        Row(
            modifier = Modifier.fillMaxWidth(),
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
                text = "Crear caso",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        // Título

        Text(
            text = "Nuevo caso",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Describe el problema que estás teniendo.",
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.7f
            )
        )


        Spacer(
            modifier = Modifier.height(28.dp)
        )


        // Asunto

        Text(
            text = "Asunto",
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedTextField(
            value = asunto,
            onValueChange = {
                asunto = it
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text("Ej. Mi computador no enciende")
            }
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // Descripción

        Text(
            text = "Descripción",
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = {
                descripcion = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = {
                Text(
                    "Describe detalladamente el problema..."
                )
            }
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // Categoría

        Text(
            text = "Categoría",
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        ExposedDropdownMenuBox(
            expanded = menuAbierto,
            onExpandedChange = {
                menuAbierto = !menuAbierto
            }
        ) {

            OutlinedTextField(
                value = categoriaSeleccionada,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                placeholder = {
                    Text("Selecciona una categoría")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuAbierto
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = {
                    menuAbierto = false
                }
            ) {

                categorias.forEach { categoria ->

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = categoria.first
                            )
                        },
                        onClick = {

                            categoriaSeleccionada = categoria.first

                            menuAbierto = false
                        }
                    )
                }
            }
        }


        // Descripción de la categoría

        if (descripcionCategoria != null) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = descripcionCategoria,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.7f
                ),
                fontSize = 13.sp
            )
        }


        Spacer(
            modifier = Modifier.height(28.dp)
        )


        // Evidencias

        Text(
            text = "Evidencias",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Puedes adjuntar imágenes o documentos relacionados con el caso.",
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.7f
            ),
            fontSize = 13.sp
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Imagen

            OutlinedButton(
                onClick = {
                    // Próximamente
                },
                enabled = false,
                modifier = Modifier.weight(1f)
            ) {

                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Imagen"
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Imagen"
                )
            }


            // Documento

            OutlinedButton(
                onClick = {
                    // Próximamente
                },
                enabled = false,
                modifier = Modifier.weight(1f)
            ) {

                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Documento"
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Documento"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Funcionalidad disponible próximamente.",
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.5f
            ),
            fontSize = 12.sp
        )


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        // Botón crear

        Button(
            onClick = {

                onCrearCasoClick(
                    asunto,
                    descripcion,
                    categoriaSeleccionada
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = asunto.isNotBlank()
                    && descripcion.isNotBlank()
                    && categoriaSeleccionada.isNotBlank()
        ) {

            Text(
                text = "Crear caso"
            )
        }
    }
}