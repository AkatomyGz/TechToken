package com.example.techtokensss.Pantallas.RegistroCaso

import com.example.techtokensss.Datos.Modelos.Categoria
import com.example.techtokensss.Datos.Modelos.NuevoCaso
import com.example.techtokensss.Datos.Supabase.supabase

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class CasoRepository {

    suspend fun crearCaso(
        asunto: String,
        descripcion: String,
        nombreCategoria: String
    ): Result<Unit> {

        return try {

            // Obtener el usuario actualmente autenticado

            val usuario = supabase.auth.currentUserOrNull()

            if (usuario == null) {

                return Result.failure(
                    Exception("No hay un usuario autenticado.")
                )
            }


            // Buscar la categoría seleccionada

            val categoria = supabase
                .from("categorias")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("nombre", nombreCategoria)
                    }
                }
                .decodeSingle<Categoria>()


            // Preparar los datos del nuevo caso

            val nuevoCaso = NuevoCaso(
                idCreador = usuario.id,
                idCategoria = categoria.idCategoria,
                asunto = asunto,
                descripcion = descripcion
            )


            // Insertar el caso en Supabase

            supabase
                .from("casos")
                .insert(nuevoCaso)


            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}