package com.sanaesencia.app.data


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices

class LocationHelper(context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Coordenadas Los Vilos (Caupolicán 220)
    private val losVilosLat = -31.9123
    private val losVilosLng = -71.5164

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

class NotificationHelper(private val context: Context) {
    private val channelId = "sanaesencia_alerts"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Sanaesencia Alertas"
            val descriptionText = "Recordatorios de Citas y Reenganche"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendBookingNotification(serviceName: String, date: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Cita Confirmada - Sanaesencia")
            .setContentText("Tu atención de $serviceName fue agendada para el $date.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}