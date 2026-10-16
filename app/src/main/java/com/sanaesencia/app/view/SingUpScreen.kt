package com.sanaesencia.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.data.UserEntity
import com.sanaesencia.app.data.UserRole
import com.sanaesencia.app.data.Validators
import com.sanaesencia.app.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: UserViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var run by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var securityQuestion by remember { mutableStateOf("¿Nombre de tu primera mascota?") }
    var securityAnswer by remember { mutableStateOf("") }

    val isRunValid = remember(run) { run.isBlank() || Validators.isValidRUN(run) }
    val isNameValid = remember(name) { name.isBlank() || Validators.isValidName(name) }
    val isEmailValid = remember(email) { email.isBlank() || Validators.isValidEmail(email) }
    val isPhoneValid = remember(phone) { phone.isBlank() || Validators.isValidPhone(phone) }
    val isPasswordValid = remember(password) { password.isBlank() || Validators.isValidPassword(password) }
    val isSecurityAnswerValid = remember(securityAnswer) { securityAnswer.isBlank() || securityAnswer.trim().length >= 2 }

    val isFormCompleteAndValid = run.isNotBlank() && isRunValid &&
            name.isNotBlank() && isNameValid &&
            email.isNotBlank() && isEmailValid &&
            phone.isNotBlank() && isPhoneValid &&
            password.isNotBlank() && isPasswordValid &&
            securityQuestion.isNotBlank() &&
            securityAnswer.isNotBlank() && isSecurityAnswerValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Paciente") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Ingrese sus datos personales para crear su cuenta de Paciente:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = run,
                onValueChange = { run = it },
                label = { Text("RUN (Ej: 12345678-9)") },
                isError = !isRunValid,
                supportingText = { if (!isRunValid) Text("RUN chileno inválido", color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre Completo") },
                isError = !isNameValid,
                supportingText = { if (!isNameValid) Text("Mínimo 3 caracteres", color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico (ej. usuario@dominio.com)") },
                isError = !isEmailValid,
                supportingText = { if (!isEmailValid) Text("Formato de correo inválido", color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono (+56912345678)") },
                isError = !isPhoneValid,
                supportingText = { if (!isPhoneValid) Text("Número de teléfono inválido (8 a 12 dígitos)", color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = !isPasswordValid,
                supportingText = { if (!isPasswordValid) Text("Mínimo 4 caracteres", color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Recuperación de cuenta", style = MaterialTheme.typography.titleSmall)

            OutlinedTextField(
                value = securityQuestion,
                onValueChange = { securityQuestion = it },
                label = { Text("Pregunta de Seguridad") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = securityAnswer,
                onValueChange = { securityAnswer = it },
                label = { Text("Respuesta de Seguridad") },
                isError = !isSecurityAnswerValid,
                supportingText = { if (!isSecurityAnswerValid) Text("Ingrese una respuesta válida", color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val newUser = UserEntity(
                        run = run.trim(),
                        name = name.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        passwordHash = password.trim(),
                        role = UserRole.PACIENTE,
                        securityQuestion = securityQuestion.trim(),
                        securityAnswer = securityAnswer.trim()
                    )
                    viewModel.registerUser(newUser) {
                        onRegisterSuccess()
                    }
                },
                enabled = isFormCompleteAndValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Registrarse como Paciente")
            }
        }
    }
}
