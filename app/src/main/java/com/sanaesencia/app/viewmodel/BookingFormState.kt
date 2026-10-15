package com.sanaesencia.app.viewmodel


data class BookingFormState(
    val name: String = "",
    val nameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val serviceType: String = "Psicología Infanto-Juvenil",
    val date: String = "",
    val dateError: String? = null,
    val isOnline: Boolean = false,
    val isFormValid: Boolean = false,
    val isSubmittedSuccessfully: Boolean = false,
    val patientName: String = "Juan Pérez",
    val patientEmail: String = "juan.perez@email.com",
    val patientPhone: String = "+56912345678",
    val professionalName: String = "",
    val selectedDateMillis: Long? = null,
    val dateFormatted: String = "",
    val timeSlot: String = "",

)
