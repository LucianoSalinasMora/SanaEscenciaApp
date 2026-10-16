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
import com.sanaesencia.app.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: UserViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var run by remember { mutableStateOf("") }
    var names by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var securityQuestion by remember { mutableStateOf("¿Nombre de tu primera mascota?") }
    var securityAnswer by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = run, onValueChange = { run = it }, label = { Text("RUN") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = names, onValueChange = { names = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Recuperación de cuenta", style = MaterialTheme.typography.titleSmall)

            OutlinedTextField(value = securityQuestion, onValueChange = { securityQuestion = it }, label = { Text("Pregunta de Seguridad") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = securityAnswer, onValueChange = { securityAnswer = it }, label = { Text("Respuesta") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    val newUser = UserEntity(
                        run = run,
                        name = names,
                        phone = email,
                        passwordHash = password,
                        role = UserRole.PACIENTE,
                        securityQuestion = securityQuestion,
                        securityAnswer = securityAnswer
                    )
                    viewModel.registerUser(newUser) {
                        onRegisterSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 16.dp)
            ) {
                Text("Registrarse")
            }
        }
    }
}