package com.example.marsphotos.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.marsphotos.MarsPhotosApplication

class LoginWorker(ctx: Context, params: WorkerParameters)
    : CoroutineWorker(ctx, params) {

    // Acceso al contenedor de dependencias
    private val container = (ctx.applicationContext as MarsPhotosApplication).container

    override suspend fun doWork(): Result {
        // 1. Recuperamos los datos que vienen desde el SNWMRepository
        val matricula = inputData.getString("KEY_MATRICULA") ?: ""
        val password = inputData.getString("KEY_PASSWORD") ?: ""
        val tipo = inputData.getString("KEY_TIPO") ?: "ALUMNO" // Valor por defecto

        return try {
            // 2. Llamamos al repositorio con los 3 parámetros requeridos
            val u = container.snRepository.acceso(matricula, password, tipo)

            // 3. Enviamos el resultado de vuelta
            Result.success(workDataOf("u" to u))
        } catch (e: Exception) {
            // En caso de error en la red o SOAP
            Result.failure(workDataOf("error" to e.localizedMessage))
        }
    }
}