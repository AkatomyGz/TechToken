package com.example.techtokensss.Pantallas.Casos

import com.example.techtokensss.Datos.Modelos.Caso
import com.example.techtokensss.Datos.Supabase.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import com.example.techtokensss.Datos.Modelos.Categoria
import android.util.Log

class CasosRepository {


    suspend fun obtenerMisCasos(): Result<List<Caso>> {
        return runCatching {
            val usuario = supabase.auth.currentUserOrNull()
                ?: error("No hay una sesión iniciada.")

            Log.d("CASOS_DEBUG", "UUID usuario: ${usuario.id}")

            val resultado = supabase
                .from("casos")
                .select {
                    filter {
                        eq("id_creador", usuario.id)
                    }
                }
                .decodeList<Caso>()

            Log.d("CASOS_DEBUG", "Casos recibidos: ${resultado.size}")

            resultado
        }
    }


    suspend fun obtenerCategorias(): Result<List<Categoria>> {
        return runCatching {
            supabase
                .from("categorias")
                .select()
                .decodeList<Categoria>()
        }
    }


    suspend fun obtenerCasoPorId(idCaso: String): Result<Caso?> {
        return runCatching {
            supabase
                .from("casos")
                .select {
                    filter {
                        eq("id_caso", idCaso)
                    }
                }
                .decodeList<Caso>()
                .firstOrNull()
        }
    }


    suspend fun obtenerEncargado(idEncargado: String): Result<String?> {
        return runCatching {
            val perfiles = supabase
                .from("perfiles")
                .select {
                    filter {
                        eq("id_perfil", idEncargado)
                    }
                }
                .decodeList<com.example.techtokensss.Datos.Modelos.Perfil>()

            perfiles.firstOrNull()?.let {
                "${it.nombres} ${it.apellidos}"
            }
        }
    }


}