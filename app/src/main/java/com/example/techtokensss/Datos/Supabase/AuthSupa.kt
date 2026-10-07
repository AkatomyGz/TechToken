package com.example.techtokensss.Datos.Supabase

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthSupa {

    suspend fun registrarUsuario(
        correo: String,
        contraseña: String
    ): Result<String> {

        return try {

            supabase.auth.signUpWith(Email) {
                email = correo
                password = contraseña
            }

            val usuario = supabase.auth.currentUserOrNull()

            if (usuario == null) {
                Result.failure(
                    Exception("No se pudo obtener el usuario registrado.")
                )
            } else {
                Result.success(usuario.id)
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    suspend fun iniciarSesion(
        correo: String,
        contraseña: String
    ): Result<Unit> {

        return try {

            supabase.auth.signInWith(Email) {
                email = correo
                password = contraseña
            }

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}
