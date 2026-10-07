package com.example.techtokensss.Navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.techtokensss.Datos.Supabase.AuthSupa

import com.example.techtokensss.Pantallas.Configs.ConfiguracionScreen
import com.example.techtokensss.Pantallas.Dashboard.DashboardScreen
import com.example.techtokensss.Pantallas.Inicio.InicioScreen
import com.example.techtokensss.Pantallas.Login.LoginScreen
import com.example.techtokensss.Pantallas.Perfil.PerfilScreen
import com.example.techtokensss.Pantallas.Registro.RegistroScreen


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


        composable("dashboard") {

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


        composable("configuracion") {

            ConfiguracionScreen(

                modoOscuro = modoOscuro,

                onModoOscuroChange = onModoOscuroChange,

                onVolverClick = {

                    navController.popBackStack()
                }
            )
        }


        composable("perfil") {

            PerfilScreen(

                onVolverClick = {

                    navController.popBackStack()
                }
            )
        }
    }
}