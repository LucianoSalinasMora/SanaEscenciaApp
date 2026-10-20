package com.sanaesencia.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// ==========================================
// Definición de Rutas de Navegación de la App
// ==========================================
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    // ------------------------------------------
    // Rutas de Autenticación y Acceso
    // ------------------------------------------
    object Login : Screen("login", "Login")
    object SignUp : Screen("signup", "Registro")
    object ForgotPassword : Screen("forgot_password", "Recuperar") // NUEVO

    // ------------------------------------------
    // Rutas Principales de la Aplicación
    // ------------------------------------------
    object Main : Screen("main/{role}", "Main") {
        fun createRoute(role: String) = "main/$role"
    }

    object Booking : Screen("booking", "Agendar Cita")

    // ------------------------------------------
    // Pestañas Generales (Compartidas por Roles)
    // ------------------------------------------
    object Inicio : Screen("inicio", "Inicio", Icons.Default.Home)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)

    // ------------------------------------------
    // Pestañas de Vista para Pacientes
    // ------------------------------------------
    object Catalog : Screen("catalog", "Servicios", Icons.Default.Spa)
    object PatientProducts : Screen("patient_products", "Productos", Icons.Default.ShoppingBag)
    object About : Screen("about", "Nosotros", Icons.Default.Info)

    // ------------------------------------------
    // Pestañas de Vista para Profesionales
    // ------------------------------------------
    object MyPatients : Screen("my_patients", "Pacientes", Icons.Default.People)
    object MySchedule : Screen("my_schedule", "Turnos", Icons.Default.CalendarMonth)

    // ------------------------------------------
    // Pestañas de Vista para Administradores
    // ------------------------------------------
    object ManageProducts : Screen("manage_products", "Productos", Icons.Default.Inventory)
    object ManageSchedule : Screen("manage_schedule", "Citas", Icons.AutoMirrored.Filled.EventNote)
    object AdminProfessionals : Screen("admin_professionals", "Gestión", Icons.Default.ManageAccounts)
}