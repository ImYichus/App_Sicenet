package com.example.marsphotos.data

import android.util.Log
import com.example.marsphotos.data.local.SicenetDao
import com.example.marsphotos.model.*
import com.example.marsphotos.network.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody.Companion.toRequestBody

interface SNRepository {
    // Métodos de Red
    suspend fun acceso(m: String, p: String, t: String): String
    suspend fun accesoObjeto(m: String, p: String): Usuario
    suspend fun profile(m: String, p: String): ProfileStudent
    suspend fun getKardex(lineamiento: String = "1"): List<KardexItem>
    suspend fun getCalificacionesUnidades(): List<CalificacionParcial>
    suspend fun getCalificacionesFinales(modoEducativo: Int = 1): List<CalificacionFinal>
    suspend fun getCargaAcademica(): List<MateriaCarga>

    // Métodos de Base de Datos
    suspend fun getPerfil(): ProfileStudent?
    suspend fun insertPerfil(perfil: ProfileStudent)
    suspend fun insertKardex(lista: List<KardexItem>)
    suspend fun insertCarga(lista: List<MateriaCarga>)
    suspend fun insertParciales(lista: List<CalificacionParcial>)
    suspend fun insertFinales(lista: List<CalificacionFinal>)
}

class NetworSNRepository(
    private val snApiService: SICENETWService
) : SNRepository {

    // Funciones auxiliares de limpieza
    private fun limpiarJson(json: String): String {
        return json
            .replace("&quot;", "\"")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("\\\"", "\"")
            .trim()
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

    // Implementaciones de Red
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

    // Busca esta función dentro de NetworSNRepository en SNRepository.kt y reemplázala
    override suspend fun profile(m: String, p: String): ProfileStudent {
        return try {
            val response = snApiService.getPerfil(bodyAlumno.format(m, p).toRequestBody())
            val jsonLimpio = limpiarJson(extraerJson(response.string(), "{", "}"))

            // Usamos una limpieza extra para asegurar que Gson no falle
            if (!jsonLimpio.contains("{")) throw Exception("JSON de perfil inválido")

            val perfil = Gson().fromJson(jsonLimpio, ProfileStudent::class.java)

            // Si el nombre viene vacío por error de mapeo, forzamos un valor para que no se vea blanco
            if (perfil.nombre.isBlank()) perfil.copy(nombre = "Usuario Estudiante") else perfil
        } catch (e: Exception) {
            Log.e("RXML", "Error en profile: ${e.message}")
            ProfileStudent("Error al cargar", m, false, "Desconocida")
        }
    }

    override suspend fun getKardex(lineamiento: String): List<KardexItem> {
        return try {
            val response = snApiService.getKardex(bodyKardex.format(lineamiento).toRequestBody())
            val jsonLimpio = limpiarJson(extraerJson(response.string(), "[", "]"))

            // EL ESCUDO CORREGIDO: A prueba de espacios y saltos de línea
            if (jsonLimpio.isEmpty() || jsonLimpio == "[]" || !jsonLimpio.contains("{")) return emptyList()

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
            val inicio = xmlString.indexOf("[")
            val fin = xmlString.lastIndexOf("]")

            if (inicio == -1 || fin == -1) return emptyList()

            val jsonBruto = xmlString.substring(inicio, fin + 1)
            val jsonLimpio = jsonBruto.replace("&quot;", "\"").replace("\\\"", "\"").replace("\"{", "{").replace("}\"", "}").trim()

            // EL ESCUDO CORREGIDO
            if (jsonLimpio.isEmpty() || jsonLimpio == "[]" || !jsonLimpio.contains("{")) return emptyList()

            val itemType = object : TypeToken<List<CalificacionParcial>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getCalificacionesFinales(modoEducativo: Int): List<CalificacionFinal> {
        return try {
            val response = snApiService.getCalifFinales(bodyCalifFinales.format(modoEducativo).toRequestBody())
            val jsonLimpio = limpiarJson(extraerJson(response.string(), "[", "]"))

            // EL ESCUDO CORREGIDO
            if (jsonLimpio.isEmpty() || jsonLimpio == "[]" || !jsonLimpio.contains("{")) return emptyList()

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

            // EL ESCUDO CORREGIDO
            if (jsonLimpio.isEmpty() || jsonLimpio == "[]" || !jsonLimpio.contains("{")) return emptyList()

            val itemType = object : TypeToken<List<MateriaCarga>>() {}.type
            Gson().fromJson(jsonLimpio, itemType)
        } catch (e: Exception) {
            Log.e("RXML", "Error Carga: ${e.message}")
            emptyList()
        }
    }

    // Implementaciones vacías para la Red (No guardan en DB)
    override suspend fun getPerfil(): ProfileStudent? = null
    override suspend fun insertPerfil(perfil: ProfileStudent) {}
    override suspend fun insertKardex(lista: List<KardexItem>) {}
    override suspend fun insertCarga(lista: List<MateriaCarga>) {}
    override suspend fun insertParciales(lista: List<CalificacionParcial>) {}
    override suspend fun insertFinales(lista: List<CalificacionFinal>) {}
}

class DBLocalSNRepository(private val dao: SicenetDao) : SNRepository {
    // Red (No implementada en Local)
    override suspend fun acceso(m: String, p: String, t: String): String = ""
    override suspend fun accesoObjeto(m: String, p: String): Usuario = Usuario("")
    override suspend fun profile(m: String, p: String): ProfileStudent = throw NotImplementedError()

    // Consultas Locales
    override suspend fun getPerfil(): ProfileStudent? = dao.getPerfil()
    override suspend fun getKardex(lineamiento: String): List<KardexItem> = dao.getAllKardex()
    override suspend fun getCalificacionesUnidades(): List<CalificacionParcial> = dao.getParciales()
    override suspend fun getCalificacionesFinales(modoEducativo: Int): List<CalificacionFinal> = dao.getFinales()
    override suspend fun getCargaAcademica(): List<MateriaCarga> = dao.getCarga()

    // Inserciones Locales
    override suspend fun insertPerfil(perfil: ProfileStudent) = dao.insertPerfil(perfil)
    override suspend fun insertKardex(lista: List<KardexItem>) = dao.insertKardex(lista)
    override suspend fun insertCarga(lista: List<MateriaCarga>) = dao.insertCarga(lista)
    override suspend fun insertParciales(lista: List<CalificacionParcial>) = dao.insertParciales(lista)
    override suspend fun insertFinales(lista: List<CalificacionFinal>) = dao.insertFinales(lista)
}