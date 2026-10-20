package com.sanaesencia.app.viewmodel

// ==========================================
// Estado del Formulario de Agendamiento de Citas
// ==========================================
data class BookingFormState(
    // Datos del Paciente
    val patientName: String = "",
    val patientEmail: String = "",
    val patientPhone: String = "",
    val patientRun: String = "",
    // Selección de Servicio y Profesional
    val serviceType: String = "Psicología Infanto-Juvenil",
    val professionalName: String = "Dr. Carlos Mendoza",
    // Fecha, Horario y Modalidad
    val date: String = "",
    val timeSlot: String = "",
    val notes: String = "",
    val isOnline: Boolean = false,
    // Estados de Validación y Envío del Formulario
    val isFormValid: Boolean = false,
    val isSubmittedSuccessfully: Boolean = false,
    val errorMessage: String? = null
)
