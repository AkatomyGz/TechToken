package com.example.techtokensss.Pantallas.Perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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

import com.example.techtokensss.Datos.Supabase.PerfilRepository
import com.example.techtokensss.Datos.Supabase.supabase

import io.github.jan.supabase.auth.auth


@Composable
fun UserScreen(
    onVolverClick: () -> Unit
) {

    val perfilRepository = remember {
        PerfilRepository()
    }

    var nombres by remember {
        mutableStateOf("")
    }

    var apellidos by remember {
        mutableStateOf("")
    }

    var numeroContacto by remember {
        mutableStateOf("")
    }

    var correo by remember {
        mutableStateOf("")
    }

    var fechaRegistro by remember {
        mutableStateOf("")
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }


    // --------------------------------------------------
    // CARGAR INFORMACIÓN DEL USUARIO
    // --------------------------------------------------

    LaunchedEffect(Unit) {

        val resultado = perfilRepository.obtenerPerfilActual()

        if (resultado.isSuccess) {

            val perfil = resultado.getOrNull()

            if (perfil != null) {

                nombres = perfil.nombres
                apellidos = perfil.apellidos
                numeroContacto = perfil.numeroContacto
                fechaRegistro = perfil.fechaRegistro.substringBefore("T")
            }

            val usuario = supabase.auth.currentUserOrNull()

            correo = usuario?.email ?: ""

        } else {

            error = resultado.exceptionOrNull()?.message
                ?: "No se pudo cargar el perfil."
        }

        cargando = false
    }


    // --------------------------------------------------
    // CARGANDO
    // --------------------------------------------------

    if (cargando) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            CircularProgressIndicator()

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Cargando perfil...",
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        return
    }


    // --------------------------------------------------
    // ERROR
    // --------------------------------------------------

    if (error != null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = error ?: "Error desconocido",
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            IconButton(
                onClick = onVolverClick
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        return
    }


    // --------------------------------------------------
    // INFORMACIÓN DEL USUARIO
    // --------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                text = "Mi perfil",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        // Icono de perfil
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Perfil",
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )


        Spacer(
            modifier = Modifier.height(25.dp)
        )


        // Nombres
        Text(
            text = "Nombres",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedTextField(
            value = nombres,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            readOnly = true
        )


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // Apellidos
        Text(
            text = "Apellidos",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedTextField(
            value = apellidos,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            readOnly = true
        )


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // Número de contacto
        Text(
            text = "Número de contacto",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedTextField(
            value = numeroContacto,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            readOnly = true
        )


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // Correo
        Text(
            text = "Correo electrónico",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedTextField(
            value = correo,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            readOnly = true
        )


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // Fecha de registro
        Text(
            text = "Fecha de registro",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedTextField(
            value = fechaRegistro,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            readOnly = true
        )
    }
}