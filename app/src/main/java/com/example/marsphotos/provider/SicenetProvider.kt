package com.example.marsphotos.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.marsphotos.data.local.SicenetDatabase

class SicenetProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.marsphotos.provider"
        const val KARDEX = 1
        const val CARGA = 2

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "kardex", KARDEX)
            addURI(AUTHORITY, "carga", CARGA)
        }
    }

    override fun onCreate(): Boolean {
        return true // Se inicializa correctamente
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val db = SicenetDatabase.getDatabase(context!!)
        val dao = db.sicenetDao()

        val cursor = when (uriMatcher.match(uri)) {
            KARDEX -> dao.getKardexCursor()
            CARGA -> dao.getCargaCursor()
            else -> throw IllegalArgumentException("URI no soportada: $uri")
        }

        // Notifica a los observadores si hay cambios en los datos
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // Lanzamos esta excepción para comprobar el mecanismo en tu app cliente
        throw UnsupportedOperationException("Escritura bloqueada temporalmente por el Provider")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Actualización no permitida")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Eliminación no permitida")
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            KARDEX -> "vnd.android.cursor.dir/vnd.$AUTHORITY.kardex"
            CARGA -> "vnd.android.cursor.dir/vnd.$AUTHORITY.carga"
            else -> null
        }
    }
}