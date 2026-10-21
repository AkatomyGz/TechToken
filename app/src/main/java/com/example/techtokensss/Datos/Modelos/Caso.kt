package com.example.techtokensss.Datos.Modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Caso(
    @SerialName("id_caso")
    val idCaso: String,

    @SerialName("id_creador")
    val idCreador: String,

    @SerialName("id_categoria")
    val idCategoria: String,

    val asunto: String,
    val descripcion: String,
    val estado: String,

    @SerialName("fecha_creacion")
    val fechaCreacion: String,

    @SerialName("id_encargado")
    val idEncargado: String? = null,

    @SerialName("fecha_cierre")
    val fechaCierre: String? = null
)