package com.sanaesencia.app.data

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.sanaesencia.app.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// ==========================================
// Modelos y Categorías para Notificaciones
// ==========================================
enum class NotificationCategory(val title: String) {
    CONFIRMATION("Confirmación de Citas"),
    SEGUIMIENTO("Alertas de Seguimiento"),
    NOVEDAD("Novedades y Promociones")
}

data class AppNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val category: NotificationCategory,
    val timestamp: String = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault()).format(Date()),
    val isRead: Boolean = false,
    val promoCode: String? = null
)

// ==========================================
// Ayudante de Geolocalización y Proximidad (GPS)
// ==========================================
class LocationHelper(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Coordenadas Los Vilos (Caupolicán 220)
    private val losVilosLat = -31.9123
    private val losVilosLng = -71.5164

    // ------------------------------------------
    // Comprobación de Permisos de Ubicación
    // ------------------------------------------
    /**
     * Verifica si la aplicación cuenta con permisos de ubicación (Fina o Coarse).
     */
    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    // ------------------------------------------
    // Cálculo de Distancia y Proximidad al Centro
    // ------------------------------------------
    /**
     * Consulta el GPS del dispositivo para calcular la distancia en kilómetros
     * a la sede presencial en Los Vilos. Determina si el paciente se encuentra cerca (< 20 km)
     * para recomendar atención presencial u online.
     */
    @SuppressLint("MissingPermission")
    fun checkProximityToCenter(onResult: (isNear: Boolean, distanceKm: Float) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val centerLocation = Location("Sanaesencia").apply {
                    latitude = losVilosLat
                    longitude = losVilosLng
                }
                val distanceInMeters = location.distanceTo(centerLocation)
                val distanceInKm = distanceInMeters / 1000
                onResult(distanceInKm < 20.0, distanceInKm)
            } else {
                onResult(false, 0f)
            }
        }.addOnFailureListener {
            onResult(false, 0f)
        }
    }
}

// ==========================================
// Gestor de Notificaciones Push y Alertas del Sistema
// ==========================================
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_CONFIRMATIONS = "sanaesencia_confirmations"
        const val CHANNEL_SEGUIMIENTO = "sanaesencia_seguimiento"
        const val CHANNEL_NOVEDADES = "sanaesencia_novedades"

        // Lista global estática reactiva para historial de notificaciones in-app
        private val _notifications = MutableStateFlow<List<AppNotification>>(
            listOf(
                AppNotification(
                    title = "¡Bienvenido a Sanaesencia!",
                    message = "Recibe notificaciones sobre tus horas agendadas, alertas de seguimiento y promociones.",
                    category = NotificationCategory.NOVEDAD
                ),
                AppNotification(
                    title = "Promo Salud Holistic",
                    message = "20% de descuento en Terapias Complementarias este mes con el código SANA20.",
                    category = NotificationCategory.NOVEDAD,
                    promoCode = "SANA20"
                )
            )
        )
        val notificationsState: StateFlow<List<AppNotification>> = _notifications.asStateFlow()
    }

    init {
        createNotificationChannels()
    }

    // ------------------------------------------
    // Creación y Configuración de Canales de Notificación (Android 8.0+)
    // ------------------------------------------
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

            val channelConfirm = NotificationChannel(
                CHANNEL_CONFIRMATIONS,
                "Confirmación de Citas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones instantáneas al agendar o modificar citas médicas"
            }

            val channelSeguimiento = NotificationChannel(
                CHANNEL_SEGUIMIENTO,
                "Alertas de Seguimiento",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recordatorios de controles, seguimiento pos-atención y reenganche de pacientes"
            }

            val channelNovedades = NotificationChannel(
                CHANNEL_NOVEDADES,
                "Novedades y Promociones",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alertas de ofertas, nuevos profesionales, descuentos y eventos"
            }

            notificationManager?.createNotificationChannel(channelConfirm)
            notificationManager?.createNotificationChannel(channelSeguimiento)
            notificationManager?.createNotificationChannel(channelNovedades)
        }
    }

    // ------------------------------------------
    // Verificación de Permisos de Notificación (Android 13+)
    // ------------------------------------------
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // ------------------------------------------
    // Construcción de PendingIntent para Navegación al Pulsar
    // ------------------------------------------
    private fun getIntentPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // ------------------------------------------
    // Envío: Notificación Push de Confirmación de Cita
    // ------------------------------------------
    fun sendBookingNotification(serviceName: String, date: String, timeSlot: String = "") {
        val title = "Cita Confirmada - Sanaesencia"
        val message = "Tu hora para $serviceName ha sido agendada con éxito para el $date${if (timeSlot.isNotBlank()) " a las $timeSlot" else ""}."

        addNotificationToState(title, message, NotificationCategory.CONFIRMATION)

        val builder = NotificationCompat.Builder(context, CHANNEL_CONFIRMATIONS)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(getIntentPendingIntent())

        notifySystem(builder.build())
    }

    // ------------------------------------------
    // Envío: Alertas de Seguimiento (Controles y Reenganche)
    // ------------------------------------------
    fun sendFollowUpNotification(
        patientName: String,
        serviceName: String,
        customMessage: String? = null
    ) {
        val title = "Alerta de Seguimiento Médico"
        val message = customMessage ?: "Hola $patientName, ¿cómo te has sentido después de tu atención de $serviceName? Recuerda agendar tu control de seguimiento."

        addNotificationToState(title, message, NotificationCategory.SEGUIMIENTO)

        val builder = NotificationCompat.Builder(context, CHANNEL_SEGUIMIENTO)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(getIntentPendingIntent())

        notifySystem(builder.build())
    }

    // ------------------------------------------
    // Envío: Alertas de Novedades y Promociones
    // ------------------------------------------
    fun sendPromoNotification(
        title: String,
        description: String,
        promoCode: String? = null
    ) {
        val fullMessage = if (!promoCode.isNullOrBlank()) {
            "$description - Usa el código de descuento: $promoCode"
        } else {
            description
        }

        addNotificationToState(title, fullMessage, NotificationCategory.NOVEDAD, promoCode)

        val builder = NotificationCompat.Builder(context, CHANNEL_NOVEDADES)
            .setSmallIcon(R.drawable.ic_menu_compass)
            .setContentTitle(title)
            .setContentText(fullMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(fullMessage))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(getIntentPendingIntent())

        notifySystem(builder.build())
    }

    // ------------------------------------------
    // Gestión del Historial de Notificaciones en Memoria
    // ------------------------------------------
    private fun addNotificationToState(
        title: String,
        message: String,
        category: NotificationCategory,
        promoCode: String? = null
    ) {
        val newNotification = AppNotification(
            title = title,
            message = message,
            category = category,
            promoCode = promoCode
        )
        _notifications.value = listOf(newNotification) + _notifications.value
    }

    private fun notifySystem(notification: Notification) {
        if (hasNotificationPermission()) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
        }
    }

    fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }
}
