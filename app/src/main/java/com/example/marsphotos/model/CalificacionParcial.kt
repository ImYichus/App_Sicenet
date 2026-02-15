package com.example.marsphotos.model

import com.google.gson.annotations.SerializedName

data class CalificacionParcial(
    @SerializedName("Materia")
    val materia: String,
    @SerializedName("P1") val p1: String?,
    @SerializedName("P2") val p2: String?,
    @SerializedName("P3") val p3: String?,
    @SerializedName("P4") val p4: String?,
    @SerializedName("P5") val p5: String?,
    @SerializedName("P6") val p6: String?,
    @SerializedName("P7") val p7: String?,
    @SerializedName("P8") val p8: String?,
    @SerializedName("P9") val p9: String?,
    @SerializedName("P10") val p10: String?,
    @SerializedName("P11") val p11: String?,
    @SerializedName("P12") val p12: String?,
    @SerializedName("P13") val p13: String?
)