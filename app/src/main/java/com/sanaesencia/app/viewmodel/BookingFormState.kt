package com.sanaesencia.app.viewmodel

data class BookingFormState(
    val patientName: String = "",
    val patientEmail: String = "",
    val patientPhone: String = "",
    val patientRun: String = "",
    val serviceType: String = "Psicología Infanto-Juvenil",
    val professionalName: String = "Dr. Carlos Mendoza",
    val date: String = "",
    val timeSlot: String = "",
    val notes: String = "",
    val isOnline: Boolean = false,
    val isFormValid: Boolean = false,
    val isSubmittedSuccessfully: Boolean = false,
    val errorMessage: String? = null
)
