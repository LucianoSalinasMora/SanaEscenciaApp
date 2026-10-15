package com.sanaesencia.app.view



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.viewmodel.BookingViewModel
import androidx.compose.material.icons.automirrored.filled.ArrowForward
data class ServiceItem(val name: String, val category: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: BookingViewModel,
    onNavigateToBooking: () -> Unit
) {
    val services = listOf(
        ServiceItem("Psicología Infanto-Juvenil", "Salud Mental", "Evaluación y acompañamiento emocional para niños y adolescentes."),
        ServiceItem("Terapia Ocupacional", "Rehabilitación", "Integración sensorial y autonomía en actividades cotidianas."),
        ServiceItem("Fonoaudiología", "Comunicación", "Diagnóstico y tratamiento del lenguaje, habla y deglución."),
        ServiceItem("Psicopedagogía", "Aprendizaje", "Acompañamiento en procesos cognitivos y escolares."),
        ServiceItem("Nutrición Integrativa", "Bienestar", "Planes de alimentación conscientes e individualizados."),
        ServiceItem("Quiropraxia y Masajes", "Cuidado Corporal", "Alineación corporal y liberación de tensiones musculares."),
        ServiceItem("Terapia Floral y Aromaterapia", "Productos Naturales", "Línea Sanaesencia de kits, roll-ons y brumas naturales.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Centro Sanaesencia - Catálogo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Especialidades y Productos Integrativos",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(services) { service ->
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (service.category == "Productos Naturales") Icons.Default.Spa else Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = service.name, style = MaterialTheme.typography.titleMedium)
                            Text(text = service.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = service.description, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(
                            onClick = {
                                viewModel.onSpecialtySelected(service.name)
                                onNavigateToBooking()
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Agendar")
                        }
                    }
                }
            }
        }
    }
}