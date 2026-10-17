package com.sanaesencia.app.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.data.NotificationHelper
import com.sanaesencia.app.viewmodel.BookingViewModel

@Composable
fun MyPatientsScreen(
    bookingViewModel: BookingViewModel,
    notificationHelper: NotificationHelper
) {
    val context = LocalContext.current
    val appointments by bookingViewModel.appointments.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Panel de Pacientes Agendados",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Revise el contexto y notas previas ingresadas por sus pacientes y envíe alertas de seguimiento pos-atención.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (appointments.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "No hay pacientes agendados actualmente.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(appointments) { appointment ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = appointment.patientName.ifBlank { "Paciente Registrado" },
                                    style = MaterialTheme.typography.titleMedium
                                )
                                AssistChip(
                                    onClick = { },
                                    label = { Text(appointment.status) }
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Fecha: ${appointment.date} • ${appointment.timeSlot}", style = MaterialTheme.typography.bodyMedium)
                            }

                            Text(
                                text = "Servicio: ${appointment.serviceType}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Teléfono: ${appointment.patientPhone}", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.width(16.dp))
                                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Email: ${appointment.patientEmail}", style = MaterialTheme.typography.bodySmall)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // NOTA / ANTECEDENTES PREVIOS INGRESADOS POR EL PACIENTE
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Notes,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(20.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Información/Síntomas previos ingresados por el paciente:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        text = if (appointment.patientNotes.isNotBlank()) appointment.patientNotes else "El paciente no ingresó notas adicionales.",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedButton(
                                onClick = {
                                    val patient = appointment.patientName.ifBlank { "Estimado Paciente" }
                                    notificationHelper.sendFollowUpNotification(
                                        patientName = patient,
                                        serviceName = appointment.serviceType,
                                        customMessage = "Hola $patient, tu profesional tratante ha enviado una recomendación de seguimiento para tu atención de ${appointment.serviceType}."
                                    )
                                    Toast.makeText(context, "Alerta de seguimiento enviada a $patient", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Enviar Alerta de Seguimiento al Paciente")
                            }
                        }
                    }
                }
            }
        }
    }
}
