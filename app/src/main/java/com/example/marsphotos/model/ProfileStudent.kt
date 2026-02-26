package com.example.marsphotos.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


data class ProfileStudent(
    val nombre: String,
    @SerializedName("semActual")
    val semestre: String,
    val inscrito: Boolean,
    val carrera: String
)
//5q$S_B