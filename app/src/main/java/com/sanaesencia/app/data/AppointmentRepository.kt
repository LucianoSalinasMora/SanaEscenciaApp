package com.sanaesencia.app.data


import kotlinx.coroutines.flow.Flow

class AppointmentRepository(private val appointmentDao: AppointmentDao) {
    val allAppointments: Flow<List<AppointmentEntity>> = appointmentDao.getAllAppointments()

    suspend fun saveAppointment(appointment: AppointmentEntity): Boolean {
        return try {
            appointmentDao.insertAppointment(appointment)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isSlotTaken(date: String, professionalName: String, timeSlot: String): Boolean {
        return appointmentDao.countConflictAppointments(date, professionalName, timeSlot) > 0
    }

    suspend fun getOccupiedBoxes(date: String, timeSlot: String): List<Int> {
        return appointmentDao.getOccupiedBoxes(date, timeSlot)
    }

    suspend fun cancelAppointment(id: Long) {
        appointmentDao.updateAppointmentState(id, "CANCELADA")
    }

    suspend fun deleteAppointment(id: Long) {
        appointmentDao.deleteAppointment(id)
    }
}