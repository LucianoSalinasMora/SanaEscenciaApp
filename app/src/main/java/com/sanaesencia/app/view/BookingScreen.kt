package com.sanaesencia.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.data.AppointmentEntity
import com.sanaesencia.app.data.mockProfessionals
import com.sanaesencia.app.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookingScreen(
    viewModel: BookingViewModel,
    onBookingSuccess: (AppointmentEntity) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showDatePickerDialog by remember { mutableStateOf(false) }

    val specialties = mockProfessionals.map { it.specialty }.distinct()
    val availableProfessionals = mockProfessionals.filter { it.specialty == state.serviceType }

    // Bloques horarios de atención
    val availableTimeSlots = listOf(
        "09:00 - 10:00",
        "10:00 - 11:00",
        "11:00 - 12:00",
        "15:00 - 16:00",
        "16:00 - 17:00",
        "17:00 - 18:00"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendamiento de Cita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Modalidad
            Text("1. Modalidad de Atención", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !state.isOnline,
                    onClick = { viewModel.onModeChanged(false) },
                    label = { Text("Presencial (Los Vilos)") }
                )
                FilterChip(
                    selected = state.isOnline,
                    onClick = { viewModel.onModeChanged(true) },
                    label = { Text("Online (MEDIlink)") }
                )
            }

            // 2. Especialidad
            Text("2. Selecciona la Especialidad", style = MaterialTheme.typography.titleMedium)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                specialties.forEach { spec ->
                    ElevatedCard(
                        onClick = { viewModel.onSpecialtySelected(spec) },
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (state.serviceType == spec) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.serviceType == spec,
                                onClick = { viewModel.onSpecialtySelected(spec) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(spec, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }

            // 3. Profesional
            if (state.serviceType.isNotBlank()) {
                Text("3. Selecciona el Profesional", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    availableProfessionals.forEach { pro ->
                        ElevatedCard(
                            onClick = { viewModel.onProfessionalSelected(pro.name) },
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = if (state.professionalName == pro.name) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = state.professionalName == pro.name,
                                    onClick = { viewModel.onProfessionalSelected(pro.name) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(pro.name, style = MaterialTheme.typography.bodyLarge)
                                    Text(pro.specialty, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }
                }
            }

            // 4. Calendario Interactivo
            if (state.professionalName.isNotBlank()) {
                Text("4. Selecciona el Día de Atención", style = MaterialTheme.typography.titleMedium)

                OutlinedCard(
                    onClick = { showDatePickerDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (state.dateFormatted.isBlank()) "Toca para abrir el calendario" else "Día Seleccionado: ${state.dateFormatted}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Diálogo DatePicker de Material 3
            if (showDatePickerDialog) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePickerDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    viewModel.onDateSelected(millis)
                                }
                                showDatePickerDialog = false
                            }
                        ) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePickerDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // 5. Bloques Horarios
            if (state.dateFormatted.isNotBlank()) {
                Text("5. Selecciona el Bloque Horario", style = MaterialTheme.typography.titleMedium)

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableTimeSlots.forEach { slot ->
                        FilterChip(
                            selected = state.timeSlot == slot,
                            onClick = { viewModel.onTimeSlotSelected(slot) },
                            label = { Text(slot) },
                            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }

            // Resumen y Confirmación
            if (state.isFormValid) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Resumen de tu Cita", style = MaterialTheme.typography.titleMedium, color = Color(0xFF2E7D32))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Especialidad: ${state.serviceType}")
                        Text("• Profesional: ${state.professionalName}")
                        Text("• Fecha: ${state.dateFormatted}")
                        Text("• Horario: ${state.timeSlot}")
                        Text("• Modalidad: ${if (state.isOnline) "Online (MEDIlink)" else "Presencial (Los Vilos)"}")
                    }
                }

                Button(
                    onClick = {
                        viewModel.submitBooking { appointment ->
                            onBookingSuccess(appointment)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Confirmar y Agendar Cita")
                }
            }
        }
    }
}