package com.sanaesencia.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.data.AppointmentEntity
import com.sanaesencia.app.data.mockProfessionals
import com.sanaesencia.app.viewmodel.BookingViewModel

@Composable
fun DashboardScreen(viewModel: BookingViewModel) {
    val appointments by viewModel.appointments.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Inicio y Novedades", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("¡Bienvenido a Centro Sanaesencia!", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Explora nuestras especialidades, agenda horas médicas presenciales en Los Vilos u online, e infórmate de nuestras novedades.")
                }
            }
        }

        item {
            Text("Tus Próximas Citas Agendadas", style = MaterialTheme.typography.titleLarge)
        }

        if (appointments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "No tienes citas agendadas aún. Presiona el botón '+' para reservar una hora.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(appointments) { appointment ->
                AppointmentCard(appointment)
            }
        }
    }
}

@Composable
fun ProfileScreen(viewModel: BookingViewModel) {
    val appointments by viewModel.appointments.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Perfil del Paciente", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Juan Pérez", style = MaterialTheme.typography.titleMedium)
                            Text("juan.perez@email.com • +56912345678", style = MaterialTheme.typography.bodySmall)
                            Text("Rol: Paciente", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }

        item {
            Text("Historial de Citas Registradas", style = MaterialTheme.typography.titleLarge)
        }

        if (appointments.isEmpty()) {
            item {
                Text("No hay registros de citas guardadas.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            }
        } else {
            items(appointments) { appointment ->
                AppointmentCard(appointment)
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: AppointmentEntity) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.serviceType,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                AssistChip(
                    onClick = { },
                    label = { Text(appointment.status) },
                    leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Atiende: ${appointment.professionalName}", style = MaterialTheme.typography.bodyMedium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Fecha: ${appointment.date}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Hora: ${appointment.timeSlot}", style = MaterialTheme.typography.bodyMedium)
            }

            Text(
                text = if (appointment.isOnline) "Modalidad: Online (MEDIlink)" else "Modalidad: Presencial (Caupolicán 220, Los Vilos ${appointment.boxNumber?.let { "- Box $it" } ?: ""})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

// Mantenemos las vistas estáticas del resto de pestañas...
@Composable fun CatalogTabScreen() { Column(modifier = Modifier.fillMaxSize().padding(16.dp)) { Text("Catálogo de Servicios", style = MaterialTheme.typography.headlineMedium) } }
@Composable fun ProfessionalsScreen() { LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) { items(mockProfessionals) { Text(it.name) } } }
@Composable fun AboutScreen() { Column(modifier = Modifier.fillMaxSize().padding(16.dp)) { Text("Nosotros", style = MaterialTheme.typography.headlineMedium) } }