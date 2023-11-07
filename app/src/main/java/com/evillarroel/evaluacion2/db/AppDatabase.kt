package com.evillarroel.evaluacion2.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Producto::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var BASE_DATOS: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return BASE_DATOS ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "productos.bd"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { BASE_DATOS = it }
                Log.d("AppDatabase", "Base de datos Room creada exitosamente")
                return database
            }
        }
    }
}