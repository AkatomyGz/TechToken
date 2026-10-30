
package com.example.techtokensss.Pantallas.CasosTecnico

import com.example.techtokensss.Datos.Modelos.Caso
import com.example.techtokensss.Datos.Supabase.supabase
import com.example.techtokensss.Pantallas.Dashboard.EstadisticasCasos

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class CasosTecnicoRepository {

    suspend fun obtenerMisCasos(): Result<List<Caso>> {
        return runCatching {
            val usuario = supabase.auth.currentUserOrNull()
                ?: error("No hay una sesión iniciada.")

            supabase
                .from("casos")
                .select {
                    filter {
                        eq("id_encargado", usuario.id)
                    }
                }
                .decodeList<Caso>()
        }
    }

    suspend fun obtenerBandeja(): Result<List<Caso>> {
        return runCatching {
            val usuario = supabase.auth.currentUserOrNull()
                ?: error("No hay una sesión iniciada.")

            val casosRegistrados = supabase
                .from("casos")
                .select {
                    filter {
                        eq("estado", "registrado")
                    }
                }
                .decodeList<Caso>()

            casosRegistrados.filter { caso ->
                caso.idEncargado == null
            }
        }
    }

    suspend fun reclamarCaso(idCaso: String): Result<Unit> {
        return runCatching {
            supabase.auth.currentUserOrNull()
                ?: error("No hay una sesión iniciada.")

            val respuesta = supabase.postgrest
                .rpc(
                    function = "reclamar_caso",
                    parameters = buildJsonObject {
                        put("p_id_caso", idCaso)
                    }
                )
                .decodeAs<Boolean>()

            check(respuesta) {
                "No se pudo reclamar el caso. Puede que otro técnico ya lo haya reclamado."
            }
        }
    }

    suspend fun actualizarCaso(
        idCaso: String,
        idCategoria: String,
        estado: String
    ): Result<Unit> {
        return runCatching {
            val usuario = supabase.auth.currentUserOrNull()
                ?: error("No hay una sesión iniciada.")

            supabase
                .from("casos")
                .update({
                    set("id_categoria", idCategoria)
                    set("estado", estado)
                }) {
                    filter {
                        eq("id_caso", idCaso)
                        eq("id_encargado", usuario.id)
                    }
                }
        }
    }

    suspend fun obtenerEstadisticas(): Result<EstadisticasCasos> {
        return runCatching {
            val casos = obtenerMisCasos().getOrThrow()

            EstadisticasCasos(
                registrados = casos.count {
                    it.estado == "registrado"
                },
                enProceso = casos.count {
                    it.estado == "en_proceso"
                },
                solucionados = casos.count {
                    it.estado == "solucionado"
                },
                cerrados = casos.count {
                    it.estado == "cerrado"
                }
            )
        }
    }
}
