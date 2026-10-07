
package com.example.techtokensss.Pantallas.Dashboard

import androidx.compose.material3.MaterialTheme

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
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DashboardScreen(
    onMenuClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onCasosClick: () -> Unit,
    onConfiguracionClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
) {


    var menuAbierto by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(
                    onClick = {
                        menuAbierto = true
                    }
                ) {
                    Text("☰")
                }


                Text(

                    text = "TechToken",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        onPerfilClick()
                    }
                ) {
                    Text("👤")
                }
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(

                    text = "Hola 👋",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold

                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(

                    text = "Resumen de tus casos",
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.7f
                    ),

                    fontSize = 16.sp
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp),

                shape = RoundedCornerShape(24.dp),

                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "GRÁFICO",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "Estado de casos",

                color = MaterialTheme.colorScheme.onBackground,

                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                TarjetaEstado(
                    titulo = "Registrados",
                    cantidad = "12",
                    modifier = Modifier.weight(1f)
                )

                TarjetaEstado(
                    titulo = "En proceso",
                    cantidad = "5",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                TarjetaEstado(
                    titulo = "Cerrados",
                    cantidad = "3",
                    modifier = Modifier.weight(1f)
                )

                TarjetaEstado(
                    titulo = "Solucionados",
                    cantidad = "7",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if (menuAbierto) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000))
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
fun TarjetaEstado(
    titulo: String,
    cantidad: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.height(110.dp),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.7f
                ),

                fontSize = 14.sp
            )

            Text(
                text = cantidad,
                color = MaterialTheme.colorScheme.onSurface,

                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
