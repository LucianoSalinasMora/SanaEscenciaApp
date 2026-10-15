package com.sanaesencia.app.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientName: String,
    val patientEmail: String,
    val patientPhone: String,
    val serviceType: String,
    val professionalName: String,
    val date: String,
    val timeSlot: String,
    val isOnline: Boolean,
    val boxNumber: Int? = null,
    val status: String = "Agendada"
)