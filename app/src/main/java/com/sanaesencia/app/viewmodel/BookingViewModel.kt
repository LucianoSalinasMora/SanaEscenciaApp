package com.sanaesencia.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanaesencia.app.data.AppointmentEntity
import com.sanaesencia.app.data.AppointmentRepository
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

    fun onNameChanged(newValue: String) {
        val error = if (newValue.isBlank()) "El nombre es obligatorio" else null
        _uiState.update { it.copy(name = newValue, nameError = error) }
        validateForm()
    }

    fun onEmailChanged(newValue: String) {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()
        val error = when {
            newValue.isBlank() -> "El correo es obligatorio"
            !newValue.matches(emailRegex) -> "Formato de correo inválido"
            else -> null
        }
        _uiState.update { it.copy(email = newValue, emailError = error) }
        validateForm()
    }

    fun onPhoneChanged(newValue: String) {
        val error = when {
            newValue.isBlank() -> "El teléfono es obligatorio"
            newValue.length < 9 -> "Debe tener al menos 9 dígitos"
            else -> null
        }
        _uiState.update { it.copy(phone = newValue, phoneError = error) }
        validateForm()
    }

    fun onDateChanged(newValue: String) {
        val error = if (newValue.isBlank()) "Seleccione una fecha" else null
        _uiState.update { it.copy(date = newValue, dateError = error) }
        validateForm()
    }

    fun onModeChanged(isOnline: Boolean) {
        _uiState.update { it.copy(isOnline = isOnline) }
    }

    fun onServiceChanged(service: String) {
        _uiState.update { it.copy(serviceType = service) }
    }

    fun onSpecialtySelected(service: String) {
        onServiceChanged(service)
    }

    private fun validateForm() {
        val state = _uiState.value
        val isValid = state.name.isNotBlank() && state.nameError == null &&
                state.email.isNotBlank() && state.emailError == null &&
                state.phone.isNotBlank() && state.phoneError == null &&
                state.date.isNotBlank() && state.dateError == null

        _uiState.update { it.copy(isFormValid = isValid) }
    }

    fun submitBooking(onSuccess: (AppointmentEntity) -> Unit) {
        if (!_uiState.value.isFormValid) return

        viewModelScope.launch {
            val state = _uiState.value
            val entity = AppointmentEntity(
                patientName = state.name,
                patientEmail = state.email,
                patientPhone = state.phone,
                serviceType = state.serviceType,
                date = state.date,
                isOnline = state.isOnline,
                boxNumber = if (state.isOnline) null else (1..8).random()
            )

            val success = repository.saveAppointment(entity)
            if (success) {
                _uiState.update { it.copy(isSubmittedSuccessfully = true) }
                onSuccess(entity)
            }
        }
    }
}
