package com.example.marsphotos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marsphotos.model.CalificacionFinal
import com.example.marsphotos.model.CalificacionParcial
import com.example.marsphotos.model.KardexItem
import com.example.marsphotos.model.MateriaCarga
import com.example.marsphotos.model.ProfileStudent

@Dao
interface SicenetDao {
    // --- PERFIL (Lo que faltaba) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerfil(perfil: ProfileStudent)

    @Query("SELECT * FROM perfil LIMIT 1") // Asegúrate que tu Entity se llame 'perfil'
    suspend fun getPerfil(): ProfileStudent?

    // --- KÁRDEX ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKardex(items: List<KardexItem>)

    @Query("SELECT * FROM kardex")
    suspend fun getAllKardex(): List<KardexItem>

    // --- CARGA ACADÉMICA ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarga(items: List<MateriaCarga>)

    @Query("SELECT * FROM carga_academica")
    suspend fun getCarga(): List<MateriaCarga>

    // --- CALIFICACIONES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParciales(items: List<CalificacionParcial>)

    @Query("SELECT * FROM parciales")
    suspend fun getParciales(): List<CalificacionParcial>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinales(items: List<CalificacionFinal>)

    @Query("SELECT * FROM finales")
    suspend fun getFinales(): List<CalificacionFinal>
}