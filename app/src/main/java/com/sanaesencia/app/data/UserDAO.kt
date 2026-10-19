package com.sanaesencia.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE run = :run")
    suspend fun deleteUser(run: String)

    @Query("SELECT * FROM users WHERE run = :run AND passwordHash = :password LIMIT 1")
    suspend fun login(run: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE run = :run LIMIT 1")
    suspend fun getUserByRun(run: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>

    @Query("UPDATE users SET passwordHash = :newPassword WHERE run = :run")
    suspend fun updatePassword(run: String, newPassword: String)
}
