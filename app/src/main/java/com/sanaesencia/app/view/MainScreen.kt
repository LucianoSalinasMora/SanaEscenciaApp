package com.sanaesencia.app.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sanaesencia.app.R
import com.sanaesencia.app.Screen
import com.sanaesencia.app.data.UserRole
import com.sanaesencia.app.viewmodel.BookingViewModel
import com.sanaesencia.app.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    parentNavController: NavHostController,
    userRole: String, // Recibe "PACIENTE", "PROFESIONAL" o "ADMIN"
    userViewModel: UserViewModel,
    bookingViewModel: BookingViewModel,
    onLogout: () -> Unit // Acción para cerrar sesión
) {
    val bottomNavController = rememberNavController()
    var mostrarDialogoLogout by remember { mutableStateOf(false) }

    // 1. Configuración de TABS según el Rol
    val navItems = when (userRole) {
        UserRole.ADMIN.name -> listOf(
            Screen.Inicio,             // Dashboard y Proyecciones
            Screen.ManageProducts,     // Gestión de Productos y Novedades
            Screen.ManageSchedule,     // Gestión de Turnos Global
            Screen.AdminProfessionals, // Editar Profesionales
            Screen.About               // Gestión de Reseñas
        )
        UserRole.PROFESIONAL.name -> listOf(
            Screen.Inicio,             // Turnos del día
            Screen.MyPatients,         // Seguimiento de Pacientes
            Screen.MySchedule,         // Turnos asignados
            Screen.Profile             // Datos del profesional
        )
        else -> listOf( // PACIENTE por defecto
            Screen.Inicio,
            Screen.Catalog,
            Screen.About,
            Screen.Profile
        )
    }

    if (mostrarDialogoLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLogout = false },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoLogout = false
                    userViewModel.logout()
                    onLogout()
                }) {
                    Text("Sí, cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLogout = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        // 2. TopAppBar global con Isotipo/Logo, Campana de Notificaciones y Cierre de Sesión
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo Sanaesencia",
                        modifier = Modifier.height(36.dp)
                    )
                },
                actions = {
                    IconButton(onClick = { /* Lógica para notificaciones push */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                    }
                    IconButton(onClick = { mostrarDialogoLogout = true }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        ),
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if (userRole == UserRole.PACIENTE.name) {
                FloatingActionButton(
                    onClick = { parentNavController.navigate(Screen.Booking.route) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agendar Cita")
                }
            } else if (userRole == UserRole.PROFESIONAL.name) {
                FloatingActionButton(
                    onClick = { /* Agendar a paciente asignado */ },
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agendar a Paciente")
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Inicio.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Vistas compartidas
            composable(Screen.Inicio.route) { DashboardScreen(viewModel = bookingViewModel, userViewModel = userViewModel, role = userRole) }
            composable(Screen.Profile.route) { ProfileScreen(userViewModel = userViewModel, bookingViewModel = bookingViewModel) }
            composable(Screen.About.route) { NosotrosScreen(role = userRole) }

            // Vistas de Paciente
            composable(Screen.Catalog.route) {
                ServiciosCatalogoScreen(
                    userViewModel = userViewModel,
                    onAgendar = { parentNavController.navigate(Screen.Booking.route) }
                )
            }

            // Vistas de Profesional
            composable(Screen.MyPatients.route) { MyPatientsScreen(bookingViewModel = bookingViewModel, userViewModel = userViewModel) }
            composable(Screen.MySchedule.route) { MyScheduleScreen(bookingViewModel = bookingViewModel, userViewModel = userViewModel) }

            // Vistas de Administrador
            composable(Screen.ManageProducts.route) { ManageProductsScreen() }
            composable(Screen.ManageSchedule.route) { ManageScheduleScreen(bookingViewModel = bookingViewModel) }
            composable(Screen.AdminProfessionals.route) { AdminProfessionalsScreen(viewModel = userViewModel) }
        }
    }
}
