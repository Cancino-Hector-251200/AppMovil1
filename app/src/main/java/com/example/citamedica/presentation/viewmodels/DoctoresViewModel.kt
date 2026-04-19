package com.example.citamedica.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.citamedica.data.models.Doctor
import com.example.citamedica.data.repository.CitaMedicaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctoresViewModel : ViewModel() {

    private val repository = CitaMedicaRepository()

    var isLoading by mutableStateOf(false)
        private set

    var doctores by mutableStateOf<List<Doctor>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun cargarDoctores() {
        isLoading = true
        errorMessage = null

        repository.obtenerDoctores().enqueue(object : Callback<List<Doctor>> {
            override fun onResponse(
                call: Call<List<Doctor>>,
                response: Response<List<Doctor>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    doctores = response.body() ?: emptyList()
                } else {
                    errorMessage = "No se pudieron cargar los doctores"
                }
            }

            override fun onFailure(call: Call<List<Doctor>>, t: Throwable) {
                isLoading = false
                errorMessage = t.message ?: "Error de conexión"
            }
        })
    }

    fun onErrorMostrado() {
        errorMessage = null
    }
}
