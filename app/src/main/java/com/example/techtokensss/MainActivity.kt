package com.example.techtokensss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.example.techtokensss.Datos.Preferencias.MODO_OSCURO
import com.example.techtokensss.Datos.Preferencias.dataStore
import com.example.techtokensss.Navegacion.AppNavigation
import com.example.techtokensss.ui.theme.TechTokensssTheme
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            val modoOscuro by dataStore.data
                .catch {
                    emit(emptyPreferences())
                }
                .map { preferencias ->
                    preferencias[MODO_OSCURO] ?: true
                }
                .collectAsState(initial = true)

            TechTokensssTheme(
                darkTheme = modoOscuro
            ) {

                AppNavigation(
                    modoOscuro = modoOscuro,

                    onModoOscuroChange = { nuevoValor ->

                        lifecycleScope.launch {

                            dataStore.edit { preferencias ->
                                preferencias[MODO_OSCURO] = nuevoValor
                            }
                        }
                    }
                )
            }
        }
    }
}