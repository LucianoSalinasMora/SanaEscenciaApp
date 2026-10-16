package com.sanaesencia.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 1. Agrega todas las clases al arreglo 'entities' y cambia version = 2
@Database(
    entities = [
        AppointmentEntity::class,
        UserEntity::class,
        ProductEntity::class,
        ReviewEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // 2. Declara los accesos (DAOs) para cada tabla
    abstract fun appointmentDao(): AppointmentDao
    abstract fun userDao(): UserDao       // Necesitaremos crear este DAO
    abstract fun productDao(): ProductDao // Necesitaremos crear este DAO
    abstract fun reviewDao(): ReviewDao   // Necesitaremos crear este DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sanaesencia_db"
                )
                    // 3. Agrega esta línea para que recree la base de datos sin errores al actualizar la versión
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}