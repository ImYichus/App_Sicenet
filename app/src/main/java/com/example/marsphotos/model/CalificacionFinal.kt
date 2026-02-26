package com.example.marsphotos.model

import com.google.gson.annotations.SerializedName

data class CalificacionFinal(
    @SerializedName("materia") val materia: String,
    @SerializedName("calificacion") val calificacion: String,
    @SerializedName("acreditacion") val acreditacion: String // Ej: "ORDINARIO"
)