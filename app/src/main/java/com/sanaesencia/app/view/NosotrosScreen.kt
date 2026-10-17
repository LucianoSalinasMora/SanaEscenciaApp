package com.sanaesencia.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NosotrosScreen(role: String = "PACIENTE") {
    // Reseñas de ejemplo (ficticias, solo en memoria). El admin puede eliminarlas.
    val resenas = remember {
        mutableStateListOf(
            "Paciente 1" to "Excelente atención y un espacio muy acogedor.",
            "Paciente 2" to "Pude agendar mi hora online sin complicaciones.",
            "Paciente 3" to "Los profesionales explican todo con mucha claridad."
        )
    }

    val especialidades = listOf(
        "Psicología",
        "Terapia Ocupacional",
        "Fonoaudiología",
        "Psicopedagogía",
        "Nutrición",
        "Matronería",
        "Quiropraxia y Masajes",
        "Evaluaciones del Neurodesarrollo",
        "Terapia Floral y Aromaterapia"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Nosotros",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Centro Integrativo Sanaesencia", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Salud mental infanto-juvenil, familia y bienestar integrativo. " +
                                "Atendemos de forma presencial en nuestros 8 boxes de Los Vilos y online para todo Chile y el extranjero.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Contacto", style = MaterialTheme.typography.titleMedium)
                    Text("Dirección: Caupolicán 220, Los Vilos", style = MaterialTheme.typography.bodyMedium)
                    Text("Teléfono / WhatsApp: +56 9 2366 5073", style = MaterialTheme.typography.bodyMedium)
                    Text("Correo: contacto@centrosanaesencia.cl", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nuestras especialidades", style = MaterialTheme.typography.titleMedium)
                    especialidades.forEach { esp ->
                        Text("• $esp", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // Solo el administrador gestiona las reseñas
        if (role == "ADMIN") {
            item {
                Text("Gestión de Reseñas", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Reseñas de pacientes (datos de ejemplo).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            if (resenas.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            "No hay reseñas para mostrar.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                itemsIndexed(resenas) { index, resena ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(resena.first, style = MaterialTheme.typography.titleSmall)
                                Text(resena.second, style = MaterialTheme.typography.bodyMedium)
                            }
                            IconButton(onClick = { resenas.removeAt(index) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar reseña",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
