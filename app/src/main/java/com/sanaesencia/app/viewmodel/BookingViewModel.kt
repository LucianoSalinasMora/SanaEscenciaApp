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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class BookingViewModel(private val repository: AppointmentRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingFormState())
    val uiState: StateFlow<BookingFormState> = _uiState.asStateFlow()

    // Flujo en tiempo real de citas persistidas en Room
    val appointments: StateFlow<List<AppointmentEntity>> = repository.allAppointments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSpecialtySelected(specialty: String) {
        _uiState.update {
            it.copy(
                serviceType = specialty,
                professionalName = "",
                timeSlot = ""
            )
        }
        validateForm()
    }

    fun onProfessionalSelected(professional: String) {
        _uiState.update { it.copy(professionalName = professional, timeSlot = "") }
        validateForm()
    }

    fun onDateSelected(millis: Long) {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val formattedDate = formatter.format(Date(millis))
        _uiState.update {
            it.copy(
                selectedDateMillis = millis,
                dateFormatted = formattedDate,
                timeSlot = ""
            )
        }
        validateForm()
    }

    fun onTimeSlotSelected(slot: String) {
        _uiState.update { it.copy(timeSlot = slot) }
        validateForm()
    }

    fun onModeChanged(isOnline: Boolean) {
        _uiState.update { it.copy(isOnline = isOnline) }
    }

    private fun validateForm() {
        val state = _uiState.value
        val isValid = state.serviceType.isNotBlank() &&
                state.professionalName.isNotBlank() &&
                state.dateFormatted.isNotBlank() &&
                state.timeSlot.isNotBlank()

        _uiState.update { it.copy(isFormValid = isValid) }
    }

    fun submitBooking(onSuccess: (AppointmentEntity) -> Unit) {
        if (!_uiState.value.isFormValid) return

        viewModelScope.launch {
            val state = _uiState.value
            val entity = AppointmentEntity(
                patientName = state.patientName,
                patientEmail = state.patientEmail,
                patientPhone = state.patientPhone,
                serviceType = state.serviceType,
                professionalName = state.professionalName,
                date = state.dateFormatted,
                timeSlot = state.timeSlot,
                isOnline = state.isOnline,
                boxNumber = if (state.isOnline) null else (1..8).random()
            )

            val success = repository.saveAppointment(entity)
            if (success) {
                _uiState.update { BookingFormState() } // Limpiar formulario
                onSuccess(entity)
            }
        }
    }
}