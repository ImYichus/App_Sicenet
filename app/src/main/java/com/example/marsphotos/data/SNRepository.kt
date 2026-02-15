package com.example.marsphotos.data

import android.util.Log
import com.example.marsphotos.model.Usuario
import com.example.marsphotos.network.SICENETWService
import com.example.marsphotos.network.bodyAlumno
import com.example.marsphotos.network.bodyKardex
import com.example.marsphotos.network.bodyacceso
import com.example.marsphotos.network.bodyCalifUnidades
import com.example.marsphotos.network.bodyCalifFinales
import com.example.marsphotos.network.bodyCargaAcademica
import okhttp3.RequestBody.Companion.toRequestBody

interface SNRepository {
    suspend fun acceso(m: String, p: String, t: String): String
    suspend fun accesoObjeto(m: String, p: String): Usuario
    suspend fun profile(m: String, p: String): String
    suspend fun getKardex(lineamiento: String = "1"): String
    suspend fun getCalificacionesUnidades(): String
    suspend fun getCalificacionesFinales(modoEducativo: Int = 1): String
    suspend fun getCargaAcademica(): String
}

class NetworSNRepository(
    private val snApiService: SICENETWService
) : SNRepository {

    // ... (metodos anteriores de acceso, profile, kardex, etc se mantienen igual)

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

    override suspend fun profile(m: String, p: String): String {
        return try {
            val response = snApiService.getPerfil(bodyAlumno.format(m, p).toRequestBody())
            extraerJson(response.string(), "{", "}")
        } catch (e: Exception) { throw e }
    }

    override suspend fun getKardex(lineamiento: String): String {
        return try {
            val response = snApiService.getKardex(bodyKardex.format(lineamiento).toRequestBody())
            extraerJson(response.string(), "[", "]")
        } catch (e: Exception) { "[]" }
    }

    override suspend fun getCalificacionesUnidades(): String {
        return try {
            val response = snApiService.getCalifUnidades(bodyCalifUnidades.toRequestBody())
            extraerJson(response.string(), "[", "]")
        } catch (e: Exception) { "[]" }
    }

    override suspend fun getCalificacionesFinales(modoEducativo: Int): String {
        return try {
            val response = snApiService.getCalifFinales(bodyCalifFinales.format(modoEducativo).toRequestBody())
            extraerJson(response.string(), "[", "]")
        } catch (e: Exception) { "[]" }
    }

    // --- ACTUALIZACIÓN CARGA ACADÉMICA ---
    override suspend fun getCargaAcademica(): String {
        return try {
            // SICENET requiere que el body esté presente aunque sea vacío para este método
            val requestBody = bodyCargaAcademica.toRequestBody()
            val response = snApiService.getCargaAcademica(requestBody)
            val xmlResponse = response.string()

            // Log para debug: Verifica si el XML trae algo en getCargaAcademicaByAlumnoResult
            Log.d("DEBUG_CARGA", "Respuesta XML Carga: $xmlResponse")

            val jsonExtraido = extraerJson(xmlResponse, "[", "]")

            // Si el JSON extraído está vacío o mal formado, devolvemos un array vacío seguro
            if (jsonExtraido.isBlank()) "[]" else jsonExtraido
        } catch (e: Exception) {
            Log.e("RXML", "Error Carga Académica: ${e.message}")
            "[]"
        }
    }

    private fun extraerJson(xmlString: String, inicio: String, fin: String): String {
        // SICENET a veces devuelve caracteres escapados (&quot;). Los limpiamos.
        val cleanXml = xmlString.replace("&quot;", "\"")
            .replace("&lt;", "<")
            .replace("&gt;", ">")

        val startIndex = cleanXml.indexOf(inicio)
        val endIndex = cleanXml.lastIndexOf(fin)

        return if (startIndex != -1 && endIndex != -1) {
            cleanXml.substring(startIndex, endIndex + 1)
        } else {
            Log.e("RXML", "No se encontró JSON en la respuesta. Raw: $xmlString")
            if (inicio == "[") "[]" else "{}"
        }
    }
}

class DBLocalSNRepository(val apiDB : Any): SNRepository {
    override suspend fun acceso(m: String, p: String, t: String): String = ""
    override suspend fun accesoObjeto(m: String, p: String): Usuario = Usuario(matricula = "")
    override suspend fun profile(m: String, p: String): String = ""
    override suspend fun getKardex(lineamiento: String): String = "[]"
    override suspend fun getCalificacionesUnidades(): String = "[]"
    override suspend fun getCalificacionesFinales(modoEducativo: Int): String = "[]"
    override suspend fun getCargaAcademica(): String = "[]"
}