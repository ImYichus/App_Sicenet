package com.example.marsphotos.model

import com.google.gson.annotations.SerializedName

data class CalificacionParcial(
    @SerializedName("Materia")
    val materia: String,
    @SerializedName("C1") val p1: String?,
    @SerializedName("C2") val p2: String?,
    @SerializedName("C3") val p3: String?,
    @SerializedName("C4") val p4: String?,
    @SerializedName("C5") val p5: String?,
    @SerializedName("C6") val p6: String?,
    @SerializedName("C7") val p7: String?,
    @SerializedName("C8") val p8: String?,
    @SerializedName("C9") val p9: String?,
    @SerializedName("C10") val p10: String?,
    @SerializedName("C11") val p11: String?,
    @SerializedName("C12") val p12: String?,
    @SerializedName("C13") val p13: String?
)