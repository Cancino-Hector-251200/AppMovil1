package com.example.citamedica.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.citamedica.data.models.Cita
import com.example.citamedica.data.models.MensajeResponse
import com.example.citamedica.data.repository.CitaMedicaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CitasViewModel : ViewModel() {

    private val repository = CitaMedicaRepository()

    var isLoading by mutableStateOf(false)
        private set

    var citas by mutableStateOf<List<Cita>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isEliminando by mutableStateOf(false)
        private set

    var citaEliminadaMensaje by mutableStateOf<String?>(null)
        private set

    fun cargarCitas(idPaciente: Int) {
        isLoading = true
        errorMessage = null

        repository.obtenerCitasPaciente(idPaciente).enqueue(object : Callback<List<Cita>> {
            override fun onResponse(call: Call<List<Cita>>, response: Response<List<Cita>>) {
                isLoading = false
                if (response.isSuccessful) {
                    citas = response.body()
                        ?.sortedWith(
                            compareBy({ ordenDiaSemana(it.fecha) }, { it.hora })
                        ) ?: emptyList()
                } else {
                    errorMessage = "No se pudieron cargar las citas"
                }
            }

            override fun onFailure(call: Call<List<Cita>>, t: Throwable) {
                isLoading = false
                errorMessage = t.message ?: "Error de conexión"
            }
        })
    }

    fun eliminarCita(idCita: Int) {
        isEliminando = true
        errorMessage = null

        repository.eliminarCita(idCita).enqueue(object : Callback<MensajeResponse> {
            override fun onResponse(
                call: Call<MensajeResponse>,
                response: Response<MensajeResponse>
            ) {
                isEliminando = false
                if (response.isSuccessful) {
                    citas = citas.filterNot { it.idCita == idCita }
                    citaEliminadaMensaje = response.body()?.mensaje ?: "Cita eliminada"
                } else {
                    errorMessage = "No se pudo eliminar la cita"
                }
            }

            override fun onFailure(call: Call<MensajeResponse>, t: Throwable) {
                isEliminando = false
                errorMessage = t.message ?: "Error de conexión"
            }
        })
    }

    fun onCitaEliminadaMensajeMostrado() {
        citaEliminadaMensaje = null
    }

    fun onErrorMostrado() {
        errorMessage = null
    }

    private fun ordenDiaSemana(dia: String): Int = when (dia.lowercase()) {
        "lunes" -> 1
        "martes" -> 2
        "miercoles", "miércoles" -> 3
        "jueves" -> 4
        "viernes" -> 5
        "sabado", "sábado" -> 6
        "domingo" -> 7
        else -> 99
    }
}
