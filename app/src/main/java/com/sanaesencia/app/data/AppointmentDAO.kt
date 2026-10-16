package com.sanaesencia.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    @Query("SELECT * FROM appointments ORDER BY id DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    // Obtener citas específicas de un profesional
    @Query("SELECT * FROM appointments WHERE professionalRun = :professionalRun ORDER BY date DESC")
    fun getAppointmentsByProfessional(professionalRun: String): Flow<List<AppointmentEntity>>

    // Obtener citas específicas de un paciente
    @Query("SELECT * FROM appointments WHERE patientRun = :patientRun ORDER BY date DESC")
    fun getAppointmentsByPatient(patientRun: String): Flow<List<AppointmentEntity>>

    // Cambiar el estado de una cita
    @Query("UPDATE appointments SET state = :newState WHERE id = :appointmentId")
    suspend fun updateAppointmentState(appointmentId: Long, newState: String)
}
