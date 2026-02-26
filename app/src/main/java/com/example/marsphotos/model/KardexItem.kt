package com.example.marsphotos.model

import com.google.gson.annotations.SerializedName

data class KardexItem(


    @SerializedName("Materia")
    val materia: String,

    @SerializedName("Calif")
    val calificacion: Int,

    @SerializedName("P1")
    val periodo: String?,

    @SerializedName("A1")
    val anio: String?,

    @SerializedName("S1")
    val semestre: String?
)
//5q$S_B