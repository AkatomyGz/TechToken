package com.example.techtokensss.Pantallas.Dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.techtokensss.ui.components.screenInsets
import com.example.techtokensss.ui.theme.APP_NAME

/**
 * Panel del menú lateral. La firma no cambia, por lo que los dashboards
 * que lo llaman directamente siguen funcionando.
 */
@Composable
fun MenuLateral(
    onPerfilClick: () -> Unit,
    onCasosClick: () -> Unit,
    onConfiguracionClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
) {
    // 78 % del ancho en teléfonos, con tope de 320 dp en pantallas grandes.
    val ancho = minOf(
        (LocalConfiguration.current.screenWidthDp * 0.78f).dp,
        320.dp
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(ancho)
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            )
            .screenInsets()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // Cabecera de marca
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.shapes.large
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SupportAgent,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column {
                Text(
                    text = APP_NAME,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Menú principal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ItemMenu(
                icono = Icons.Outlined.Person,
                texto = "Perfil",
                onClick = onPerfilClick
            )

            ItemMenu(
                icono = Icons.Outlined.Folder,
                texto = "Casos",
                onClick = onCasosClick
            )

            ItemMenu(
                icono = Icons.Outlined.Settings,
                texto = "Configuración",
                onClick = onConfiguracionClick
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        ItemMenu(
            icono = Icons.AutoMirrored.Filled.Logout,
            texto = "Cerrar sesión",
            onClick = onCerrarSesionClick,
            colorTexto = MaterialTheme.colorScheme.error,
            colorIcono = MaterialTheme.colorScheme.error
        )
    }
}

/**
 * Menú con fondo oscurecido y animación de entrada y salida.
 * Colócalo como ÚLTIMO hijo del Box raíz de la pantalla.
 * Con el menú abierto, el botón Atrás lo cierra en lugar de salir de la pantalla.
 */
@Composable
fun MenuLateralOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    onPerfilClick: () -> Unit,
    onCasosClick: () -> Unit,
    onConfiguracionClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
) {
    BackHandler(enabled = visible) {
        onDismiss()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClickLabel = "Cerrar menú",
                        onClick = onDismiss
                    )
            )
        }

        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally(initialOffsetX = { ancho -> -ancho }),
            exit = slideOutHorizontally(targetOffsetX = { ancho -> -ancho })
        ) {
            MenuLateral(
                onPerfilClick = onPerfilClick,
                onCasosClick = onCasosClick,
                onConfiguracionClick = onConfiguracionClick,
                onCerrarSesionClick = onCerrarSesionClick
            )
        }
    }
}

@Composable
private fun ItemMenu(
    icono: ImageVector,
    texto: String,
    onClick: () -> Unit,
    colorTexto: Color = MaterialTheme.colorScheme.onSurface,
    colorIcono: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = colorIcono
        )

        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            color = colorTexto
        )
    }
}