package com.example.techtokensss.Pantallas.RegistroCaso

import com.example.techtokensss.Datos.Modelos.Caso
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
    ): Result<String> {
        return try {
            // 1. Obtener el usuario autenticado.
            val usuario = supabase.auth.currentUserOrNull()
                ?: return Result.failure(
                    Exception("No hay un usuario autenticado.")
                )

            // 2. Buscar la categoría seleccionada.
            val categoria = supabase
                .from("categorias")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("nombre", nombreCategoria)
                    }
                }
                .decodeSingle<Categoria>()

            // 3. Preparar el nuevo caso.
            val nuevoCaso = NuevoCaso(
                idCreador = usuario.id,
                idCategoria = categoria.idCategoria,
                asunto = asunto,
                descripcion = descripcion
            )

            // 4. Insertar el caso y recuperar el registro creado.
            val casoCreado = supabase
                .from("casos")
                .insert(nuevoCaso) {
                    select()
                }
                .decodeSingle<Caso>()

            // 5. Devolver el identificador del caso.
            Result.success(casoCreado.idCaso)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerCategorias(): Result<List<Categoria>> {
        return try {
            val categorias = supabase
                .from("categorias")
                .select(columns = Columns.ALL)
                .decodeList<Categoria>()

            Result.success(categorias)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}