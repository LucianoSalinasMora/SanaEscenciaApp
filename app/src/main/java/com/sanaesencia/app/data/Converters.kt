package com.sanaesencia.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole?): String? {
        return role?.name
    }

    @TypeConverter
    fun toUserRole(value: String?): UserRole? {
        return if (value != null) {
            try {
                UserRole.valueOf(value)
            } catch (e: Exception) {
                UserRole.PACIENTE
            }
        } else {
            UserRole.PACIENTE
        }
    }
}
