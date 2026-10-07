package com.example.techtokensss.Datos.Modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Perfil(

    @SerialName("id_perfil")
    val idPerfil: String,

    val nombres: String,

    val apellidos: String,

    @SerialName("numero_contacto")
    val numeroContacto: String,

    val rol: String
)