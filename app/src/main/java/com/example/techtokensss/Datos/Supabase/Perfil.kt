package com.example.techtokensss.Datos.Supabase

import kotlinx.serialization.Serializable

@Serializable
data class Perfil(
    val id_perfil: String,
    val nombres: String,
    val apellidos: String,
    val numero_contacto: String
)

