package com.example.techtokensss.Pantallas.Dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MenuLateral(
    onPerfilClick: () -> Unit,
    onCasosClick: () -> Unit,
    onConfiguracionClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.78f)
            .background(MaterialTheme.colorScheme.background)

            .padding(24.dp),

        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "TechToken",

            color = MaterialTheme.colorScheme.onBackground,

            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Menú principal",
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.7f
            ),

            fontSize = 14.sp
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Button(
            onClick = onPerfilClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Text(
                text = "👤   Perfil",
                color = MaterialTheme.colorScheme.onSurface,

                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onCasosClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Text(
                text = "📁   Casos",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onConfiguracionClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Text(
                text = "⚙   Configuración",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onCerrarSesionClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF241827)
            )
        ) {

            Text(
                text = "🚪   Cerrar sesión",
                color = Color.White,

                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}