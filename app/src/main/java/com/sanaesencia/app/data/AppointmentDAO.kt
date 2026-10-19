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

    // Verificar si ya existe una cita activa para el profesional en esa fecha y horario
    @Query("SELECT COUNT(*) FROM appointments WHERE date = :date AND professionalName = :professionalName AND timeSlot = :timeSlot AND state != 'CANCELADA'")
    suspend fun countConflictAppointments(date: String, professionalName: String, timeSlot: String): Int

    // Obtener qué boxes presenciales ya están ocupados en esa fecha y horario
    @Query("SELECT boxNumber FROM appointments WHERE date = :date AND timeSlot = :timeSlot AND isOnline = 0 AND state != 'CANCELADA' AND boxNumber IS NOT NULL")
    suspend fun getOccupiedBoxes(date: String, timeSlot: String): List<Int>

    // Eliminar una cita por su ID
    @Query("DELETE FROM appointments WHERE id = :appointmentId")
    suspend fun deleteAppointment(appointmentId: Long)
}
