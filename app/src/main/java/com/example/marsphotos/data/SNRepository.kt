package com.example.marsphotos.data

import android.util.Log
import com.example.marsphotos.model.MateriaCarga // <--- IMPORTANTE: Importar el modelo
import com.example.marsphotos.model.Usuario
import com.example.marsphotos.network.SICENETWService
import com.example.marsphotos.network.bodyAlumno
import com.example.marsphotos.network.bodyKardex
import com.example.marsphotos.network.bodyacceso
import com.example.marsphotos.network.bodyCalifUnidades
import com.example.marsphotos.network.bodyCalifFinales
import com.example.marsphotos.network.bodyCargaAcademica
import com.google.gson.Gson // <--- IMPORTANTE: Importar Gson
import com.google.gson.reflect.TypeToken // <--- IMPORTANTE: Importar TypeToken
import okhttp3.RequestBody.Companion.toRequestBody

interface SNRepository {
    suspend fun acceso(m: String, p: String, t: String): String
    suspend fun accesoObjeto(m: String, p: String): Usuario
    suspend fun profile(m: String, p: String): String
    suspend fun getKardex(lineamiento: String = "1"): String
    suspend fun getCalificacionesUnidades(): String
    suspend fun getCalificacionesFinales(modoEducativo: Int = 1): String

    // CAMBIO: Ahora devuelve la Lista de objetos, no un String
    suspend fun getCargaAcademica(): List<MateriaCarga>
}

class NetworSNRepository(
    private val snApiService: SICENETWService
) : SNRepository {

    // ... (Tus métodos acceso, accesoObjeto, profile, getKardex, etc. se quedan IGUAL) ...
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
    // ... (Fin de métodos anteriores)

    // --- ACTUALIZACIÓN CARGA ACADÉMICA (CORREGIDA PARA EL MAESTRO) ---
    override suspend fun getCargaAcademica(): List<MateriaCarga> {
        return try {
            val requestBody = bodyCargaAcademica.toRequestBody()
            val response = snApiService.getCargaAcademica(requestBody)
            val xmlResponse = response.string()

            Log.d("DEBUG_CARGA", "Respuesta XML Carga: $xmlResponse")

            // 1. Extraemos el texto crudo del XML
            val jsonExtraido = extraerJson(xmlResponse, "[", "]")

            // 2. Limpiamos caracteres sucios (esto antes lo hacías en el ViewModel)
            val jsonLimpio = jsonExtraido
                .replace("&quot;", "\"")
                .replace("&lt;", "<")
                .replace("&gt;", ">")

            if (jsonLimpio.isBlank() || jsonLimpio == "[]") return emptyList()

            // 3. Convertimos a Objeto AQUÍ en el Repository
            val gson = Gson()
            val itemType = object : TypeToken<List<MateriaCarga>>() {}.type
            val lista: List<MateriaCarga> = gson.fromJson(jsonLimpio, itemType)

            lista // Retornamos la lista ya hecha

        } catch (e: Exception) {
            Log.e("RXML", "Error Carga Académica: ${e.message}")
            emptyList() // En caso de error, devolvemos lista vacía
        }
    }

    private fun extraerJson(xmlString: String, inicio: String, fin: String): String {
        // Tu lógica de extracción se queda igual, aunque la limpieza extra la moví arriba
        val cleanXml = xmlString // Ya limpiamos arriba en getCargaAcademica, o puedes dejarlo aquí
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
    // ... otros métodos ...
    override suspend fun acceso(m: String, p: String, t: String): String = ""
    override suspend fun accesoObjeto(m: String, p: String): Usuario = Usuario(matricula = "")
    override suspend fun profile(m: String, p: String): String = ""
    override suspend fun getKardex(lineamiento: String): String = "[]"
    override suspend fun getCalificacionesUnidades(): String = "[]"
    override suspend fun getCalificacionesFinales(modoEducativo: Int): String = "[]"

    // CAMBIO: Debe devolver lista vacía, no string
    override suspend fun getCargaAcademica(): List<MateriaCarga> = emptyList()
}