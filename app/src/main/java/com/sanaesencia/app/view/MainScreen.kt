package com.sanaesencia.app.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sanaesencia.app.Screen
import com.sanaesencia.app.data.UserRole
import com.sanaesencia.app.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    parentNavController: NavHostController,
    userRole: String,
    bookingViewModel: BookingViewModel
) {
    val bottomNavController = rememberNavController()

    // Configuración dinámica de items según el rol
    val navItems = mutableListOf<Screen>(Screen.Inicio)

    if (userRole == UserRole.ADMINISTRADOR.name) {
        navItems.add(Screen.AdminMenu)
        navItems.add(Screen.Professionals)
        navItems.add(Screen.Profile)
    } else {
        navItems.add(Screen.Catalog)
        navItems.add(Screen.Professionals)
        navItems.add(Screen.About)
        navItems.add(Screen.Profile)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
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
            if (userRole != UserRole.ADMINISTRADOR.name) {
                FloatingActionButton(
                    onClick = { parentNavController.navigate(Screen.Booking.route) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agendar Cita")
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Inicio.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Inicio.route) { DashboardScreen(viewModel = bookingViewModel) }
            composable(Screen.Catalog.route) { CatalogTabScreen() }
            composable(Screen.Professionals.route) { ProfessionalsScreen() }
            composable(Screen.About.route) { AboutScreen() }
            composable(Screen.Profile.route) { ProfileScreen(viewModel = bookingViewModel) }
            composable(Screen.AdminMenu.route) { AdminProfessionalsScreen() }
        }
    }
}