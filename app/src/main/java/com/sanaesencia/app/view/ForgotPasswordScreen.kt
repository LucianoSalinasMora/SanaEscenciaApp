package com.sanaesencia.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.data.UserEntity
import com.sanaesencia.app.viewmodel.UserViewModel

// ==========================================
// Pantalla de Recuperación de Contraseña (3 Pasos)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: UserViewModel,
    onBack: () -> Unit,
    onPasswordResetSuccess: () -> Unit
) {
    // Control de paso actual (1: RUN, 2: Pregunta Seguridad, 3: Nueva Clave)
    var step by remember { mutableIntStateOf(1) }

    var run by remember { mutableStateOf("") }
    var runError by remember { mutableStateOf(false) }

    var fetchedUser by remember { mutableStateOf<UserEntity?>(null) }
    var answer by remember { mutableStateOf("") }
    var answerError by remember { mutableStateOf(false) }

    var newPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar Contraseña") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ------------------------------------------
            // Paso 1: Búsqueda de Usuario por RUN
            // ------------------------------------------
            // --- PASO 1: Ingresar RUN ---
            OutlinedTextField(
                value = run,
                onValueChange = { run = it.replace("\n", "").replace("\r", ""); runError = false },
                isError = runError,
                label = { Text("Ingresa tu RUN") },
                singleLine = true,
                maxLines = 1,
                supportingText = { if (runError) Text("Usuario no encontrado") },
                enabled = step == 1,
                modifier = Modifier.fillMaxWidth()
            )

            if (step == 1) {
                Button(
                    onClick = {
                        viewModel.getUserByRun(run) { user ->
                            if (user != null) {
                                fetchedUser = user
                                step = 2
                            } else {
                                runError = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) { Text("Buscar Usuario") }
            }

            // ------------------------------------------
            // Paso 2: Validación de la Pregunta de Seguridad
            // ------------------------------------------
            // --- PASO 2: Pregunta de Seguridad ---
            if (step >= 2 && fetchedUser != null) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
                Text("Pregunta de seguridad:", color = MaterialTheme.colorScheme.primary)
                Text(fetchedUser!!.securityQuestion, style = MaterialTheme.typography.bodyLarge)

                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it.replace("\n", "").replace("\r", ""); answerError = false },
                    label = { Text("Tu respuesta") },
                    singleLine = true,
                    maxLines = 1,
                    isError = answerError,
                    supportingText = { if (answerError) Text("Respuesta incorrecta") },
                    enabled = step == 2,
                    modifier = Modifier.fillMaxWidth()
                )

                if (step == 2) {
                    Button(
                        onClick = {
                            if (answer.equals(fetchedUser!!.securityAnswer, ignoreCase = true)) {
                                step = 3
                            } else {
                                answerError = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) { Text("Verificar Respuesta") }
                }
            }

            // ------------------------------------------
            // Paso 3: Asignación y Guardado de Nueva Contraseña
            // ------------------------------------------
            // --- PASO 3: Nueva Contraseña ---
            if (step == 3) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it.replace("\n", "").replace("\r", "") },
                    label = { Text("Nueva Contraseña") },
                    singleLine = true,
                    maxLines = 1,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        viewModel.updatePassword(run, newPassword) {
                            onPasswordResetSuccess()
                        }
                    },
                    enabled = newPassword.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) { Text("Guardar y Volver") }
            }
        }
    }
}