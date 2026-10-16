package com.sanaesencia.app.data



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    // Para el registro de pacientes, profesionales o admins
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Para el Login (busca si el RUN y la contraseña coinciden)
    @Query("SELECT * FROM users WHERE run = :run AND passwordHash = :password LIMIT 1")
    suspend fun login(run: String, password: String): UserEntity?

    // Para buscar un usuario por su RUN (útil para recuperar contraseña o validar si ya existe)
    @Query("SELECT * FROM users WHERE run = :run LIMIT 1")
    suspend fun getUserByRun(run: String): UserEntity?

    // Para actualizar la contraseña si responde bien la pregunta de seguridad
    @Query("UPDATE users SET passwordHash = :newPassword WHERE run = :run")
    suspend fun updatePassword(run: String, newPassword: String)
}