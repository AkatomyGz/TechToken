package com.example.techtokensss.Pantallas.RegistroUser

import com.example.techtokensss.Datos.Modelos.Perfil
import com.example.techtokensss.Datos.Supabase.AuthSupa
import com.example.techtokensss.Datos.Supabase.supabase
import io.github.jan.supabase.postgrest.from

class RegistroRepository {

    private val authSupa = AuthSupa()

    suspend fun registrar(
        nombres: String,
        apellidos: String,
        correo: String,
        numeroContacto: String,
        contraseña: String
    ): Result<Unit> {

        return try {


            val resultadoAuth = authSupa.registrarUsuario(
                correo = correo,
                contraseña = contraseña
            )


            val idUsuario = resultadoAuth.getOrElse {
                return Result.failure(it)
            }


            val perfil = Perfil(
                idPerfil = idUsuario,
                nombres = nombres,
                apellidos = apellidos,
                numeroContacto = numeroContacto,
                rol = "usuario"
            )


            supabase
                .from("perfiles")
                .insert(perfil)

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}

