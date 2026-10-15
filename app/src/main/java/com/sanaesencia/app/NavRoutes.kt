package com.sanaesencia.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    object Login : Screen("login", "Login")
    object SignUp : Screen("signup", "Registro")

    // Agrega el bloque { fun createRoute(...) } aquí:
    object Main : Screen("main/{role}", "Main") {
        fun createRoute(role: String) = "main/$role"
    }

    object Booking : Screen("booking", "Agendar Cita")

    // Tabs de la Navbar
    object Inicio : Screen("inicio", "Inicio", androidx.compose.material.icons.Icons.Default.Home)
    object Catalog : Screen("catalog", "Servicios", androidx.compose.material.icons.Icons.Default.Spa)
    object Professionals : Screen("professionals", "Pros", androidx.compose.material.icons.Icons.Default.MedicalServices)
    object About : Screen("about", "Nosotros", androidx.compose.material.icons.Icons.Default.Info)
    object Profile : Screen("profile", "Perfil", androidx.compose.material.icons.Icons.Default.Person)
    object AdminMenu : Screen("admin_menu", "Gestión", androidx.compose.material.icons.Icons.Default.ManageAccounts)
}