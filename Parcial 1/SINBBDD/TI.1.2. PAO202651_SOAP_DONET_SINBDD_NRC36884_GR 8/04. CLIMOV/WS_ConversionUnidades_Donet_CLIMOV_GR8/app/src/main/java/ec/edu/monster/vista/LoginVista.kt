package ec.edu.monster.vista

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.monster.controlador.LoginViewModel

@Composable
fun LoginVista(
    viewModel: LoginViewModel = viewModel(),
    onLoginExitoso: () -> Unit = {}
) {
    val colores = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    var mostrarClave by remember { mutableStateOf(false) }

    // Navegar cuando el login sea exitoso (se ejecuta una sola vez por cambio de mensaje)
    LaunchedEffect(viewModel.mensajeText) {
        if (viewModel.mensajeText == "¡Login exitoso!") {
            onLoginExitoso()
        }
    }

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
                .imePadding()
        ) {

            // ───────── Cabecera con degradado ─────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(colores.primary, colores.tertiary)
                        ),
                        shape = RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp)
                    )
                    .statusBarsPadding()
                    .padding(top = 40.dp, bottom = 56.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = colores.onPrimary.copy(alpha = 0.2f),
                        modifier = Modifier.size(88.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Icono candado",
                                tint = colores.onPrimary,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Bienvenido",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = colores.onPrimary
                        )
                    )
                    Text(
                        text = "Inicia sesión para continuar",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = colores.onPrimary.copy(alpha = 0.85f)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ───────── Formulario ─────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = colores.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        // Usuario
                        OutlinedTextField(
                            value = viewModel.usuarioText,
                            onValueChange = { viewModel.onUsuarioChange(it) },
                            label = { Text("Usuario") },
                            placeholder = { Text("Ingrese su usuario") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = colores.primary
                                )
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colores.primary,
                                unfocusedBorderColor = colores.outline.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Contraseña (oculta, con botón Mostrar/Ocultar)
                        OutlinedTextField(
                            value = viewModel.claveText,
                            onValueChange = { viewModel.onClaveChange(it) },
                            label = { Text("Contraseña") },
                            placeholder = { Text("••••••••") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = colores.primary
                                )
                            },
                            trailingIcon = {
                                TextButton(onClick = { mostrarClave = !mostrarClave }) {
                                    Text(
                                        text = if (mostrarClave) "Ocultar" else "Mostrar",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            visualTransformation = if (mostrarClave) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.login()
                                }
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colores.primary,
                                unfocusedBorderColor = colores.outline.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Botón de ingreso
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.login()
                            },
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
                                    text = "Ingresando...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "Iniciar Sesión",
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

                // Mensaje de estado / error
                AnimatedVisibility(
                    visible = viewModel.mensajeText.isNotEmpty(),
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut()
                ) {
                    val esError = viewModel.mensajeText.contains("incorrecto") ||
                            viewModel.mensajeText.contains("llene") ||
                            viewModel.mensajeText.startsWith("Error")

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = if (esError) colores.errorContainer else colores.primaryContainer
                    ) {
                        Text(
                            text = viewModel.mensajeText,
                            color = if (esError) colores.onErrorContainer else colores.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}