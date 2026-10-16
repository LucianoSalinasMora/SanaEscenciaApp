package com.sanaesencia.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientName: String = "",
    val patientEmail: String = "",
    val patientPhone: String = "",
    val patientRun: String = "",
    val professionalRun: String = "",
    val serviceType: String = "",
    val date: String = "",
    val timeSlot: String = "10:00 AM",
    val professionalName: String = "Centro Sanaesencia",
    val isOnline: Boolean = false,
    val boxNumber: Int? = null,
    val status: String = "Agendada",
    val state: String = "PENDIENTE",
    val patientNotes: String = ""
)
