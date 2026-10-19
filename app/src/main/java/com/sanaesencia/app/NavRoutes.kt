package com.sanaesencia.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    // Autenticación
    object Login : Screen("login", "Login")
    object SignUp : Screen("signup", "Registro")
    object ForgotPassword : Screen("forgot_password", "Recuperar") // NUEVO

    object Main : Screen("main/{role}", "Main") {
        fun createRoute(role: String) = "main/$role"
    }

    object Booking : Screen("booking", "Agendar Cita")

    // Tabs Generales
    object Inicio : Screen("inicio", "Inicio", Icons.Default.Home)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)

    // Tabs de Paciente
    object Catalog : Screen("catalog", "Servicios", Icons.Default.Spa)
    object PatientProducts : Screen("patient_products", "Productos", Icons.Default.ShoppingBag)
    object About : Screen("about", "Nosotros", Icons.Default.Info)

    // Tabs de Profesional (NUEVOS)
    object MyPatients : Screen("my_patients", "Pacientes", Icons.Default.People)
    object MySchedule : Screen("my_schedule", "Turnos", Icons.Default.CalendarMonth)

    // Tabs de Administrador (NUEVOS/MODIFICADOS)
    object ManageProducts : Screen("manage_products", "Productos", Icons.Default.Inventory)
    object ManageSchedule : Screen("manage_schedule", "Citas", Icons.AutoMirrored.Filled.EventNote)
    object AdminProfessionals : Screen("admin_professionals", "Gestión", Icons.Default.ManageAccounts)
}