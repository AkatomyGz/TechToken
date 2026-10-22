
package com.example.techtokensss.Pantallas.Dashboard

data class EstadisticasCasos(
    val registrados: Int = 0,
    val enProceso: Int = 0,
    val solucionados: Int = 0,
    val cerrados: Int = 0
) {
    val total: Int
        get() = registrados + enProceso + solucionados + cerrados
}
