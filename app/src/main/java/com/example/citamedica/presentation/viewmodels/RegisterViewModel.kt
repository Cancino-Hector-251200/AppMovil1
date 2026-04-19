package com.example.citamedica.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.citamedica.data.models.RegisterResponse
import com.example.citamedica.data.repository.CitaMedicaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val repository = CitaMedicaRepository()

    var isLoading by mutableStateOf(false)
        private set

    var registerSuccess by mutableStateOf<RegisterResponse?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun registrar(nombre: String, correo: String, password: String) {
        isLoading = true
        errorMessage = null

        repository.registrar(
            nombre = nombre,
            correo = correo,
            password = password,
            rol = "PACIENTE"
        ).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                isLoading = false
                val body = response.body()
                if (response.isSuccessful && body != null && body.estado == "exito") {
                    registerSuccess = body
                } else {
                    errorMessage = body?.mensaje ?: "No fue posible registrar el usuario"
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                isLoading = false
                errorMessage = t.message ?: "Error de conexión"
            }
        })
    }

    fun onRegistroManejado() {
        registerSuccess = null
    }

    fun onErrorMostrado() {
        errorMessage = null
    }
}
