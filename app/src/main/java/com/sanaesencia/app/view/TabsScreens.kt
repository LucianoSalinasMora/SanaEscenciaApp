package com.sanaesencia.app.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.sanaesencia.app.R
import com.sanaesencia.app.data.AppointmentEntity
import com.sanaesencia.app.data.NotificationHelper
import com.sanaesencia.app.data.UserRole
import com.sanaesencia.app.data.Validators
import com.sanaesencia.app.data.mockProfessionals
import com.sanaesencia.app.viewmodel.BookingViewModel
import com.sanaesencia.app.viewmodel.UserViewModel

@Composable
fun DashboardScreen(
    viewModel: BookingViewModel,
    notificationHelper: NotificationHelper,
    role: String = "PACIENTE"
) {
    val context = LocalContext.current
    val appointments by viewModel.appointments.collectAsState()
    val isPaciente = role == UserRole.PACIENTE.name

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
                    Text("¡Bienvenido a Centro Sanaesencia!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Explora nuestras especialidades, agenda horas médicas presenciales en Los Vilos u online, e infórmate de nuestras novedades.")
                }
            }
        }

        // --- SECCIÓN: ALERTAS Y NOTIFICACIONES (SÓLO VISIBLE PARA PACIENTES) ---
        if (isPaciente) {
            // 1. Alertas de Novedades y Promociones
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Campaign,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Novedades y Promociones Activas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Text(
                            text = "¡Aprovecha 20% de descuento en Terapias de Sanación este mes! Código promocional: SANA20",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Button(
                            onClick = {
                                notificationHelper.sendPromoNotification(
                                    title = "¡Promoción Especial Sanaesencia!",
                                    description = "Obtén 20% de descuento en Terapias Complementarias presenciales en Los Vilos.",
                                    promoCode = "SANA20"
                                )
                                Toast.makeText(context, "Alerta de novedad enviada a notificaciones push", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Activar Alerta de Promoción")
                        }
                    }
                }
            }

            // 2. Alertas de Seguimiento Cita / Controles
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Alertas de Seguimiento y Controles",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }

                        Text(
                            text = "Recibe avisos periódicos de seguimiento para tus evaluaciones kinesiológicas y controles psicológicos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )

                        OutlinedButton(
                            onClick = {
                                val lastAppointment = appointments.firstOrNull()
                                val service = lastAppointment?.serviceType ?: "Medicina Holística"
                                notificationHelper.sendFollowUpNotification(
                                    patientName = "Estimado Paciente",
                                    serviceName = service,
                                    customMessage = "Recordatorio Sanaesencia: Ha pasado tiempo desde tu atención en $service. ¿Deseas agendar un control de seguimiento?"
                                )
                                Toast.makeText(context, "Alerta de seguimiento generada", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Solicitar Recordatorio de Seguimiento")
                        }
                    }
                }
            }
        }

        item {
            Text(
                if (isPaciente) "Tus Próximas Citas Agendadas" else "Resumen de Citas Médicas",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (appointments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = if (isPaciente) "No tienes citas agendadas aún. Presiona el botón '+' para reservar una hora." else "No hay citas registradas en el sistema.",
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
fun ProfileScreen(
    userViewModel: UserViewModel,
    bookingViewModel: BookingViewModel
) {
    val context = LocalContext.current
    val currentUser by userViewModel.currentUser.collectAsState()
    val appointments by bookingViewModel.appointments.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var name by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
    var email by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }
    var phone by remember(currentUser) { mutableStateOf(currentUser?.phone ?: "") }

    val isNameValid = remember(name) { name.isBlank() || Validators.isValidName(name) }
    val isEmailValid = remember(email) { email.isBlank() || Validators.isValidEmail(email) }
    val isPhoneValid = remember(phone) { phone.isBlank() || Validators.isValidPhone(phone) }

    val isFormValid = name.isNotBlank() && isNameValid &&
            email.isNotBlank() && isEmailValid &&
            phone.isNotBlank() && isPhoneValid

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Perfil de Usuario", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(currentUser?.name ?: "Usuario", style = MaterialTheme.typography.titleMedium)
                                Text("RUN: ${currentUser?.run ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                                Text("Rol: ${currentUser?.role?.name ?: "PACIENTE"}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        IconButton(onClick = { isEditing = !isEditing }) {
                            Icon(if (isEditing) Icons.Default.Save else Icons.Default.Edit, contentDescription = "Editar Perfil")
                        }
                    }

                    HorizontalDivider()

                    if (!isEditing) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Correo Electrónico: ${currentUser?.email?.ifBlank { "No registrado" }}", style = MaterialTheme.typography.bodyMedium)
                            Text("Teléfono / WhatsApp: ${currentUser?.phone?.ifBlank { "No registrado" }}", style = MaterialTheme.typography.bodyMedium)
                            if (!currentUser?.specialty.isNullBraking()) {
                                Text("Especialidad: ${currentUser?.specialty}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    } else {
                        Text("Editar Datos Personales", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nombre Completo") },
                            isError = !isNameValid,
                            supportingText = { if (!isNameValid) Text("Mínimo 3 caracteres", color = MaterialTheme.colorScheme.error) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Correo Electrónico") },
                            isError = !isEmailValid,
                            supportingText = { if (!isEmailValid) Text("Formato correo inválido (ej. usuario@dominio.com)", color = MaterialTheme.colorScheme.error) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Teléfono de Contacto") },
                            isError = !isPhoneValid,
                            supportingText = { if (!isPhoneValid) Text("Número inválido (8 a 12 dígitos)", color = MaterialTheme.colorScheme.error) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                userViewModel.updateUserProfile(name.trim(), email.trim(), phone.trim()) {
                                    isEditing = false
                                    Toast.makeText(context, "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = isFormValid,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar Cambios")
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

private fun String?.isNullBraking(): Boolean = this.isNullOrBlank()

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

            if (appointment.patientNotes.isNotBlank()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nota/Motivo: ${appointment.patientNotes}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun CatalogTabScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Catálogo de Servicios", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun ProfessionalsScreen() {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(mockProfessionals) { pro ->
            Text(pro.name)
        }
    }
}

@Composable
fun AboutScreen(role: String = "PACIENTE") {
    val context = LocalContext.current
    val mapsUrl = "https://maps.app.goo.gl/3hFvN2dRbjQkc5vq5"

    fun openGoogleMaps() {
        val intent = Intent(Intent.ACTION_VIEW, mapsUrl.toUri())
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "No se pudo abrir la aplicación de mapas", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Nosotros",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo Centro Sanaesencia",
                        modifier = Modifier.height(42.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Centro Médico y Holístico Sanaesencia",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Somos un centro interdisciplinario dedicado al cuidado de la salud integral, combinando medicina biorreguladora, kinesiología, psicología, aromaterapia y terapias complementarias presenciales en Los Vilos y atención online.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // --- TARJETA DE UBICACIÓN Y MAPA INTERACTIVO ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openGoogleMaps() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_map_preview),
                            contentDescription = "Mapa Ubicación Sanaesencia Caupolicán 220",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                            shape = CircleShape,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = "Abrir Google Maps",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Abrir Maps",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ubicación Presencial",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Caupolicán 220, Los Vilos, Región de Coquimbo, Chile",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "Toca la imagen o presiona el botón para abrir la ruta en Google Maps.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = { openGoogleMaps() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ver Ubicación en Google Maps")
                        }
                    }
                }
            }
        }

        // --- CONTACTO Y HORARIOS ---
        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Información de Atención",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Contacto / WhatsApp: +56 9 1234 5678", style = MaterialTheme.typography.bodyMedium)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Horario: Lunes a Viernes 09:00 - 19:00 hrs", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
