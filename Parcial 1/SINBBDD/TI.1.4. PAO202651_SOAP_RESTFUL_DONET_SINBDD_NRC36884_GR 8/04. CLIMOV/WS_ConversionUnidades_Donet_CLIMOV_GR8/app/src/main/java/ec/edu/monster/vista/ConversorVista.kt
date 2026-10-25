package ec.edu.monster.vista

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.monster.controlador.ConversorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversorVista(
    viewModel: ConversorViewModel = viewModel(),
    onLogout: () -> Unit = {}
) {
    val unidadesDisponibles = viewModel.unidadesDisponibles
    val colores = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colores.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {

            // ───────── Encabezado con degradado ─────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(colores.primary, colores.tertiary)
                        ),
                        shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
                    )
                    .statusBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 36.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Conversor de Unidades",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = colores.onPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Elige una categoría, escribe el valor y convierte",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colores.onPrimary.copy(alpha = 0.85f)
                            )
                        )
                    }

                    IconButton(
                        onClick = { viewModel.logout(onLogout) },
                        enabled = !viewModel.estaCargando
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = colores.onPrimary
                        )
                    }
                }
            }

            // ───────── Contenido ─────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // 1. Categorías (con scroll horizontal por si hay muchas)
                Column {
                    Text(
                        text = "CATEGORÍA",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = colores.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        viewModel.magnitudes.forEach { tipo ->
                            val seleccionado = viewModel.tipoSeleccionado == tipo
                            FilterChip(
                                selected = seleccionado,
                                onClick = { viewModel.onTipoChange(tipo) },
                                label = {
                                    Text(
                                        text = tipo,
                                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                shape = RoundedCornerShape(50),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colores.primary,
                                    selectedLabelColor = colores.onPrimary
                                )
                            )
                        }
                    }

                    // Mensaje del envelope si el catálogo no se pudo cargar desde el servidor
                    if (viewModel.mensajeCatalogo.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = viewModel.mensajeCatalogo,
                            style = MaterialTheme.typography.bodySmall,
                            color = colores.error
                        )
                    }
                }

                // 2. Formulario
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = colores.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = viewModel.valorOrigenText,
                            onValueChange = { viewModel.onValorOrigenChange(it) },
                            label = { Text("Valor a convertir") },
                            placeholder = { Text("Ejemplo: 100.5") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            textStyle = MaterialTheme.typography.titleLarge,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colores.primary,
                                unfocusedBorderColor = colores.outline.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        DropdownUnidades(
                            titulo = "Desde",
                            seleccionado = viewModel.unidadOrigen,
                            opciones = unidadesDisponibles,
                            onSeleccion = { viewModel.unidadOrigen = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Botón para intercambiar origen y destino
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            FilledTonalIconButton(
                                onClick = {
                                    val temp = viewModel.unidadOrigen
                                    viewModel.unidadOrigen = viewModel.unidadDestino
                                    viewModel.unidadDestino = temp
                                },
                                shape = CircleShape,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Intercambiar unidades"
                                )
                            }
                        }

                        DropdownUnidades(
                            titulo = "Hacia",
                            seleccionado = viewModel.unidadDestino,
                            opciones = unidadesDisponibles,
                            onSeleccion = { viewModel.unidadDestino = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.convertirUnidades() },
                            enabled = !viewModel.estaCargando,
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colores.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            if (viewModel.estaCargando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = colores.onPrimary,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Convirtiendo...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "Convertir ahora",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // 3. Resultado
                AnimatedVisibility(
                    visible = viewModel.resultadoText.isNotEmpty(),
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = colores.primaryContainer)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            colores.primaryContainer,
                                            colores.secondaryContainer
                                        )
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            Column {
                                Text(
                                    text = "RESULTADO",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = colores.onPrimaryContainer.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = viewModel.resultadoText,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = colores.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Desplegable de unidades
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownUnidades(
    titulo: String,
    seleccionado: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = seleccionado,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(titulo) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = opcion,
                            fontWeight = if (opcion == seleccionado) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
