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
}