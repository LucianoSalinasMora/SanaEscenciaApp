package com.sanaesencia.app.view


import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.data.AppointmentEntity
import com.sanaesencia.app.viewmodel.BookingViewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    viewModel: BookingViewModel,
    onBookingSuccess: (AppointmentEntity) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendamiento de Citas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Servicio Seleccionado: ${state.serviceType}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = !state.isOnline,
                    onClick = { viewModel.onModeChanged(false) },
                    label = { Text("Presencial (Los Vilos)") },
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = state.isOnline,
                    onClick = { viewModel.onModeChanged(true) },
                    label = { Text("Online (MEDIlink)") },
                    leadingIcon = { Icon(Icons.Default.Computer, contentDescription = null) }
                )
            }

            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onNameChanged(it) },
                label = { Text("Nombre del Paciente") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = state.nameError != null,
                supportingText = { state.nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                trailingIcon = {
                    if (state.email.isNotBlank()) {
                        val icon = if (state.emailError == null) Icons.Default.CheckCircle else Icons.Default.Warning
                        val tint = if (state.emailError == null) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                        Icon(icon, contentDescription = null, tint = tint)
                    }
                },
                isError = state.emailError != null,
                supportingText = { state.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.onPhoneChanged(it) },
                label = { Text("Teléfono / WhatsApp") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                isError = state.phoneError != null,
                supportingText = { state.phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.date,
                onValueChange = { viewModel.onDateChanged(it) },
                label = { Text("Fecha de Atención (AAAA-MM-DD)") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                isError = state.dateError != null,
                supportingText = { state.dateError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = state.isFormValid,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Formulario completado correctamente", color = Color(0xFF2E7D32))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.submitBooking { appointment ->
                        onBookingSuccess(appointment)
                    }
                },
                enabled = state.isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Confirmar y Guardar Cita")
            }
        }
    }
}