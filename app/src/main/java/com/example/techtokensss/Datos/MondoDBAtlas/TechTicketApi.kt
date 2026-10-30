
package com.example.techtokensss.Datos.MondoDBAtlas

import android.content.Context
import android.net.Uri
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import org.json.JSONObject

class TechTokenApi {

    private val baseUrl = "http://10.77.101.243:8000"

    private val cliente = HttpClient(Android)

    suspend fun comprobarConexion(): Result<String> {
        return try {
            val respuesta = cliente.get("$baseUrl/")
            val cuerpo = respuesta.bodyAsText()

            if (respuesta.status.value in 200..299) {
                Result.success(cuerpo)
            } else {
                Result.failure(
                    Exception("HTTP ${respuesta.status.value}: $cuerpo")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerHistorial(
        idCaso: String,
        token: String
    ): Result<String> {
        return try {
            val respuesta = cliente.get("$baseUrl/historial/$idCaso") {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
            }

            val cuerpo = respuesta.bodyAsText()

            if (respuesta.status.value in 200..299) {
                Result.success(cuerpo)
            } else {
                Result.failure(
                    Exception("HTTP ${respuesta.status.value}: $cuerpo")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrarEvento(
        idCaso: String,
        token: String,
        tipoActividad: String,
        descripcion: String,
        cambios: Map<String, String> = emptyMap()
    ): Result<String> {
        return try {
            val datos = JSONObject().apply {
                put("idCaso", idCaso)
                put("tipoActividad", tipoActividad)
                put("descripcion", descripcion)
                put("actorId", "cliente")
                put("actorRol", "cliente")
                put("cambios", JSONObject(cambios))
            }

            val respuesta = cliente.post("$baseUrl/historial") {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
                contentType(ContentType.Application.Json)
                setBody(datos.toString())
            }

            val cuerpo = respuesta.bodyAsText()

            if (respuesta.status.value in 200..299) {
                Result.success(cuerpo)
            } else {
                Result.failure(
                    Exception("HTTP ${respuesta.status.value}: $cuerpo")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun subirEvidencia(
        context: Context,
        idCaso: String,
        token: String,
        uri: Uri,
        nombreArchivo: String
    ): Result<String> {
        return try {
            val tipoMime = context.contentResolver.getType(uri)
                ?: "application/octet-stream"

            val bytes = context.contentResolver
                .openInputStream(uri)
                ?.use { it.readBytes() }
                ?: throw Exception(
                    "No se pudo leer el archivo: $nombreArchivo"
                )

            if (bytes.isEmpty()) {
                throw Exception("El archivo está vacío: $nombreArchivo")
            }

            if (bytes.size > 10 * 1024 * 1024) {
                throw Exception(
                    "El archivo supera el límite de 10 MB: $nombreArchivo"
                )
            }

            val nombreSeguro = nombreArchivo
                .replace("\"", "")
                .replace("\r", "")
                .replace("\n", "")

            val respuesta = cliente.submitFormWithBinaryData(
                url = "$baseUrl/casos/$idCaso/evidencias",
                formData = formData {
                    append(
                        key = "archivo",
                        value = bytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, tipoMime)
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"archivo\"; filename=\"$nombreSeguro\""
                            )
                        }
                    )
                }
            ) {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
            }

            val cuerpo = respuesta.bodyAsText()

            if (respuesta.status.value in 200..299) {
                Result.success(cuerpo)
            } else {
                Result.failure(
                    Exception(
                        "No se pudo subir $nombreArchivo. " +
                                "HTTP ${respuesta.status.value}: $cuerpo"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Descargar los bytes de una evidencia desde FastAPI.
    suspend fun obtenerEvidencia(
        idCaso: String,
        idArchivo: String,
        token: String
    ): Result<ByteArray> {
        return try {
            val respuesta = cliente.get(
                "$baseUrl/casos/$idCaso/evidencias/$idArchivo"
            ) {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
            }

            if (respuesta.status.value in 200..299) {
                Result.success(respuesta.bodyAsBytes())
            } else {
                val cuerpo = respuesta.bodyAsText()
                Result.failure(
                    Exception(
                        "No se pudo descargar la evidencia. " +
                                "HTTP ${respuesta.status.value}: $cuerpo"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cerrar() {
        cliente.close()
    }
}
