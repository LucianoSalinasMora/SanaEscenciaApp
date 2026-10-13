package com.sanaesencia.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanaesencia.app.data.AppDatabase
import com.sanaesencia.app.data.AppointmentRepository
import com.sanaesencia.app.data.NotificationHelper
import com.sanaesencia.app.ui.theme.SanaesenciaTheme
import com.sanaesencia.app.view.BookingScreen
import com.sanaesencia.app.view.CatalogScreen
import com.sanaesencia.app.viewmodel.BookingViewModel
import com.sanaesencia.app.viewmodel.BookingViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = AppointmentRepository(database.appointmentDao())
        val notificationHelper = NotificationHelper(this)

        setContent {
            SanaesenciaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val bookingViewModel: BookingViewModel = viewModel(
                        factory = BookingViewModelFactory(repository)
                    )

                    var currentScreen by remember { mutableStateOf("catalog") }

                    when (currentScreen) {
                        "catalog" -> {
                            CatalogScreen(
                                viewModel = bookingViewModel,
                                onNavigateToBooking = { currentScreen = "booking" }
                            )
                        }
                        "booking" -> {
                            BookingScreen(
                                viewModel = bookingViewModel,
                                onBookingSuccess = { appointment ->
                                    notificationHelper.sendBookingNotification(
                                        serviceName = appointment.serviceType,
                                        date = appointment.date
                                    )
                                    val boxMsg = appointment.boxNumber?.let { " en Box $it" } ?: ""
                                    Toast.makeText(
                                        this,
                                        "Cita registrada exitosamente$boxMsg",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    currentScreen = "catalog"
                                },
                                onBack = { currentScreen = "catalog" }
                            )
                        }
                    }
                }
            }
        }
    }
}