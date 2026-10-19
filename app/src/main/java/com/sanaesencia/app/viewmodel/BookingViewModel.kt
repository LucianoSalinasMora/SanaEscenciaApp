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

    fun onAreaChanged(area: String, defaultProfessional: String = "") {
        _uiState.update { current ->
            current.copy(
                serviceType = area,
                professionalName = defaultProfessional
            )
        }
        validateForm()
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

    fun cancelAppointment(id: Long, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.cancelAppointment(id)
            onSuccess()
        }
    }

    fun deleteAppointment(id: Long, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteAppointment(id)
            onSuccess()
        }
    }

    fun submitBooking(
        onSuccess: (AppointmentEntity) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        if (!_uiState.value.isFormValid) {
            onError?.invoke("Por favor complete todos los campos requeridos")
            return
        }

        viewModelScope.launch {
            val state = _uiState.value

            // 1. Validar directamente en SQLite Room que el horario no esté tomado
            val alreadyTaken = repository.isSlotTaken(
                date = state.date.trim(),
                professionalName = state.professionalName.trim(),
                timeSlot = state.timeSlot.trim()
            )
            if (alreadyTaken) {
                onError?.invoke("Este horario ya ha sido reservado con el profesional.")
                return@launch
            }

            // 2. Para modalidad presencial, buscar un box libre (1..8) para evitar colisiones de sala
            val occupiedBoxes = repository.getOccupiedBoxes(state.date.trim(), state.timeSlot.trim())
            val freeBoxes = (1..8).filter { it !in occupiedBoxes }
            val assignedBox = if (state.isOnline) null else (freeBoxes.firstOrNull() ?: (1..8).random())

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
                boxNumber = assignedBox,
                patientNotes = state.notes
            )

            val success = repository.saveAppointment(entity)
            if (success) {
                _uiState.update { it.copy(isSubmittedSuccessfully = true, errorMessage = null) }
                onSuccess(entity)
            } else {
                onError?.invoke("Error al guardar la cita en el sistema.")
            }
        }
    }
}
