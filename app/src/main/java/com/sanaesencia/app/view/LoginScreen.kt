package com.sanaesencia.app.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.R
import com.sanaesencia.app.data.UserRole
import com.sanaesencia.app.data.Validators
import com.sanaesencia.app.viewmodel.UserViewModel

// ==========================================
// Pantalla de Inicio de Sesión (Login)
// ==========================================
@Composable
fun LoginScreen(
    viewModel: UserViewModel,
    onLoginSuccess: (UserRole) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    // ------------------------------------------
    // Estado y Validación del Formulario de Inicio de Sesión
    // ------------------------------------------
    var run by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val isRunValid = remember(run) { run.isBlank() || Validators.isValidRUN(run) }
    val isPasswordValid = remember(password) { password.isBlank() || Validators.isValidPassword(password) }
    val isFormValid = run.isNotBlank() && isRunValid && password.isNotBlank() && isPasswordValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ------------------------------------------
        // Identidad Visual y Bienvenida
        // ------------------------------------------
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Sanaesencia",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 16.dp)
        )
        Text(
            text = "Bienvenido a Sanaesencia",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))

        // ------------------------------------------
        // Campos de Entrada: RUN / Usuario y Contraseña
        // ------------------------------------------
        OutlinedTextField(
            value = run,
            onValueChange = {
                run = it.replace("\n", "").replace("\r", "")
                showError = false
            },
            label = { Text("RUN / Usuario") },
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            isError = !isRunValid,
            supportingText = {
                if (!isRunValid) {
                    Text("RUN o Usuario inválido", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it.replace("\n", "").replace("\r", "")
                showError = false
            },
            label = { Text("Contraseña") },
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isFormValid) {
                        viewModel.login(run, password) { user ->
                            if (user != null) {
                                onLoginSuccess(user.role)
                            } else {
                                showError = true
                            }
                        }
                    }
                }
            ),
            visualTransformation = PasswordVisualTransformation(),
            isError = !isPasswordValid,
            supportingText = {
                if (!isPasswordValid) {
                    Text("Mínimo 4 caracteres", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (showError) {
            Text(
                text = "Credenciales incorrectas. Verifique su RUN/Usuario y contraseña.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ------------------------------------------
        // Botón Principal de Envío / Inicio de Sesión
        // ------------------------------------------
        Button(
            onClick = {
                viewModel.login(run, password) { user ->
                    if (user != null) {
                        onLoginSuccess(user.role)
                    } else {
                        showError = true
                    }
                }
            },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Ingresar")
        }

        // ------------------------------------------
        // Enlaces de Navegación a Recuperación y Registro
        // ------------------------------------------
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToForgotPassword) {
            Text("¿Olvidaste tu contraseña?")
        }
        TextButton(onClick = onNavigateToSignUp) {
            Text("¿No tienes cuenta? Regístrate como Paciente")
        }
    }
}
