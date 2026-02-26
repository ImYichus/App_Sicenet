package com.example.marsphotos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.marsphotos.model.CalificacionFinal
import com.example.marsphotos.model.CalificacionParcial
import com.example.marsphotos.model.KardexItem
import com.example.marsphotos.model.MateriaCarga
import com.example.marsphotos.model.ProfileStudent

@Database(
    entities = [
        KardexItem::class,
        MateriaCarga::class,
        CalificacionParcial::class,
        CalificacionFinal::class,
        ProfileStudent::class
    ],
    version = 3, // <-- VERSIÓN ACTUALIZADA A 3 PARA LIMPIAR LA CACHÉ
    exportSchema = false
)
abstract class SicenetDatabase : RoomDatabase() {
    abstract fun sicenetDao(): SicenetDao

    companion object {
        @Volatile
        private var Instance: SicenetDatabase? = null

        fun getDatabase(context: Context): SicenetDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    SicenetDatabase::class.java,
                    "sicenet_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { Instance = it }
            }
        }
    }
}