package com.sanaesencia.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanaesencia.app.data.AppointmentEntity
import com.sanaesencia.app.data.AppointmentRepository
import com.sanaesencia.app.data.UserEntity
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
            validateForm()
        }
    }

    fun onAreaChanged(area: String) {
        _uiState.update { it.copy(serviceType = area) }
        validateForm()
    }

    fun onSpecialtySelected(service: String) {
        onAreaChanged(service)
    }

    fun onProfessionalChanged(professional: String) {
        _uiState.update { it.copy(professionalName = professional) }
        validateForm()
    }

    fun onDateChanged(newValue: String) {
        _uiState.update { it.copy(date = newValue) }
        validateForm()
    }

    fun onTimeSlotChanged(newValue: String) {
        _uiState.update { it.copy(timeSlot = newValue) }
        validateForm()
    }

    fun onNotesChanged(newValue: String) {
        _uiState.update { it.copy(notes = newValue) }
    }

    fun onModeChanged(isOnline: Boolean) {
        _uiState.update { it.copy(isOnline = isOnline) }
    }

    private fun validateForm() {
        val state = _uiState.value
        val isValid = state.serviceType.isNotBlank() &&
                state.professionalName.isNotBlank() &&
                state.date.isNotBlank() &&
                state.timeSlot.isNotBlank()

        _uiState.update { it.copy(isFormValid = isValid) }
    }

    fun submitBooking(onSuccess: (AppointmentEntity) -> Unit) {
        if (!_uiState.value.isFormValid) return

        viewModelScope.launch {
            val state = _uiState.value
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
                _uiState.update { it.copy(isSubmittedSuccessfully = true) }
                onSuccess(entity)
            }
        }
    }
}
