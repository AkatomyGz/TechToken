package com.example.techtokensss.Datos.Preferencias

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(
    name = "preferencias_app"
)

val MODO_OSCURO = booleanPreferencesKey("modo_oscuro")