package com.sanaesencia.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.viewmodel.BookingViewModel

@Composable
fun ManageScheduleScreen(bookingViewModel: BookingViewModel) {
    val citas by bookingViewModel.appointments.collectAsState()

    val ordenadas = citas.sortedWith(compareBy({ it.date }, { it.timeSlot }))
    val presenciales = citas.count { !it.isOnline }
    val online = citas.count { it.isOnline }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Gestión de Citas",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Programación general y ocupación de los 8 boxes de atención.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Resumen general
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResumenCard("Total", citas.size.toString(), Modifier.weight(1f))
                ResumenCard("Presenciales", presenciales.toString(), Modifier.weight(1f))
                ResumenCard("Online", online.toString(), Modifier.weight(1f))
            }
        }

        // Ocupación de los 8 boxes (solo citas presenciales tienen box)
        item {
            Text("Ocupación por box", style = MaterialTheme.typography.titleMedium)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..4).forEach { box ->
                        BoxCard(box, citas.count { it.boxNumber == box }, Modifier.weight(1f))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (5..8).forEach { box ->
                        BoxCard(box, citas.count { it.boxNumber == box }, Modifier.weight(1f))
                    }
                }
            }
        }

        // Lista completa de citas
        item {
            Text("Todas las citas", style = MaterialTheme.typography.titleMedium)
        }

        if (ordenadas.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "Aún no hay citas agendadas en el centro.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(ordenadas) { cita ->
                AppointmentCard(
                    appointment = cita,
                    onCancel = { id -> bookingViewModel.cancelAppointment(id) }
                )
            }
        }
    }
}

@Composable
private fun ResumenCard(titulo: String, valor: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = titulo, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun BoxCard(box: Int, cantidad: Int, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Box $box", style = MaterialTheme.typography.labelMedium)
            Text(
                text = cantidad.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = if (cantidad > 0) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline
            )
            Text(
                text = if (cantidad == 1) "cita" else "citas",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
