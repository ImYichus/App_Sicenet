package com.example.marsphotos.ui.screens

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.marsphotos.MarsPhotosApplication
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface SNUiState {
    data class Success(
        val data: ProfileStudent,
        val kardex: List<KardexItem> = emptyList(),
        val califUnidades: List<CalificacionParcial> = emptyList(),
        val califFinales: List<CalificacionFinal> = emptyList(),
        val cargaAcademica: List<MateriaCarga> = emptyList()
    ) : SNUiState
    object Error : SNUiState
    object Loading : SNUiState
    object Idle : SNUiState
}

class SNViewModel(private val snRepository: SNRepository) : ViewModel() {

    var snUiState: SNUiState by mutableStateOf(SNUiState.Idle)
        private set

    fun logout(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().remove("PREF_COOKIES").apply()
        snUiState = SNUiState.Idle
    }

    fun loginYConsultarPerfil(matricula: String, contrasenia: String, tipo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            snUiState = SNUiState.Loading
            try {
                val loginResponse = snRepository.acceso(matricula, contrasenia, tipo)
                if (loginResponse.contains("\"acceso\":true")) {
                    val perfilJson = snRepository.profile(matricula, contrasenia)
                    val perfilObjeto = Gson().fromJson(perfilJson, ProfileStudent::class.java)
                    withContext(Dispatchers.Main) {
                        snUiState = SNUiState.Success(data = perfilObjeto)
                    }
                } else {
                    withContext(Dispatchers.Main) { snUiState = SNUiState.Error }
                }
            } catch (e: Exception) {
                Log.e("SICENET_ERROR", "Login Error: ${e.message}")
                withContext(Dispatchers.Main) { snUiState = SNUiState.Error }
            }
        }
    }

    // --- FUNCIÓN ACTUALIZADA: CONSULTAR CARGA ACADÉMICA ---
    fun consultarCargaAcademica() {
        val currentState = snUiState
        if (currentState is SNUiState.Success) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val rawResponse = snRepository.getCargaAcademica()

                    // 1. Limpieza profunda del JSON (quita escapes de XML)
                    val jsonString = rawResponse
                        .replace("&quot;", "\"")
                        .replace("&lt;", "<")
                        .replace("&gt;", ">")

                    Log.d("SICENET_DEBUG", "JSON de Carga: $jsonString")

                    val itemType = object : TypeToken<List<MateriaCarga>>() {}.type
                    val lista: List<MateriaCarga> = Gson().fromJson(jsonString, itemType)

                    // 2. Actualizamos el estado en el hilo principal (Main) para que la UI reaccione
                    withContext(Dispatchers.Main) {
                        snUiState = currentState.copy(cargaAcademica = lista)
                    }
                } catch (e: Exception) {
                    Log.e("SICENET_DEBUG", "Error Carga Académica: ${e.message}")
                }
            }
        }
    }

    // Se recomienda aplicar el mismo patrón de withContext(Dispatchers.Main) a los demás métodos
    fun consultarKardex() {
        val currentState = snUiState
        if (currentState is SNUiState.Success) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val jsonString = snRepository.getKardex().replace("&quot;", "\"")
                    val itemType = object : TypeToken<List<KardexItem>>() {}.type
                    val listaKardex: List<KardexItem> = Gson().fromJson(jsonString, itemType)
                    withContext(Dispatchers.Main) {
                        snUiState = currentState.copy(kardex = listaKardex)
                    }
                } catch (e: Exception) {
                    Log.e("SICENET_DEBUG", "Error Kardex: ${e.message}")
                }
            }
        }
    }

    fun consultarCalificacionesUnidades() {
        val currentState = snUiState
        if (currentState is SNUiState.Success) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val jsonString = snRepository.getCalificacionesUnidades().replace("&quot;", "\"")
                    val itemType = object : TypeToken<List<CalificacionParcial>>() {}.type
                    val lista: List<CalificacionParcial> = Gson().fromJson(jsonString, itemType)
                    withContext(Dispatchers.Main) {
                        snUiState = currentState.copy(califUnidades = lista)
                    }
                } catch (e: Exception) {
                    Log.e("SICENET_DEBUG", "Error Unidades: ${e.message}")
                }
            }
        }
    }

    fun consultarCalificacionesFinales() {
        val currentState = snUiState
        if (currentState is SNUiState.Success) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val jsonString = snRepository.getCalificacionesFinales(1).replace("&quot;", "\"")
                    val itemType = object : TypeToken<List<CalificacionFinal>>() {}.type
                    val lista: List<CalificacionFinal> = Gson().fromJson(jsonString, itemType)
                    withContext(Dispatchers.Main) {
                        snUiState = currentState.copy(califFinales = lista)
                    }
                } catch (e: Exception) {
                    Log.e("SICENET_DEBUG", "Error Finales: ${e.message}")
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MarsPhotosApplication)
                val snRepository = application.container.snRepository
                SNViewModel(snRepository = snRepository)
            }
        }
    }
}