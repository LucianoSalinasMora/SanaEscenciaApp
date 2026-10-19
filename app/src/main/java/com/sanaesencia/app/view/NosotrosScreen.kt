package com.sanaesencia.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.data.AboutInfoEntity
import com.sanaesencia.app.viewmodel.AboutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NosotrosScreen(
    role: String = "PACIENTE",
    aboutViewModel: AboutViewModel
) {
    val aboutInfo by aboutViewModel.aboutInfo.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    // Reseñas de ejemplo
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nosotros",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (role == "ADMIN") {
                    Button(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Editar Información")
                    }
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(aboutInfo?.centerTitle ?: "Centro Integrativo Sanaesencia", style = MaterialTheme.typography.titleMedium)
                    Text(
                        aboutInfo?.description ?: "Salud mental infanto-juvenil, familia y bienestar integrativo.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Contacto", style = MaterialTheme.typography.titleMedium)
                    Text("Dirección: ${aboutInfo?.address ?: "Caupolicán 220, Los Vilos"}", style = MaterialTheme.typography.bodyMedium)
                    Text("Teléfono / WhatsApp: ${aboutInfo?.phone ?: "+56 9 2366 5073"}", style = MaterialTheme.typography.bodyMedium)
                    Text("Correo: ${aboutInfo?.email ?: "contacto@centrosanaesencia.cl"}", style = MaterialTheme.typography.bodyMedium)
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
                Spacer(modifier = Modifier.height(8.dp))
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

    if (showEditDialog) {
        AboutEditDialog(
            currentInfo = aboutInfo ?: AboutInfoEntity(),
            onDismiss = { showEditDialog = false },
            onSave = { updatedInfo ->
                aboutViewModel.updateAboutInfo(updatedInfo)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun AboutEditDialog(
    currentInfo: AboutInfoEntity,
    onDismiss: () -> Unit,
    onSave: (AboutInfoEntity) -> Unit
) {
    var centerTitle by remember { mutableStateOf(currentInfo.centerTitle) }
    var description by remember { mutableStateOf(currentInfo.description) }
    var address by remember { mutableStateOf(currentInfo.address) }
    var phone by remember { mutableStateOf(currentInfo.phone) }
    var email by remember { mutableStateOf(currentInfo.email) }

    val isFormValid = centerTitle.isNotBlank() && description.isNotBlank() && address.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Información 'Nosotros'") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = centerTitle,
                    onValueChange = { centerTitle = it },
                    label = { Text("Título del Centro") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción / Quiénes Somos") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono / WhatsApp") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        AboutInfoEntity(
                            id = 1,
                            centerTitle = centerTitle.trim(),
                            description = description.trim(),
                            address = address.trim(),
                            phone = phone.trim(),
                            email = email.trim()
                        )
                    )
                },
                enabled = isFormValid
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
