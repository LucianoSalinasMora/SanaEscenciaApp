package com.sanaesencia.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.data.UserRole
import com.sanaesencia.app.viewmodel.UserViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.sanaesencia.app.R
@Composable
fun LoginScreen(
    viewModel: UserViewModel,
    onLoginSuccess: (UserRole) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var run by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // Nombre de tu archivo sin extensión
            contentDescription = "Logo Sanaesencia",
            modifier = Modifier
                .size(150.dp) // Ajusta el ancho y alto según lo necesites
                .padding(bottom = 16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = run,
            onValueChange = {
                run = it
                showError = false
            },
            label = { Text("RUN") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                showError = false
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (showError) {
            Text(
                text = "Credenciales incorrectas",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

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
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToForgotPassword) {
            Text("¿Olvidaste tu contraseña?")
        }
        TextButton(onClick = onNavigateToSignUp) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }
    }
}