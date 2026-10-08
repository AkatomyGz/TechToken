package com.example.techtokensss.Datos.Modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Categoria(

    @SerialName("id_categoria")
    val idCategoria: String,

    val nombre: String,

    val descripcion: String? = null
)
