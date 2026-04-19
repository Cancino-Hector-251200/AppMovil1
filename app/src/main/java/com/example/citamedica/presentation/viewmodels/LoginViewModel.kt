package com.example.citamedica.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.citamedica.data.models.Usuario
import com.example.citamedica.data.repository.CitaMedicaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    private val repository = CitaMedicaRepository()

    var isLoading by mutableStateOf(false)
        private set

    var usuario by mutableStateOf<Usuario?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun iniciarSesion(correo: String, password: String) {
        isLoading = true
        errorMessage = null

        repository.iniciarSesion(correo, password).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                isLoading = false
                if (response.isSuccessful) {
                    usuario = response.body()
                } else {
                    errorMessage = "Correo o contraseña incorrectos"
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                isLoading = false
                errorMessage = t.message ?: "Error de conexión"
            }
        })
    }

    fun onUsuarioManejado() {
        usuario = null
    }

    fun onErrorMostrado() {
        errorMessage = null
    }
}
