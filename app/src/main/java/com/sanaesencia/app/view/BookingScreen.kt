package com.sanaesencia.app.view

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.data.AppointmentEntity
import com.sanaesencia.app.viewmodel.BookingViewModel
import com.sanaesencia.app.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    userViewModel: UserViewModel,
    bookingViewModel: BookingViewModel,
    onBookingSuccess: (AppointmentEntity) -> Unit,
    onBack: () -> Unit
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val dbProfessionals by userViewModel.professionals.collectAsState()
    val state by bookingViewModel.uiState.collectAsState()
    val allAppointments by bookingViewModel.appointments.collectAsState()

    // Cargar automáticamente los datos del paciente logueado
    LaunchedEffect(currentUser) {
        bookingViewModel.loadPatientData(currentUser)
    }

    var showDatePicker by remember { mutableStateOf(false) }

    // Lista de Áreas / Especialidades del Centro
    val areas = listOf(
        "Psicología Infanto-Juvenil",
        "Psicología Adolescentes y Adultos",
        "Terapia Ocupacional",
        "Fonoaudiología",
        "Psicopedagogía",
        "Nutrición Integrativa",
        "Matronería y Salud Femenina",
        "Quiropraxia y Masajes",
        "Terapia Floral y Aromaterapia",
        "Evaluaciones del Neurodesarrollo",
        "Medicina General y Salud Mental",
        "Acupuntura"
    )

    // Filtrar profesionales disponibles según la especialidad seleccionada
    val availableProfessionals = remember(state.serviceType, dbProfessionals) {
        bookingViewModel.getAvailableProfessionals(dbProfessionals, state.serviceType)
    }

    // Opciones de Horarios disponibles
    val timeSlots = listOf(
        "09:00 AM",
        "10:00 AM",
        "11:00 AM",
        "12:00 PM",
        "15:00 PM",
        "16:00 PM",
        "17:00 PM"
    )

    // Bloques horarios ya reservados para el profesional y fecha seleccionados (Evita solapamientos)
    val bookedSlots = remember(allAppointments, state.date, state.professionalName) {
        allAppointments.filter {
            it.date == state.date &&
            it.professionalName.equals(state.professionalName, ignoreCase = true) &&
            it.state != "CANCELADA" &&
            it.status != "Cancelada"
        }.map { it.timeSlot }.toSet()
    }

    var areaExpanded by remember { mutableStateOf(false) }
    var proExpanded by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error Alert Dialog / Card si ocurre un conflicto de solapamiento o validación
            state.errorMessage?.let { errorMsg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Aviso de Validación", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onErrorContainer)
                            Text(errorMsg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                        IconButton(onClick = { bookingViewModel.clearError() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            // TARJETA DE DATOS DE PACIENTE (Cargados automáticamente de la sesión)
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Datos del Paciente (Cargados automáticamente)", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Nombre: ${state.patientName.ifBlank { currentUser?.name ?: "Paciente" }}", style = MaterialTheme.typography.bodyMedium)
                    Text("RUN: ${state.patientRun.ifBlank { currentUser?.run ?: "N/A" }} • Tel: ${state.patientPhone.ifBlank { currentUser?.phone ?: "Sin registro" }}", style = MaterialTheme.typography.bodySmall)
                    Text("Correo: ${state.patientEmail.ifBlank { currentUser?.email ?: "Sin registro" }}", style = MaterialTheme.typography.bodySmall)
                }
            }

            // 1. SELECCIÓN DE ÁREA / ESPECIALIDAD
            Text("1. Área o Especialidad Requerida", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            ExposedDropdownMenuBox(
                expanded = areaExpanded,
                onExpandedChange = { areaExpanded = !areaExpanded }
            ) {
                OutlinedTextField(
                    value = state.serviceType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Área de Atención") },
                    leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = areaExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = areaExpanded,
                    onDismissRequest = { areaExpanded = false }
                ) {
                    areas.forEach { area ->
                        DropdownMenuItem(
                            text = { Text(area) },
                            onClick = {
                                bookingViewModel.onAreaChanged(area, dbProfessionals)
                                areaExpanded = false
                            }
                        )
                    }
                }
            }

            // 2. SELECCIÓN DE PROFESIONAL DEL ÁREA (Filtrado dinámicamente)
            Text("2. Profesional del Área", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            ExposedDropdownMenuBox(
                expanded = proExpanded,
                onExpandedChange = { proExpanded = !proExpanded }
            ) {
                OutlinedTextField(
                    value = state.professionalName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Profesional Atendiente") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = proExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = proExpanded,
                    onDismissRequest = { proExpanded = false }
                ) {
                    availableProfessionals.forEach { proName ->
                        DropdownMenuItem(
                            text = { Text(proName) },
                            onClick = {
                                bookingViewModel.onProfessionalChanged(proName, dbProfessionals)
                                proExpanded = false
                            }
                        )
                    }
                }
            }

            // 3. SELECCIÓN DE FECHA
            Text("3. Fecha de Atención", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            OutlinedTextField(
                value = state.date,
                onValueChange = { },
                readOnly = true,
                label = { Text("Seleccionar Fecha en Calendario") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // 4. SELECCIÓN DE HORARIO (Bloquea/Deshabilita bloques ya reservados)
            Text("4. Horario / Módulo Disponible", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            if (state.date.isBlank()) {
                Text("⚠️ Primero seleccione una fecha para ver los horarios disponibles.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timeSlots.take(4).forEach { slot ->
                        val isBooked = bookedSlots.contains(slot)
                        FilterChip(
                            selected = state.timeSlot == slot,
                            onClick = { bookingViewModel.onTimeSlotChanged(slot, dbProfessionals) },
                            label = { Text(if (isBooked) "$slot (Ocupado)" else slot, style = MaterialTheme.typography.labelSmall) },
                            enabled = !isBooked,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timeSlots.drop(4).forEach { slot ->
                        val isBooked = bookedSlots.contains(slot)
                        FilterChip(
                            selected = state.timeSlot == slot,
                            onClick = { bookingViewModel.onTimeSlotChanged(slot, dbProfessionals) },
                            label = { Text(if (isBooked) "$slot (Ocupado)" else slot, style = MaterialTheme.typography.labelSmall) },
                            enabled = !isBooked,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 5. MODALIDAD
            Text("5. Modalidad de Atención", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = !state.isOnline,
                    onClick = { bookingViewModel.onModeChanged(false) },
                    label = { Text("Presencial (Los Vilos)") },
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = state.isOnline,
                    onClick = { bookingViewModel.onModeChanged(true) },
                    label = { Text("Online (MEDIlink)") },
                    leadingIcon = { Icon(Icons.Default.Computer, contentDescription = null) }
                )
            }

            // 6. INFORMACIÓN RELEVANTE / SÍNTOMAS PREVIOS
            Text("6. Información relevante o antecedentes", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            OutlinedTextField(
                value = state.notes,
                onValueChange = { bookingViewModel.onNotesChanged(it) },
                label = { Text("Describa motivo o síntomas previos") },
                placeholder = { Text("Escriba un breve párrafo con antecedentes importantes para el profesional...") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                minLines = 3,
                maxLines = 5,
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
                        Text("Área, Profesional, Fecha y Horario seleccionados correctamente", color = Color(0xFF2E7D32))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    bookingViewModel.submitBooking(dbProfessionals) { appointment ->
                        onBookingSuccess(appointment)
                    }
                },
                enabled = state.isFormValid && state.date.isNotBlank() && !bookedSlots.contains(state.timeSlot),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Confirmar y Guardar Cita")
            }
        }
    }

    // Componente Calendario
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            selectableDates = DateValidators.weekdayOnlyValidator
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val formattedDate = formatter.format(Date(millis))
                        bookingViewModel.onDateChanged(formattedDate, dbProfessionals)
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
object DateValidators {
    val weekdayOnlyValidator = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val date = Instant.ofEpochMilli(utcTimeMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            val isPast = date.isBefore(LocalDate.now())
            val isWeekend = date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7

            return !isPast && !isWeekend
        }
    }
}
