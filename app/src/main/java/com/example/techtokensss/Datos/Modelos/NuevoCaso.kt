package com.example.techtokensss.Datos.Modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NuevoCaso(

    @SerialName("id_creador")
    val idCreador: String,

    @SerialName("id_categoria")
    val idCategoria: String,

    val asunto: String,

    val descripcion: String
)