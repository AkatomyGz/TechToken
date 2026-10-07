package com.example.techtokensss.Datos.Supabase

import com.example.techtokensss.Datos.Modelos.Perfil
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class PerfilRepository {

    suspend fun obtenerPerfilActual(): Result<Perfil> {

        return try {

            val usuario = supabase.auth.currentUserOrNull()

            if (usuario == null) {
                return Result.failure(
                    Exception("No hay un usuario autenticado.")
                )
            }

            val perfil = supabase
                .from("perfiles")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("id_perfil", usuario.id)
                    }
                }
                .decodeSingle<Perfil>()

            Result.success(perfil)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}

