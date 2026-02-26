package com.example.marsphotos.data

import android.util.Log
import com.example.marsphotos.model.* import com.example.marsphotos.network.* import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody.Companion.toRequestBody

interface SNRepository {
    suspend fun acceso(m: String, p: String, t: String): String
    suspend fun accesoObjeto(m: String, p: String): Usuario
    suspend fun profile(m: String, p: String): ProfileStudent
    suspend fun getKardex(lineamiento: String = "1"): List<KardexItem>
    suspend fun getCalificacionesUnidades(): List<CalificacionParcial>
    suspend fun getCalificacionesFinales(modoEducativo: Int = 1): List<CalificacionFinal>
    suspend fun getCargaAcademica(): List<MateriaCarga>
}

class NetworSNRepository(
    private val snApiService: SICENETWService
) : SNRepository {

    // Función auxiliar para centralizar la limpieza y evitar errores de parseo
    private fun limpiarJson(json: String): String {
        return json
            .replace("&quot;", "\"")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("\\\"", "\"") // Quita comillas escapadas del servidor
            .trim()
    }

    override suspend fun acceso(m: String, p: String, t: String): String {
        return try {
            val res = snApiService.acceso(bodyacceso.format(m, p, t).toRequestBody())
            val responseString = res.string()
            Log.d("RXML", "Respuesta Servidor: $responseString")
            responseString
        } catch (e: Exception) {
            Log.e("RXML", "Error en acceso: ${e.message}")
            "Error: ${e.message}"
        }
    }

    override suspend fun accesoObjeto(m: String, p: String): Usuario = Usuario(matricula = m)

    override suspend fun profile(m: String, p: String): ProfileStudent {
        return try {
            val response = snApiService.getPerfil(bodyAlumno.format(m, p).toRequestBody())
            val jsonLimpio = limpiarJson(extraerJson(response.string(), "{", "}"))
            Gson().fromJson(jsonLimpio, ProfileStudent::class.java)
        } catch (e: Exception) {
            Log.e("RXML", "Error en profile: ${e.message}")
            throw e
        }
    }

    override suspend fun getKardex(lineamiento: String): List<KardexItem> {
        return try {
            val response = snApiService.getKardex(bodyKardex.format(lineamiento).toRequestBody())
            val jsonLimpio = limpiarJson(extraerJson(response.string(), "[", "]"))

            if (jsonLimpio == "[]" || jsonLimpio.isEmpty()) return emptyList()

            val itemType = object : TypeToken<List<KardexItem>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) {
            Log.e("SICENET_DEBUG", "Error Kardex: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getCalificacionesUnidades(): List<CalificacionParcial> {
        return try {
            val res = snApiService.getCalifUnidades(bodyVacio.toRequestBody())
            val xmlString = res.string()

            // Buscamos el JSON entre los corchetes [ ]
            val inicio = xmlString.indexOf("[")
            val fin = xmlString.lastIndexOf("]")

            if (inicio == -1 || fin == -1) return emptyList()

            val jsonBruto = xmlString.substring(inicio, fin + 1)

            // LIMPIEZA TOTAL: SICENET a veces escapa las comillas dos veces
            val jsonLimpio = jsonBruto
                .replace("&quot;", "\"")
                .replace("\\\"", "\"")
                .replace("\"{", "{") // Corrige si el JSON viene como String
                .replace("}\"", "}")

            Log.d("SICENET_DEBUG", "JSON FINAL: $jsonLimpio")

            val itemType = object : TypeToken<List<CalificacionParcial>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) {
            Log.e("RXML", "Error en parseo: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getCalificacionesFinales(modoEducativo: Int): List<CalificacionFinal> {
        return try {
            val response = snApiService.getCalifFinales(bodyCalifFinales.format(modoEducativo).toRequestBody())
            val jsonLimpio = limpiarJson(extraerJson(response.string(), "[", "]"))

            if (jsonLimpio == "[]" || jsonLimpio.isEmpty()) return emptyList()

            val itemType = object : TypeToken<List<CalificacionFinal>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) {
            Log.e("RXML", "Error Finales: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getCargaAcademica(): List<MateriaCarga> {
        return try {
            val response = snApiService.getCargaAcademica(bodyCargaAcademica.toRequestBody())
            val jsonLimpio = limpiarJson(extraerJson(response.string(), "[", "]"))

            if (jsonLimpio == "[]" || jsonLimpio.isEmpty()) return emptyList()

            val itemType = object : TypeToken<List<MateriaCarga>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) {
            Log.e("RXML", "Error Carga: ${e.message}")
            emptyList()
        }
    }

    private fun extraerJson(xmlString: String, inicio: String, fin: String): String {
        val startIndex = xmlString.indexOf(inicio)
        val endIndex = xmlString.lastIndexOf(fin)

        return if (startIndex != -1 && endIndex != -1) {
            xmlString.substring(startIndex, endIndex + 1)
        } else {
            if (inicio == "[") "[]" else "{}"
        }
    }
}

class DBLocalSNRepository(val apiDB : Any): SNRepository {
    override suspend fun acceso(m: String, p: String, t: String): String = ""
    override suspend fun accesoObjeto(m: String, p: String): Usuario = Usuario(matricula = "")
    override suspend fun profile(m: String, p: String): ProfileStudent = throw NotImplementedError()
    override suspend fun getKardex(lineamiento: String): List<KardexItem> = emptyList()
    override suspend fun getCalificacionesUnidades(): List<CalificacionParcial> = emptyList()
    override suspend fun getCalificacionesFinales(modoEducativo: Int): List<CalificacionFinal> = emptyList()
    override suspend fun getCargaAcademica(): List<MateriaCarga> = emptyList()
}