package com.example.marsphotos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfil") // <--- ESTO ES VITAL
data class ProfileStudent(
    @PrimaryKey // Room necesita una llave primaria obligatoriamente
    val nombre: String,
    val matricula: String,
    val estatus: Boolean,
    val carrera: String
)