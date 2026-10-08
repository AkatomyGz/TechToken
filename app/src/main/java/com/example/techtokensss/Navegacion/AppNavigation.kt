package com.example.techtokensss.Navegacion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.techtokensss.Datos.Supabase.AuthSupa
import com.example.techtokensss.Datos.Supabase.PerfilRepository

import com.example.techtokensss.Pantallas.Configs.ConfiguracionScreen
import com.example.techtokensss.Pantallas.Dashboard.DashboardScreen
import com.example.techtokensss.Pantallas.Dashboard.DashboardUsuarioScreen
import com.example.techtokensss.Pantallas.Inicio.InicioScreen
import com.example.techtokensss.Pantallas.Login.LoginScreen
import com.example.techtokensss.Pantallas.Perfil.UserScreen
import com.example.techtokensss.Pantallas.RegistroUser.RegistroScreen
import com.example.techtokensss.Pantallas.RegistroCaso.CrearCasoScreen


@Composable
fun AppNavigation(
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {

        // --------------------------------------------------
        // INICIO
        // --------------------------------------------------

        composable("inicio") {

            InicioScreen(
                onCargaTerminada = { sesionActiva ->

                    if (sesionActiva) {

                        navController.navigate("dashboard") {

                            popUpTo("inicio") {
                                inclusive = true
                            }
                        }

                    } else {

                        navController.navigate("login") {

                            popUpTo("inicio") {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }


        // --------------------------------------------------
        // LOGIN
        // --------------------------------------------------

        composable("login") {

            val authSupa = remember {
                AuthSupa()
            }

            LoginScreen(

                onLoginClick = { correo, contraseña ->

                    val resultado = authSupa.iniciarSesion(
                        correo = correo,
                        contraseña = contraseña
                    )

                    if (resultado.isSuccess) {

                        navController.navigate("dashboard") {

                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    }

                    resultado
                },

                onRegisClick = {

                    navController.navigate("registro")
                }
            )
        }


        // --------------------------------------------------
        // REGISTRO
        // --------------------------------------------------

        composable("registro") {

            RegistroScreen(

                onVolverClick = {
                    navController.popBackStack()
                },

                onRegistroClick = {

                    navController.navigate("login") {

                        popUpTo("registro") {
                            inclusive = true
                        }
                    }
                }
            )
        }


        // --------------------------------------------------
        // DASHBOARD
        // --------------------------------------------------

        composable("dashboard") {

            val perfilRepository = remember {
                PerfilRepository()
            }

            var rol by remember {
                mutableStateOf<String?>(null)
            }

            var error by remember {
                mutableStateOf<String?>(null)
            }

            LaunchedEffect(Unit) {

                val resultado = perfilRepository.obtenerPerfilActual()

                if (resultado.isSuccess) {

                    rol = resultado.getOrNull()?.rol

                } else {

                    error = resultado.exceptionOrNull()?.message
                        ?: "No se pudo obtener el perfil."
                }
            }


            // Mientras buscamos el rol
            if (rol == null && error == null) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    CircularProgressIndicator()

                    Text(
                        text = "Cargando perfil..."
                    )
                }
            }


            // Si ocurrió un error
            if (error != null) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = error ?: "Error desconocido"
                    )
                }
            }


            // Cuando ya conocemos el rol
            when (rol) {

                "usuario" -> {

                    DashboardUsuarioScreen(

                        onMenuClick = {
                        },

                        onPerfilClick = {

                            navController.navigate("perfil")
                        },

                        onCasosClick = {

                            navController.navigate("casos")
                        },

                        onConfiguracionClick = {

                            navController.navigate("configuracion")
                        },

                        onCerrarSesionClick = {

                            navController.navigate("login")
                        },

                        onCrearCasoClick = {

                            navController.navigate(route= "RegisCaso")

                        }
                    )
                }


                "tecnico" -> {

                    DashboardScreen(

                        onMenuClick = {
                        },

                        onPerfilClick = {

                            navController.navigate("perfil")
                        },

                        onCasosClick = {

                            navController.navigate("casos")
                        },

                        onConfiguracionClick = {

                            navController.navigate("configuracion")
                        },

                        onCerrarSesionClick = {

                            navController.navigate("login")
                        }
                    )
                }
            }
        }


        // --------------------------------------------------
        // CONFIGURACIÓN
        // --------------------------------------------------

        composable("configuracion") {

            ConfiguracionScreen(

                modoOscuro = modoOscuro,

                onModoOscuroChange = onModoOscuroChange,

                onVolverClick = {

                    navController.popBackStack()
                }
            )
        }


        // --------------------------------------------------
        // PERFIL
        // --------------------------------------------------

        composable("perfil") {

            UserScreen(

                onVolverClick = {

                    navController.popBackStack()
                }
            )
        }

        composable(route = "RegisCaso") {

            CrearCasoScreen(

                onVolverClick = {
                    navController.popBackStack()
                },

                onCrearCasoClick = { asunto, descripcion, categoria ->

                    navController.popBackStack()

                }
            )
        }
    }
}