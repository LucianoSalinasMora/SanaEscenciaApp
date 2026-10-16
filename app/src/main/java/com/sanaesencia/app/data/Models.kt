package com.sanaesencia.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. Enums para Roles y Estados
enum class UserRole { PACIENTE, PROFESIONAL, ADMIN }
enum class AppointmentState { PENDIENTE, CONFIRMADA, COMPLETADA, CANCELADA, NO_ASISTE }

// 2. Entidad de Usuario
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val run: String, // Usaremos el RUN como ID principal
    val name: String,
    val phone: String,
    val passwordHash: String,
    val role: UserRole = UserRole.PACIENTE,
    val securityQuestion: String,
    val securityAnswer: String
)

// 3. Modelo de Profesional y Datos Mock
data class Professional(
    val id: String = "",
    val name: String,
    val specialty: String
)

val mockProfessionals = listOf(
    Professional("1", "Dr. Carlos Mendoza", "Psicología Infanto-Juvenil"),
    Professional("2", "Dra. María Paz Silva", "Terapia Ocupacional"),
    Professional("3", "Lic. Andrea Rojas", "Fonoaudiología"),
    Professional("4", "Dr. Juan Pablo Torres", "Nutrición Integrativa")
)

// 4. Entidad de Productos (Para el Admin)
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val price: Double,
    val isAvailable: Boolean = true
)

// 5. Entidad de Reseñas (Pacientes escriben, Admin gestiona)
@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientName: String,
    val content: String,
    val rating: Int, // 1 a 5 estrellas
    val date: String
)
