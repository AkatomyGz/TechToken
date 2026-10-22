package com.example.techtokensss.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.techtokensss.Datos.Modelos.Caso
import com.example.techtokensss.ui.theme.TechTicketTheme

// =====================================================================
// INSETS (barras del sistema y teclado)
// =====================================================================

/**
 * Aplica el padding de las barras del sistema y del teclado.
 * Uso: en la raíz de cada pantalla, DESPUÉS de background() y ANTES de padding().
 *
 *   Column(Modifier.fillMaxSize().background(...).screenInsets().padding(20.dp))
 *
 * Así el fondo cubre toda la pantalla y el contenido no queda bajo las barras.
 */
fun Modifier.screenInsets(): Modifier = this.safeDrawingPadding()

// =====================================================================
// BARRA SUPERIOR
// =====================================================================

/**
 * Barra superior común. Muestra flecha "Volver" si se pasa [onVolverClick],
 * o un icono propio mediante [navigationIcon] (por ejemplo, el menú).
 */
@Composable
fun TechTicketTopBar(
    titulo: String,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    onVolverClick: (() -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onBackground
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (navigationIcon != null) {
                navigationIcon()
            } else if (onVolverClick != null) {
                IconButton(onClick = onVolverClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (subtitulo != null) {
                    Text(
                        text = subtitulo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            actions()
        }
    }
}

// =====================================================================
// ESTADOS DE PANTALLA: CARGA, ERROR, VACÍO
// El tamaño lo decide quien los usa (fillMaxSize(), weight(1f), etc.)
// =====================================================================

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    mensaje: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()

        if (mensaje != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorState(
    mensaje: String,
    modifier: Modifier = Modifier,
    onReintentar: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(40.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        if (onReintentar != null) {
            Spacer(Modifier.height(16.dp))

            FilledTonalButton(onClick = onReintentar) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Reintentar")
            }
        }
    }
}

@Composable
fun EmptyState(
    titulo: String,
    modifier: Modifier = Modifier,
    descripcion: String? = null,
    icono: ImageVector = Icons.Outlined.Inbox
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(40.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        if (descripcion != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// =====================================================================
// TEXTOS
// =====================================================================

/** Encabezado de sección (ej. "Cuenta", "Preferencias", "Casos asignados a ti"). */
@Composable
fun SectionHeader(
    titulo: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = titulo,
        modifier = modifier,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
}

/** Dato con título pequeño arriba y valor debajo. Reemplaza a DatoCaso. */
@Composable
fun InfoRow(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// =====================================================================
// MENSAJES DE RESULTADO
// =====================================================================

enum class TipoMensaje { Exito, Error }

/** Mensaje en línea para operaciones exitosas o fallidas. */
@Composable
fun FeedbackBanner(
    mensaje: String,
    tipo: TipoMensaje,
    modifier: Modifier = Modifier
) {
    val extra = TechTicketTheme.colors
    val colores = MaterialTheme.colorScheme

    val fondo = if (tipo == TipoMensaje.Exito) extra.solucionado.container else colores.errorContainer
    val contenido = if (tipo == TipoMensaje.Exito) extra.solucionado.onContainer else colores.onErrorContainer
    val icono = if (tipo == TipoMensaje.Exito) Icons.Outlined.CheckCircle else Icons.Outlined.ErrorOutline

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(fondo, MaterialTheme.shapes.medium)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = contenido,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = contenido,
            modifier = Modifier.weight(1f)
        )
    }
}

// =====================================================================
// ETIQUETA DE ESTADO
// =====================================================================

/** Etiqueta con el estado del caso. Recibe el valor crudo ("en_proceso"). */
@Composable
fun StatusChip(
    estado: String,
    modifier: Modifier = Modifier
) {
    val colores = TechTicketTheme.colors.paraEstado(estado)

    Row(
        modifier = modifier
            .background(colores.container, CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(colores.solid, CircleShape)
        )
        Text(
            text = formatearEstado(estado),
            style = MaterialTheme.typography.labelMedium,
            color = colores.onContainer,
            maxLines = 1
        )
    }
}

// =====================================================================
// TARJETA DE CASO
// =====================================================================

/**
 * Tarjeta de caso para listas.
 * - [onClick] la vuelve clicable (si es null, no lo es y no se ve como tal).
 * - [accion] permite agregar contenido al final (ej. botón "Reclamar caso").
 */
@Composable
fun CaseCard(
    caso: Caso,
    nombreCategoria: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    accion: (@Composable ColumnScope.() -> Unit)? = null
) {
    val contenido: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CASO #${caso.idCaso.take(8)}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                StatusChip(estado = caso.estado)
            }

            Text(
                text = caso.asunto,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = caso.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetaItem(
                    icono = Icons.Outlined.Category,
                    texto = nombreCategoria,
                    modifier = Modifier.weight(1f)
                )
                MetaItem(
                    icono = Icons.Outlined.Schedule,
                    texto = formatearFechaCorta(caso.fechaCreacion)
                )
            }

            accion?.invoke(this)
        }
    }

    val forma = MaterialTheme.shapes.large
    val colores = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
    val borde = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = forma,
            colors = colores,
            border = borde,
            content = contenido
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = forma,
            colors = colores,
            border = borde,
            content = contenido
        )
    }
}

@Composable
private fun MetaItem(
    icono: ImageVector,
    texto: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}