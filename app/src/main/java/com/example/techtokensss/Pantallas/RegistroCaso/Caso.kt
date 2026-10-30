package com.example.techtokensss.Pantallas.RegistroCaso

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image

import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.techtokensss.Datos.Modelos.Categoria
import com.example.techtokensss.Datos.MondoDBAtlas.TechTokenApi
import com.example.techtokensss.Datos.Supabase.AuthSupa

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class ArchivoSeleccionado(
    val uri: Uri,
    val nombre: String
)

private fun obtenerNombreArchivo(
    context: Context,
    uri: Uri
): String {
    var nombre: String? = null

    context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columna = cursor.getColumnIndex(
                OpenableColumns.DISPLAY_NAME
            )

            if (columna >= 0) {
                nombre = cursor.getString(columna)
            }
        }
    }

    return nombre ?: uri.lastPathSegment ?: "Archivo adjunto"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearCasoScreen(
    onVolverClick: () -> Unit,
    onCrearCasoClick: (
        asunto: String,
        descripcion: String,
        categoria: String
    ) -> Unit
) {
    var asunto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var menuAbierto by remember { mutableStateOf(false) }

    var categorias by remember {
        mutableStateOf<List<Categoria>>(emptyList())
    }

    var cargandoCategorias by remember {
        mutableStateOf(true)
    }

    var creandoCaso by remember {
        mutableStateOf(false)
    }

    var archivosSeleccionados by remember {
        mutableStateOf<List<ArchivoSeleccionado>>(emptyList())
    }

    // Archivos que ya se guardaron correctamente en el backend.
    var archivosSubidos by remember {
        mutableStateOf<Set<Uri>>(emptySet())
    }

    // ID persistido para no crear un caso duplicado al reintentar.
    var idCasoCreado by remember {
        mutableStateOf<String?>(null)
    }

    // Control independiente del registro de novedades.
    var novedadCreacionRegistrada by remember {
        mutableStateOf(false)
    }

    var evidenciasConNovedad by remember {
        mutableStateOf<Set<Uri>>(emptySet())
    }

    var mensajeProceso by remember {
        mutableStateOf<String?>(null)
    }

    var mensajeError by remember {
        mutableStateOf<String?>(null)
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val casoRepository = remember {
        CasoRepository()
    }

    val api = remember {
        TechTokenApi()
    }

    val authSupa = remember {
        AuthSupa()
    }

    // Selector de imágenes.
    val selectorImagenes = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val nuevosArchivos = uris.map { uri ->
                ArchivoSeleccionado(
                    uri = uri,
                    nombre = obtenerNombreArchivo(context, uri)
                )
            }

            archivosSeleccionados =
                (archivosSeleccionados + nuevosArchivos)
                    .distinctBy { it.uri }

            mensajeError = null
        }
    }

    // Selector de documentos.
    val selectorDocumentos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val nuevosArchivos = uris.map { uri ->
                ArchivoSeleccionado(
                    uri = uri,
                    nombre = obtenerNombreArchivo(context, uri)
                )
            }

            archivosSeleccionados =
                (archivosSeleccionados + nuevosArchivos)
                    .distinctBy { it.uri }

            mensajeError = null
        }
    }

    // Cargar categorías desde Supabase.
    LaunchedEffect(Unit) {
        val resultado = casoRepository.obtenerCategorias()

        if (resultado.isSuccess) {
            categorias = resultado.getOrNull() ?: emptyList()
        } else {
            mensajeError =
                "No se pudieron cargar las categorías."
        }

        cargandoCategorias = false
    }

    val descripcionCategoria = categorias
        .find { it.nombre == categoriaSeleccionada }
        ?.descripcion

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Barra superior.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onVolverClick,
                enabled = !creandoCaso
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Crear caso",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Nuevo caso",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Describe el problema que estás teniendo.",
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.7f
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Asunto.
        Text(
            text = "Asunto",
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = asunto,
            onValueChange = { asunto = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = idCasoCreado == null && !creandoCaso,
            label = { Text("Asunto del caso") },
            placeholder = {
                Text("Ej. Mi computador no enciende")
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Descripción.
        Text(
            text = "Descripción",
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            enabled = idCasoCreado == null && !creandoCaso,
            label = { Text("Descripción del problema") },
            placeholder = {
                Text("Describe detalladamente el problema...")
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Categoría.
        Text(
            text = "Categoría",
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
            expanded = menuAbierto,
            onExpandedChange = {
                if (idCasoCreado == null && !creandoCaso) {
                    menuAbierto = !menuAbierto
                }
            }
        ) {
            OutlinedTextField(
                value = categoriaSeleccionada,
                onValueChange = {},
                readOnly = true,
                enabled = idCasoCreado == null && !creandoCaso,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Categoría") },
                placeholder = {
                    Text(
                        if (cargandoCategorias) {
                            "Cargando categorías..."
                        } else {
                            "Selecciona una categoría"
                        }
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuAbierto
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = {
                    menuAbierto = false
                }
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = {
                            Text(categoria.nombre)
                        },
                        onClick = {
                            categoriaSeleccionada = categoria.nombre
                            menuAbierto = false
                        }
                    )
                }
            }
        }

        if (descripcionCategoria != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = descripcionCategoria,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.7f
                ),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Evidencias.
        Text(
            text = "Evidencias",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Selecciona imágenes o documentos relacionados con el caso.",
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.7f
            ),
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    selectorImagenes.launch("image/*")
                },
                enabled = !creandoCaso && idCasoCreado == null,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Seleccionar imágenes"
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text("Imágenes")
            }

            OutlinedButton(
                onClick = {
                    selectorDocumentos.launch(
                        arrayOf(
                            "application/pdf",
                            "text/plain",
                            "application/msword",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            "application/vnd.ms-excel",
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                    )
                },
                enabled = !creandoCaso && idCasoCreado == null,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Seleccionar documentos"
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text("Documentos")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (archivosSeleccionados.isNotEmpty()) {
            Text(
                text = "Archivos seleccionados (${archivosSeleccionados.size})",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            archivosSeleccionados.forEach { archivo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = archivo.nombre,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (archivo.uri in archivosSubidos) {
                        Text(
                            text = if (archivo.uri in evidenciasConNovedad) {
                                "Subido"
                            } else {
                                "Subido · pendiente de historial"
                            },
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        IconButton(
                            onClick = {
                                archivosSeleccionados =
                                    archivosSeleccionados.filterNot {
                                        it.uri == archivo.uri
                                    }

                                mensajeError = null
                            },
                            enabled = !creandoCaso &&
                                    idCasoCreado == null
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Quitar ${archivo.nombre}"
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "No has seleccionado archivos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        mensajeProceso?.let { mensaje ->
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        mensajeError?.let { mensaje ->
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Crear el caso, registrar su novedad y procesar las evidencias.
        Button(
            onClick = {
                if (creandoCaso) return@Button

                scope.launch {
                    creandoCaso = true
                    mensajeError = null

                    try {
                        // PASO 1: crear el caso una sola vez.
                        if (idCasoCreado == null) {
                            mensajeProceso = "Creando caso..."

                            val resultado = casoRepository.crearCaso(
                                asunto = asunto.trim(),
                                descripcion = descripcion.trim(),
                                nombreCategoria = categoriaSeleccionada
                            )

                            if (resultado.isFailure) {
                                throw Exception(
                                    resultado.exceptionOrNull()?.message
                                        ?: "No se pudo crear el caso."
                                )
                            }

                            idCasoCreado = resultado.getOrThrow()
                        }

                        // Ya tenemos un ID no nulo para todos los pasos siguientes.
                        val idCasoFinal = idCasoCreado
                            ?: throw Exception(
                                "No se pudo recuperar el ID del caso."
                            )

                        // PASO 2: obtener el token de la sesión.
                        mensajeProceso = "Verificando sesión..."

                        val token = authSupa.obtenerTokenActual()

                        if (token.isNullOrBlank()) {
                            throw Exception(
                                "El caso está creado, pero no se pudo obtener " +
                                        "el token de sesión. Inicia sesión nuevamente."
                            )
                        }

                        // PASO 3: registrar la creación del caso.
                        // Si falla, se podrá reintentar sin crear otro caso.
                        if (!novedadCreacionRegistrada) {
                            mensajeProceso = "Registrando creación del caso..."

                            val resultadoHistorial = withContext(Dispatchers.IO) {
                                api.registrarEvento(
                                    idCaso = idCasoFinal,
                                    token = token,
                                    tipoActividad = "caso_creado",
                                    descripcion = "Se creó el caso de soporte.",
                                    cambios = mapOf(
                                        "asunto" to asunto.trim(),
                                        "descripcion" to descripcion.trim(),
                                        "categoria" to categoriaSeleccionada
                                    )
                                )
                            }

                            if (resultadoHistorial.isFailure) {
                                throw Exception(
                                    "El caso se creó, pero no se pudo registrar " +
                                            "su novedad: " +
                                            (
                                                    resultadoHistorial
                                                        .exceptionOrNull()
                                                        ?.message
                                                        ?: "Error desconocido"
                                                    )
                                )
                            }

                            novedadCreacionRegistrada = true
                        }

                        // PASO 4: subir las evidencias que falten
                        // y registrar su novedad por separado.
                        for ((indice, archivo) in archivosSeleccionados.withIndex()) {

                            // Subir solamente si todavía no se ha subido.
                            if (archivo.uri !in archivosSubidos) {
                                mensajeProceso =
                                    "Subiendo evidencia ${indice + 1} " +
                                            "de ${archivosSeleccionados.size}..."

                                val resultadoSubida = withContext(Dispatchers.IO) {
                                    api.subirEvidencia(
                                        context = context,
                                        idCaso = idCasoFinal,
                                        token = token,
                                        uri = archivo.uri,
                                        nombreArchivo = archivo.nombre
                                    )
                                }

                                if (resultadoSubida.isFailure) {
                                    throw Exception(
                                        resultadoSubida.exceptionOrNull()?.message
                                            ?: "No se pudo subir ${archivo.nombre}."
                                    )
                                }

                                // Guardamos el estado de subida antes
                                // de intentar registrar la novedad.
                                archivosSubidos =
                                    archivosSubidos + archivo.uri
                            }

                            // Si ya se subió, pero falta su novedad,
                            // reintentamos solo el registro del historial.
                            if (archivo.uri !in evidenciasConNovedad) {
                                mensajeProceso =
                                    "Registrando evidencia en el historial..."

                                val resultadoHistorial = withContext(Dispatchers.IO) {
                                    api.registrarEvento(
                                        idCaso = idCasoFinal,
                                        token = token,
                                        tipoActividad = "evidencia_adjuntada",
                                        descripcion =
                                            "Se adjuntó el archivo ${archivo.nombre}.",
                                        cambios = mapOf(
                                            "nombreArchivo" to archivo.nombre
                                        )
                                    )
                                }

                                if (resultadoHistorial.isFailure) {
                                    throw Exception(
                                        "El archivo ${archivo.nombre} se subió, " +
                                                "pero no se pudo registrar su novedad: " +
                                                (
                                                        resultadoHistorial
                                                            .exceptionOrNull()
                                                            ?.message
                                                            ?: "Error desconocido"
                                                        )
                                    )
                                }

                                evidenciasConNovedad =
                                    evidenciasConNovedad + archivo.uri
                            }
                        }

                        // PASO 5: finalizar cuando todos los pasos terminaron.
                        mensajeProceso =
                            "Caso, evidencias e historial guardados correctamente."

                        onVolverClick()

                    } catch (e: Exception) {
                        mensajeProceso = null

                        mensajeError = if (idCasoCreado != null) {
                            "El caso ya está creado. " +
                                    "${e.message ?: "Ocurrió un error."} " +
                                    "Puedes reintentar sin crear otro caso."
                        } else {
                            e.message ?: "Ocurrió un error al crear el caso."
                        }
                    } finally {
                        creandoCaso = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !creandoCaso && (
                    idCasoCreado != null ||
                            (
                                    asunto.isNotBlank() &&
                                            descripcion.isNotBlank() &&
                                            categoriaSeleccionada.isNotBlank() &&
                                            !cargandoCategorias
                                    )
                    )
        ) {
            Text(
                text = when {
                    creandoCaso -> "Procesando..."
                    idCasoCreado != null -> "Reintentar proceso"
                    else -> "Crear caso"
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
