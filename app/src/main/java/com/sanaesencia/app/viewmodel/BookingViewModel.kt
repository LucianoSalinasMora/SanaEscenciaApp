package com.sanaesencia.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanaesencia.app.data.AppointmentEntity
import com.sanaesencia.app.data.AppointmentRepository
import com.sanaesencia.app.data.UserEntity
import com.sanaesencia.app.data.mockProfessionals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookingViewModel(private val repository: AppointmentRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingFormState())
    val uiState: StateFlow<BookingFormState> = _uiState.asStateFlow()

    val appointments: StateFlow<List<AppointmentEntity>> = repository.allAppointments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun loadPatientData(patient: UserEntity?) {
        if (patient != null) {
            _uiState.update {
                it.copy(
                    patientName = patient.name,
                    patientEmail = patient.email,
                    patientPhone = patient.phone,
                    patientRun = patient.run
                )
            }
        }
    }

    fun getAvailableProfessionals(dbProfessionals: List<UserEntity>, area: String): List<String> {
        val filteredFromDb = dbProfessionals.filter { it.specialty.equals(area, ignoreCase = true) }.map { it.name }
        return if (filteredFromDb.isNotEmpty()) {
            filteredFromDb
        } else {
            val filteredFromMock = mockProfessionals.filter { it.specialty.equals(area, ignoreCase = true) }.map { it.name }
            if (filteredFromMock.isNotEmpty()) filteredFromMock else listOf("Dr. Carlos Mendoza", "Dra. María Paz Silva", "Lic. Andrea Rojas", "Centro Sanaesencia")
        }
    }

    fun onAreaChanged(area: String, dbProfessionals: List<UserEntity>) {
        val availablePros = getAvailableProfessionals(dbProfessionals, area)
        val currentPro = _uiState.value.professionalName
        val newPro = if (availablePros.contains(currentPro)) currentPro else (availablePros.firstOrNull() ?: "")

        _uiState.update { 
            it.copy(
                serviceType = area,
                professionalName = newPro,
                errorMessage = null
            )
        }
        validateForm(dbProfessionals)
    }

    fun onSpecialtySelected(service: String) {
        onAreaChanged(service, emptyList())
    }

    fun onProfessionalChanged(professional: String, dbProfessionals: List<UserEntity>) {
        _uiState.update { it.copy(professionalName = professional, errorMessage = null) }
        validateForm(dbProfessionals)
    }

    fun onDateChanged(newValue: String, dbProfessionals: List<UserEntity>) {
        _uiState.update { it.copy(date = newValue, errorMessage = null) }
        validateForm(dbProfessionals)
    }

    fun onTimeSlotChanged(newValue: String, dbProfessionals: List<UserEntity>) {
        _uiState.update { it.copy(timeSlot = newValue, errorMessage = null) }
        validateForm(dbProfessionals)
    }

    fun onNotesChanged(newValue: String) {
        _uiState.update { it.copy(notes = newValue) }
    }

    fun onModeChanged(isOnline: Boolean) {
        _uiState.update { it.copy(isOnline = isOnline) }
    }

    private fun validateForm(dbProfessionals: List<UserEntity>) {
        val state = _uiState.value
        val availablePros = getAvailableProfessionals(dbProfessionals, state.serviceType)
        val isProValid = availablePros.contains(state.professionalName)

        val isValid = state.serviceType.isNotBlank() &&
                state.professionalName.isNotBlank() &&
                isProValid &&
                state.date.isNotBlank() &&
                state.timeSlot.isNotBlank()

        _uiState.update { it.copy(isFormValid = isValid) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            repository.cancelAppointment(appointmentId)
        }
    }

    fun submitBooking(dbProfessionals: List<UserEntity>, onSuccess: (AppointmentEntity) -> Unit) {
        val state = _uiState.value
        if (!state.isFormValid) return

        // 1. Validar que el profesional pertenezca al área seleccionada
        val availablePros = getAvailableProfessionals(dbProfessionals, state.serviceType)
        if (!availablePros.contains(state.professionalName)) {
            _uiState.update { it.copy(errorMessage = "El profesional seleccionado no pertenece al área o especialidad seleccionada.") }
            return
        }

        // 2. Validar solapamiento de horario para el mismo profesional en la misma fecha (mismo paciente u otro)
        val existingAppointments = appointments.value
        val hasOverlap = existingAppointments.any {
            (it.date == state.date) &&
            (it.professionalName.equals(state.professionalName, ignoreCase = true)) &&
            (it.timeSlot.equals(state.timeSlot, ignoreCase = true)) &&
            (it.state != "CANCELADA") &&
            (it.status != "Cancelada")
        }

        if (hasOverlap) {
            _uiState.update { it.copy(errorMessage = "El bloque horario '${state.timeSlot}' ya se encuentra reservado para el profesional ${state.professionalName} en la fecha ${state.date}. Por favor elija otro horario.") }
            return
        }

        viewModelScope.launch {
            val entity = AppointmentEntity(
                patientName = state.patientName.ifBlank { "Paciente Registrado" },
                patientEmail = state.patientEmail,
                patientPhone = state.patientPhone,
                patientRun = state.patientRun,
                serviceType = state.serviceType,
                professionalName = state.professionalName,
                date = state.date,
                timeSlot = state.timeSlot,
                isOnline = state.isOnline,
                boxNumber = if (state.isOnline) null else (1..8).random(),
                patientNotes = state.notes
            )

            val success = repository.saveAppointment(entity)
            if (success) {
                _uiState.update { it.copy(isSubmittedSuccessfully = true, errorMessage = null) }
                onSuccess(entity)
            } else {
                _uiState.update { it.copy(errorMessage = "Error al guardar la cita en la base de datos.") }
            }
        }
    }
}
