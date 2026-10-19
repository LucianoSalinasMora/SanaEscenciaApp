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
import com.sanaesencia.app.viewmodel.ProductViewModel
import com.sanaesencia.app.viewmodel.UserViewModel

@Composable
fun ServiciosCatalogoScreen(
    userViewModel: UserViewModel,
    productViewModel: ProductViewModel,
    onAgendar: () -> Unit
) {
    val profesionales by userViewModel.professionals.collectAsState()
    val products by productViewModel.products.collectAsState()

    // Agrupa a los profesionales por especialidad y ordena las especialidades alfabéticamente
    val grupos = profesionales
        .filter { it.specialty.isNotBlank() }
        .groupBy { it.specialty }
        .toList()
        .sortedBy { it.first }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Catálogo de Servicios",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Especialidades del centro, profesionales a cargo y modalidades de atención.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        item {
            Button(
                onClick = onAgendar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agendar una cita")
            }
        }

        if (grupos.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "Aún no hay profesionales registrados en el catálogo.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(grupos) { (especialidad, pros) ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = especialidad,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Modalidad: presencial en Los Vilos u online",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        pros.forEach { pro ->
                            Text("• ${pro.name}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        val availableProducts = products.distinctBy { it.name }.filter { it.isAvailable }
        if (availableProducts.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Productos y Kits de Bienestar",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(availableProducts) { product ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, style = MaterialTheme.typography.titleMedium)
                            Text(product.description, style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Stock disponible: ${product.stock} unidades", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                            if (product.imageUrl.isNotBlank()) {
                                Text("Imagen/Ref: ${product.imageUrl}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Precio: $${product.price.toInt()} CLP",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
