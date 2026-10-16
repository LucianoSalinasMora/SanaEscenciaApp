package com.sanaesencia.app.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sanaesencia.app.Screen
import com.sanaesencia.app.data.UserRole
import com.sanaesencia.app.viewmodel.BookingViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sanaesencia.app.R
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    parentNavController: NavHostController,
    userRole: String, // Recibe "PACIENTE", "PROFESIONAL" o "ADMIN"
    bookingViewModel: BookingViewModel,
    onLogout: () -> Unit // Nueva acción para cerrar sesión
) {
    val bottomNavController = rememberNavController()

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

    Scaffold(
        // 2. TopAppBar global con Isotipo/Logo, Campana de Notificaciones y Cierre de Sesión
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.logo), // Nombre de tu imagen en res/drawable
                        contentDescription = "Logo Sanaesencia",
                        modifier = Modifier
                            .height(36.dp) // Ajusta la altura para que encaje bien en la barra superior
                    )
                },
                actions = {
                    IconButton(onClick = { /* Lógica para ver notificaciones push/alertas simuladas */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                    }
                    IconButton(onClick = onLogout) {
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
                        // Configuración para que el color seleccionado sea permanente (Requisito de Navbar)
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
            // 3. Lógica del Botón Flotante (FAB)
            if (userRole == UserRole.PACIENTE.name) {
                FloatingActionButton(
                    onClick = { parentNavController.navigate(Screen.Booking.route) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agendar Cita")
                }
            } else if (userRole == UserRole.PROFESIONAL.name) {
                FloatingActionButton(
                    onClick = { /* Lógica para que el profesional agende una cita a un paciente asignado */ },
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agendar a Paciente")
                }
            }
            // El ADMIN no tiene FAB de agendamiento global aquí
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Inicio.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Vistas compartidas (Su contenido variará internamente según el rol en el ViewModel)
            composable(Screen.Inicio.route) { DashboardScreen(viewModel = bookingViewModel, role = userRole) }
            composable(Screen.Profile.route) { ProfileScreen(viewModel = bookingViewModel) }
            composable(Screen.About.route) { AboutScreen(role = userRole) } // Muestra reseñas, Admin puede borrar

            // Vistas de Paciente
            composable(Screen.Catalog.route) { CatalogTabScreen() }

            // Vistas de Profesional
            composable(Screen.MyPatients.route) { /* Crear MyPatientsScreen() */ }
            composable(Screen.MySchedule.route) { /* Crear MyScheduleScreen() */ }

            // Vistas de Administrador
            composable(Screen.ManageProducts.route) { /* Crear ManageProductsScreen() */ }
            composable(Screen.ManageSchedule.route) { /* Crear ManageScheduleScreen() */ }
            composable(Screen.AdminProfessionals.route) { AdminProfessionalsScreen() }
        }
    }
}