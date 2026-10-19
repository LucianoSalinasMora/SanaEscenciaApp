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
import com.sanaesencia.app.data.ProductRepository
import com.sanaesencia.app.data.UserRepository
import com.sanaesencia.app.data.UserRole
import com.sanaesencia.app.ui.theme.SanaesenciaTheme
import com.sanaesencia.app.view.BookingScreen
import com.sanaesencia.app.view.ForgotPasswordScreen
import com.sanaesencia.app.view.LoginScreen
import com.sanaesencia.app.view.MainScreen
import com.sanaesencia.app.view.SignUpScreen
import com.sanaesencia.app.viewmodel.BookingViewModel
import com.sanaesencia.app.viewmodel.BookingViewModelFactory
import com.sanaesencia.app.viewmodel.ProductViewModel
import com.sanaesencia.app.viewmodel.ProductViewModelFactory
import com.sanaesencia.app.viewmodel.UserViewModel
import com.sanaesencia.app.viewmodel.UserViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = AppointmentRepository(database.appointmentDao())
        val notificationHelper = NotificationHelper(this)

        // 1. Inicializamos el repositorio de usuarios
        val userRepository = UserRepository(database.userDao())

        // 2. Inicializamos el repositorio de productos
        val productRepository = ProductRepository(database.productDao())

        setContent {
            SanaesenciaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val bookingViewModel: BookingViewModel = viewModel(
                        factory = BookingViewModelFactory(repository)
                    )

                    val userViewModel: UserViewModel = viewModel(
                        factory = UserViewModelFactory(userRepository)
                    )

                    val productViewModel: ProductViewModel = viewModel(
                        factory = ProductViewModelFactory(productRepository)
                    )

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route
                    ) {

                        // --- PANTALLA DE LOGIN ---
                        composable(Screen.Login.route) {
                            LoginScreen(
                                viewModel = userViewModel,
                                onLoginSuccess = { role ->
                                    navController.navigate(Screen.Main.createRoute(role.name)) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
                            )
                        }

                        // --- PANTALLA DE REGISTRO ---
                        composable(Screen.SignUp.route) {
                            SignUpScreen(
                                viewModel = userViewModel,
                                onRegisterSuccess = {
                                    Toast.makeText(this@MainActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // --- PANTALLA DE RECUPERAR CONTRASEÑA ---
                        composable(Screen.ForgotPassword.route) {
                            ForgotPasswordScreen(
                                viewModel = userViewModel,
                                onBack = { navController.popBackStack() },
                                onPasswordResetSuccess = {
                                    Toast.makeText(this@MainActivity, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            )
                        }

                        // --- PANTALLA PRINCIPAL (Dashboards con Navbars) ---
                        composable(Screen.Main.route) { backStackEntry ->
                            val role = backStackEntry.arguments?.getString("role") ?: UserRole.PACIENTE.name

                            MainScreen(
                                parentNavController = navController,
                                userRole = role,
                                userViewModel = userViewModel,
                                bookingViewModel = bookingViewModel,
                                productViewModel = productViewModel,
                                notificationHelper = notificationHelper,
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0)
                                    }
                                }
                            )
                        }

                        // --- PANTALLA DE AGENDAR CITA ---
                        composable(Screen.Booking.route) {
                            BookingScreen(
                                userViewModel = userViewModel,
                                bookingViewModel = bookingViewModel,
                                onBookingSuccess = { appointment ->
                                    notificationHelper.sendBookingNotification(
                                        serviceName = appointment.serviceType,
                                        date = appointment.date,
                                        timeSlot = appointment.timeSlot
                                    )
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Cita registrada exitosamente",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.popBackStack()
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
