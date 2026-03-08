package com.example.marsphotos.data.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.marsphotos.data.local.SicenetDatabase // Importa tu clase de base de datos Room

class SiceContentProvider : ContentProvider() {

    private val KARDEX = 100
    private val CARGA_ACADEMICA = 200

    // Configuración del UriMatcher para saber qué tabla nos están pidiendo
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, "kardex", KARDEX)
        addURI(AUTHORITY, "carga_academica", CARGA_ACADEMICA)
    }

    override fun onCreate(): Boolean {
        // Retornamos true indicando que el provider se cargó correctamente
        return true
    }

    // ==========================================
    // MÉTODO READ (LECTURA)
    // ==========================================
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val context = context ?: return null

        // Obtenemos la instancia de tu base de datos Room
        // Asegúrate de usar el método correcto con el que inicializas tu Room DB
        val database = SicenetDatabase.getDatabase(context)

        return when (uriMatcher.match(uri)) {
            KARDEX -> {
                // Llamamos a la función que devuelve el Cursor en el DAO
                database.sicenetDao().getKardexCursor()
            }
            CARGA_ACADEMICA -> {
                // Llamamos a la función que devuelve el Cursor en el DAO
                database.sicenetDao().getCargaCursor()
            }
            else -> throw IllegalArgumentException("URI desconocida: $uri")
        }
    }

    // ==========================================
    // MÉTODOS WRITE (ESCRITURA)
    // ==========================================
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // Aquí implementarías la lógica para insertar datos usando Room
        // O lanzar una excepción si solo quieres que sea de solo lectura por ahora
        throw UnsupportedOperationException("Operación de inserción no soportada aún")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException("Operación de actualización no soportada aún")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("Operación de borrado no soportada aún")
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            KARDEX -> "vnd.android.cursor.dir/vnd.$AUTHORITY.kardex"
            CARGA_ACADEMICA -> "vnd.android.cursor.dir/vnd.$AUTHORITY.carga_academica"
            else -> throw IllegalArgumentException("URI desconocida: $uri")
        }
    }

    companion object {
        // La autoridad debe ser exactamente igual a la del AndroidManifest
        const val AUTHORITY = "com.example.marsphotos.provider"

        // URIs públicas para que otras apps las consulten
        val URI_KARDEX: Uri = Uri.parse("content://$AUTHORITY/kardex")
        val URI_CARGA: Uri = Uri.parse("content://$AUTHORITY/carga_academica")
    }
}