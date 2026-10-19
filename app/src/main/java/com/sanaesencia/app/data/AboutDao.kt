package com.sanaesencia.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AboutDao {
    @Query("SELECT * FROM about_info WHERE id = 1")
    fun getAboutInfo(): Flow<AboutInfoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAboutInfo(info: AboutInfoEntity)
}
