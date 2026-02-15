package com.example.marsphotos.model

import com.google.gson.annotations.SerializedName

data class MateriaCarga(
    @SerializedName("Materia")
    val materia: String = "",

    @SerializedName("Docente")
    val docente: String = "",

    @SerializedName("clvOficial")
    val clvOficial: String = "",

    @SerializedName("Grupo")
    val grupo: String = "",

    @SerializedName("CreditosMateria")
    val creditos: Int = 0,

    // Estos campos ayudan a formar el horario
    @SerializedName("Lunes")
    val lunes: String = "",
    @SerializedName("Martes")
    val martes: String = "",
    @SerializedName("Miercoles")
    val miercoles: String = "",
    @SerializedName("Jueves")
    val jueves: String = "",
    @SerializedName("Viernes")
    val viernes: String = ""
)