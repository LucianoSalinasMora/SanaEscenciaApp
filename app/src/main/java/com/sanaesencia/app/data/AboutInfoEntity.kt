package com.sanaesencia.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "about_info")
data class AboutInfoEntity(
    @PrimaryKey val id: Int = 1,
    val centerTitle: String = "Centro Integrativo Sanaesencia",
    val description: String = "Salud mental infanto-juvenil, familia y bienestar integrativo. Atendemos de forma presencial en nuestros 8 boxes de Los Vilos y online para todo Chile y el extranjero.",
    val address: String = "Caupolicán 220, Los Vilos",
    val phone: String = "+56 9 2366 5073",
    val email: String = "contacto@centrosanaesencia.cl"
)
