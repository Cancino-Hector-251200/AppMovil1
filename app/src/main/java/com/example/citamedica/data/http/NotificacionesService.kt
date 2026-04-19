package com.example.citamedica.data.http

import com.example.citamedica.data.models.EliminarTokenRequest
import com.example.citamedica.data.models.MensajeResponse
import com.example.citamedica.data.models.RegistrarTokenRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificacionesService {

    @POST("api/register-token")
    fun registrarTokenFcm(@Body body: RegistrarTokenRequest): Call<MensajeResponse>

    @POST("api/delete-token")
    fun eliminarTokenFcm(@Body body: EliminarTokenRequest): Call<MensajeResponse>
}
