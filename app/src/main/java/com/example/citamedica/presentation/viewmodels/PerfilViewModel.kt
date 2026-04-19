package com.example.citamedica.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.citamedica.data.models.MensajeResponse
import com.example.citamedica.data.models.Perfil
import com.example.citamedica.data.repository.CitaMedicaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilViewModel : ViewModel() {

    private val repository = CitaMedicaRepository()

    var isLoading by mutableStateOf(false)
        private set

    var perfil by mutableStateOf<Perfil?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isActualizando by mutableStateOf(false)
        private set

    var mensajeActualizacion by mutableStateOf<String?>(null)
        private set

    fun cargarPerfil(idUsuario: Int) {
        isLoading = true
        errorMessage = null

        repository.obtenerPerfil(idUsuario).enqueue(object : Callback<Perfil> {
            override fun onResponse(call: Call<Perfil>, response: Response<Perfil>) {
                isLoading = false
                if (response.isSuccessful) {
                    perfil = response.body()
                } else {
                    errorMessage = "No se pudo cargar el perfil"
                }
            }

            override fun onFailure(call: Call<Perfil>, t: Throwable) {
                isLoading = false
                errorMessage = t.message ?: "Error de conexión"
            }
        })
    }

    fun actualizarPerfil(
        idUsuario: Int,
        nombre: String,
        edad: String,
        sexo: String,
        telefono: String
    ) {
        isActualizando = true
        errorMessage = null

        repository.actualizarPerfil(
            idUsuario = idUsuario,
            nombre = nombre,
            edad = edad,
            sexo = sexo,
            telefono = telefono
        ).enqueue(object : Callback<MensajeResponse> {
            override fun onResponse(
                call: Call<MensajeResponse>,
                response: Response<MensajeResponse>
            ) {
                isActualizando = false
                if (response.isSuccessful) {
                    mensajeActualizacion = response.body()?.mensaje
                        ?: "Perfil actualizado correctamente"
                } else {
                    errorMessage = "No fue posible actualizar el perfil"
                }
            }

            override fun onFailure(call: Call<MensajeResponse>, t: Throwable) {
                isActualizando = false
                errorMessage = t.message ?: "Error de conexión"
            }
        })
    }

    fun onMensajeActualizacionMostrado() {
        mensajeActualizacion = null
    }

    fun onErrorMostrado() {
        errorMessage = null
    }
}
