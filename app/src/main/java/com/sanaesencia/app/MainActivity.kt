package com.sanaesencia.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sanaesencia.app.data.AppDatabase
import com.sanaesencia.app.data.AppointmentRepository
import com.sanaesencia.app.data.NotificationHelper
import com.sanaesencia.app.data.UserRole
import com.sanaesencia.app.ui.theme.SanaesenciaTheme
import com.sanaesencia.app.view.BookingScreen
import com.sanaesencia.app.view.LoginScreen
import com.sanaesencia.app.view.MainScreen
import com.sanaesencia.app.view.SignUpScreen
import com.sanaesencia.app.viewmodel.BookingViewModel
import com.sanaesencia.app.viewmodel.BookingViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de la base de datos Room y servicios nativos
        val database = AppDatabase.getDatabase(this)
        val repository = AppointmentRepository(database.appointmentDao())
        val notificationHelper = NotificationHelper(this)

        setContent {
            SanaesenciaTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val rootNavController = rememberNavController()

                    // Instancia compartida de BookingViewModel
                    val bookingViewModel: BookingViewModel = viewModel(
                        factory = BookingViewModelFactory(repository)
                    )

                    NavHost(
                        navController = rootNavController,
                        startDestination = Screen.Login.route
                    ) {
                        // 1. Login
                        composable(Screen.Login.route) {
                            LoginScreen(
                                onLoginSuccess = { role ->
                                    rootNavController.navigate(Screen.Main.createRoute(role.name)) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onNavigateToSignUp = {
                                    rootNavController.navigate(Screen.SignUp.route)
                                }
                            )
                        }

                        // 2. Registro de Pacientes
                        composable(Screen.SignUp.route) {
                            SignUpScreen(
                                onRegisterSuccess = { rootNavController.popBackStack() },
                                onBack = { rootNavController.popBackStack() }
                            )
                        }

                        // 3. Pantalla Principal con NavBar
                        composable(Screen.Main.route) { backStackEntry ->
                            val role = backStackEntry.arguments?.getString("role") ?: UserRole.PACIENTE.name
                            MainScreen(
                                parentNavController = rootNavController,
                                userRole = role,
                                bookingViewModel = bookingViewModel
                            )
                        }

                        // 4. Agendamiento de Citas
                        composable(Screen.Booking.route) {
                            BookingScreen(
                                viewModel = bookingViewModel,
                                onBookingSuccess = { appointment ->
                                    notificationHelper.sendBookingNotification(
                                        serviceName = appointment.serviceType,
                                        date = appointment.date
                                    )
                                    val boxMsg = appointment.boxNumber?.let { " en Box $it" } ?: ""
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Cita agendada para el ${appointment.date} ($boxMsg)",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    rootNavController.popBackStack()
                                },
                                onBack = { rootNavController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}