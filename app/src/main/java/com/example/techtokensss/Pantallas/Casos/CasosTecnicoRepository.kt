
package com.example.techtokensss.Pantallas.CasosTecnico

import com.example.techtokensss.Datos.Modelos.Caso
import com.example.techtokensss.Datos.Supabase.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import io.github.jan.supabase.postgrest.postgrest

class CasosTecnicoRepository {

    // Obtiene únicamente los casos asignados al técnico actual.
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

    // Obtiene los casos registrados que todavía están disponibles.
    suspend fun obtenerBandeja(): Result<List<Caso>> {
        return runCatching {
            supabase.auth.currentUserOrNull()
                ?: error("No hay una sesión iniciada.")

            supabase
                .from("casos")
                .select {
                    filter {
                        eq("estado", "registrado")
                    }
                }
                .decodeList<Caso>()
                .filter { it.idEncargado == null }
        }
    }

    // Reclama un caso únicamente si continúa disponible.



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


}
