package com.sanaesencia.app.view

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.R
import com.sanaesencia.app.data.ContactHelper
import com.sanaesencia.app.data.LocationHelper
import com.sanaesencia.app.data.NosotrosInfo
import com.sanaesencia.app.data.NosotrosManager
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NosotrosScreen(role: String = "PACIENTE") {
    val context = LocalContext.current
    val nosotrosInfo by NosotrosManager.infoState.collectAsState()

    // Integración de Recurso Nativo: GPS (LocationHelper)
    val locationHelper = remember { LocationHelper(context) }
    var isCheckingGps by remember { mutableStateOf(false) }
    var gpsDistanceKm by remember { mutableStateOf<Float?>(null) }
    var gpsIsNear by remember { mutableStateOf<Boolean?>(null) }
    var gpsErrorMessage by remember { mutableStateOf<String?>(null) }
    var hasLocationPermission by remember { mutableStateOf(locationHelper.hasLocationPermission()) }

    // Launcher para solicitar permisos de GPS en tiempo de ejecución
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            hasLocationPermission = fineGranted || coarseGranted
            if (hasLocationPermission) {
                isCheckingGps = true
                gpsErrorMessage = null
                locationHelper.checkProximityToCenter { isNear, dist, err ->
                    isCheckingGps = false
                    gpsIsNear = isNear
                    gpsDistanceKm = dist
                    gpsErrorMessage = err
                }
            } else {
                isCheckingGps = false
                gpsErrorMessage = "Para calcular la distancia y orientar la llegada a la sede presencial, concede los permisos de ubicación."
            }
        }
    )

    fun consultarGps() {
        if (locationHelper.hasLocationPermission()) {
            isCheckingGps = true
            gpsErrorMessage = null
            locationHelper.checkProximityToCenter { isNear, dist, err ->
                isCheckingGps = false
                gpsIsNear = isNear
                gpsDistanceKm = dist
                gpsErrorMessage = err
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        NosotrosManager.init(context)
    }

    var mostrarDialogoEditar by remember { mutableStateOf(false) }

    // Reseñas de ejemplo (ficticias, solo en memoria). El admin puede eliminarlas.
    val resenas = remember {
        mutableStateListOf(
            "Paciente 1" to "Excelente atención y un espacio muy acogedor.",
            "Paciente 2" to "Pude agendar mi hora online sin complicaciones.",
            "Paciente 3" to "Los profesionales explican todo con mucha claridad."
        )
    }

    val especialidades = listOf(
        "Psicología Infanto-Juvenil",
        "Psicología Adolescentes y Adultos",
        "Terapia Ocupacional",
        "Fonoaudiología",
        "Psicopedagogía",
        "Nutrición Integrativa",
        "Matronería y Salud Femenina",
        "Quiropraxia y Masajes",
        "Evaluaciones del Neurodesarrollo",
        "Medicina General y Salud Mental",
        "Terapia Floral y Aromaterapia",
        "Acupuntura"
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
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                if (role == "ADMIN") {
                    Button(onClick = { mostrarDialogoEditar = true }) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Editar Información")
                    }
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(nosotrosInfo.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        nosotrosInfo.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Contacto y Horarios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Dirección: ${nosotrosInfo.address}", style = MaterialTheme.typography.bodyMedium)
                    Text("Teléfono / WhatsApp: ${nosotrosInfo.phone}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text("Correo: ${nosotrosInfo.email}", style = MaterialTheme.typography.bodyMedium)
                    Text("Horario de Atención: ${nosotrosInfo.schedule}", style = MaterialTheme.typography.bodyMedium)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                ContactHelper.openWhatsApp(
                                    context = context,
                                    rawPhone = nosotrosInfo.phone,
                                    message = "Hola Centro Sanaesencia, me gustaría solicitar información."
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "WhatsApp", tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Chatear WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                ContactHelper.addContact(
                                    context = context,
                                    name = nosotrosInfo.title,
                                    phone = nosotrosInfo.phone,
                                    email = nosotrosInfo.email,
                                    address = nosotrosInfo.address
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guardar Contacto")
                        }
                    }
                }
            }
        }

        // --- TARJETA DE LOCALIZACIÓN GPS Y ORIENTACIÓN PRESENCIAL (RECURSO NATIVO) ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { locationHelper.openDirectionsToCenter(context) }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_map_preview),
                            contentDescription = "Mapa Sede Los Vilos",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f),
                            shape = CircleShape,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = "Abrir Google Maps",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ver en Maps",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sede Presencial y Geolocalización (GPS)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Caupolicán 220, Los Vilos • 8 boxes de atención integral",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "Utiliza el GPS de tu dispositivo para orientar tu llegada a la sede o comprobar si te encuentras en el rango recomendado para atenciones presenciales u online.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Botones para consultar GPS y abrir Maps
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { consultarGps() },
                                enabled = !isCheckingGps,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isCheckingGps) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Calculando...", style = MaterialTheme.typography.labelMedium)
                                } else {
                                    Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Calcular GPS", style = MaterialTheme.typography.labelMedium)
                                }
                            }

                            OutlinedButton(
                                onClick = { locationHelper.openDirectionsToCenter(context) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cómo Llegar", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        // Resultado animado del cálculo de proximidad
                        AnimatedVisibility(
                            visible = gpsDistanceKm != null || gpsErrorMessage != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            if (gpsErrorMessage != null) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = gpsErrorMessage ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            } else if (gpsDistanceKm != null) {
                                val isNear = gpsIsNear == true
                                val cardBg = if (isNear) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                val iconTint = if (isNear) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = cardBg),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (isNear) Icons.Default.CheckCircle else Icons.Default.Info,
                                                contentDescription = null,
                                                tint = iconTint,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Distancia a la sede: ${String.format(Locale.getDefault(), "%.1f", gpsDistanceKm)} km",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = iconTint
                                            )
                                        }
                                        Text(
                                            text = if (isNear) {
                                                "¡Estás en la zona de Los Vilos! Te encuentras a menos de 20 km. Tu mejor opción es atención presencial en nuestros 8 boxes."
                                            } else {
                                                "Te encuentras a más de 20 km de Los Vilos. Puedes optar por atención presencial si tienes transporte, o elegir atención Online vía MEDIlink."
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nuestras Especialidades", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                    "Reseñas publicadas por pacientes (puedes administrarlas o eliminarlas).",
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
                                Text(resena.first, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
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

    // Modal de Edición del apartado Nosotros para el ADMIN
    if (mostrarDialogoEditar && role == "ADMIN") {
        var titulo by remember { mutableStateOf(nosotrosInfo.title) }
        var descripcion by remember { mutableStateOf(nosotrosInfo.description) }
        var direccion by remember { mutableStateOf(nosotrosInfo.address) }
        var telefono by remember { mutableStateOf(nosotrosInfo.phone) }
        var correo by remember { mutableStateOf(nosotrosInfo.email) }
        var horario by remember { mutableStateOf(nosotrosInfo.schedule) }

        AlertDialog(
            onDismissRequest = { mostrarDialogoEditar = false },
            title = { Text("Editar Apartado Nosotros") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it.replace("\n", "") },
                        label = { Text("Nombre del Centro") },
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it.replace("\n", "") },
                        label = { Text("Descripción / Misión") },
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it.replace("\n", "") },
                        label = { Text("Dirección") },
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it.replace("\n", "") },
                        label = { Text("Teléfono / WhatsApp") },
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it.replace("\n", "") },
                        label = { Text("Correo Electrónico") },
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = horario,
                        onValueChange = { horario = it.replace("\n", "") },
                        label = { Text("Horario de Atención") },
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = NosotrosInfo(
                            title = titulo.trim(),
                            description = descripcion.trim(),
                            address = direccion.trim(),
                            phone = telefono.trim(),
                            email = correo.trim(),
                            schedule = horario.trim()
                        )
                        NosotrosManager.saveInfo(context, updated)
                        mostrarDialogoEditar = false
                    },
                    enabled = titulo.isNotBlank() && telefono.isNotBlank()
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEditar = false }) { Text("Cancelar") }
            }
        )
    }
}
