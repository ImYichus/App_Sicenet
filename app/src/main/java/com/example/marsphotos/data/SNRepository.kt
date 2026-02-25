package com.example.marsphotos.data

import android.util.Log
import com.example.marsphotos.model.* // Importa todos tus modelos de datos
import com.example.marsphotos.network.* // Importa tus variables body y el servicio
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody.Companion.toRequestBody

interface SNRepository {
    suspend fun acceso(m: String, p: String, t: String): String
    suspend fun accesoObjeto(m: String, p: String): Usuario

    // --- TODOS LOS MÉTODOS AHORA DEVUELVEN OBJETOS ---
    suspend fun profile(m: String, p: String): ProfileStudent
    suspend fun getKardex(lineamiento: String = "1"): List<KardexItem>
    suspend fun getCalificacionesUnidades(): List<CalificacionParcial>
    suspend fun getCalificacionesFinales(modoEducativo: Int = 1): List<CalificacionFinal>
    suspend fun getCargaAcademica(): List<MateriaCarga>
}

class NetworSNRepository(
    private val snApiService: SICENETWService
) : SNRepository {

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
            val jsonCrudo = extraerJson(response.string(), "{", "}")
            val jsonLimpio = jsonCrudo.replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">")

            // Casteo directo en el Repositorio
            Gson().fromJson(jsonLimpio, ProfileStudent::class.java)
        } catch (e: Exception) {
            Log.e("RXML", "Error en profile: ${e.message}")
            throw e
        }
    }

    override suspend fun getKardex(lineamiento: String): List<KardexItem> {
        return try {
            val requestBody = bodyKardex.format(lineamiento).toRequestBody()
            val response = snApiService.getKardex(requestBody)

            val jsonCrudo = extraerJson(response.string(), "[", "]")
            val jsonLimpio = jsonCrudo.replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">")

            if (jsonLimpio.isBlank() || jsonLimpio == "[]") return emptyList()

            val itemType = object : TypeToken<List<KardexItem>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) {
            Log.e("SICENET_DEBUG", "Error en Repositorio (Kardex): ${e.message}")
            emptyList()
        }
    }

    override suspend fun getCalificacionesUnidades(): List<CalificacionParcial> {
        return try {
            val response = snApiService.getCalifUnidades(bodyCalifUnidades.toRequestBody())
            val jsonCrudo = extraerJson(response.string(), "[", "]")
            val jsonLimpio = jsonCrudo.replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">")

            if (jsonLimpio.isBlank() || jsonLimpio == "[]") return emptyList()

            val itemType = object : TypeToken<List<CalificacionParcial>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) {
            Log.e("RXML", "Error Unidades: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getCalificacionesFinales(modoEducativo: Int): List<CalificacionFinal> {
        return try {
            val response = snApiService.getCalifFinales(bodyCalifFinales.format(modoEducativo).toRequestBody())
            val jsonCrudo = extraerJson(response.string(), "[", "]")
            val jsonLimpio = jsonCrudo.replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">")

            if (jsonLimpio.isBlank() || jsonLimpio == "[]") return emptyList()

            val itemType = object : TypeToken<List<CalificacionFinal>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) {
            Log.e("RXML", "Error Finales: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getCargaAcademica(): List<MateriaCarga> {
        return try {
            val requestBody = bodyCargaAcademica.toRequestBody()
            val response = snApiService.getCargaAcademica(requestBody)

            val jsonCrudo = extraerJson(response.string(), "[", "]")
            val jsonLimpio = jsonCrudo.replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">")

            if (jsonLimpio.isBlank() || jsonLimpio == "[]") return emptyList()

            val itemType = object : TypeToken<List<MateriaCarga>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) {
            Log.e("RXML", "Error Carga Académica: ${e.message}")
            emptyList()
        }
    }

    private fun extraerJson(xmlString: String, inicio: String, fin: String): String {
        val cleanXml = xmlString
        val startIndex = cleanXml.indexOf(inicio)
        val endIndex = cleanXml.lastIndexOf(fin)

        return if (startIndex != -1 && endIndex != -1) {
            cleanXml.substring(startIndex, endIndex + 1)
        } else {
            Log.e("RXML", "No se encontró JSON en la respuesta.")
            if (inicio == "[") "[]" else "{}"
        }
    }
}

class DBLocalSNRepository(val apiDB : Any): SNRepository {
    override suspend fun acceso(m: String, p: String, t: String): String = ""
    override suspend fun accesoObjeto(m: String, p: String): Usuario = Usuario(matricula = "")

    // Devolvemos excepciones o listas vacías porque es una DB local de prueba
    override suspend fun profile(m: String, p: String): ProfileStudent = throw NotImplementedError()
    override suspend fun getKardex(lineamiento: String): List<KardexItem> = emptyList()
    override suspend fun getCalificacionesUnidades(): List<CalificacionParcial> = emptyList()
    override suspend fun getCalificacionesFinales(modoEducativo: Int): List<CalificacionFinal> = emptyList()
    override suspend fun getCargaAcademica(): List<MateriaCarga> = emptyList()
}