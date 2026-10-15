package com.sanaesencia.app.data

// Tipos de usuario
enum class UserRole {
    PACIENTE, PROFESIONAL, ADMINISTRADOR
}

// Modelo de Profesional
data class Professional(
    val id: Int,
    val name: String,
    val specialty: String,
    val imageUrl: String = "" // Para futura integración de imágenes
)

// Datos Ficticios
val mockProfessionals = listOf(
    Professional(1, "Dra. Ana López", "Psicología Infanto-Juvenil"),
    Professional(2, "Lic. Carlos Gómez", "Terapia Ocupacional"),
    Professional(3, "Lic. María Fernández", "Fonoaudiología"),
    Professional(4, "Dr. Roberto Silva", "Quiropraxia y Masajes"),
    Professional(5, "Lic. Camila Soto", "Nutrición Integrativa")
)

