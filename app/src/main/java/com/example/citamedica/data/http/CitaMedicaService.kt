package com.example.citamedica.data.http

import com.example.citamedica.data.models.ActualizarPerfilRequest
import com.example.citamedica.data.models.AgendarCitaRequest
import com.example.citamedica.data.models.AgendarCitaResponse
import com.example.citamedica.data.models.Cita
import com.example.citamedica.data.models.Doctor
import com.example.citamedica.data.models.Horario
import com.example.citamedica.data.models.LoginRequest
import com.example.citamedica.data.models.MensajeResponse
import com.example.citamedica.data.models.Perfil
import com.example.citamedica.data.models.RegisterRequest
import com.example.citamedica.data.models.RegisterResponse
import com.example.citamedica.data.models.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CitaMedicaService {

    @POST("auth/login")
    fun iniciarSesion(@Body body: LoginRequest): Call<Usuario>

    @POST("auth/register")
    fun registrar(@Body body: RegisterRequest): Call<RegisterResponse>

    @GET("pacientes/{id}/citas")
    fun obtenerCitasPaciente(@Path("id") idPaciente: Int): Call<List<Cita>>

    @GET("pacientes/doctores")
    fun obtenerDoctores(): Call<List<Doctor>>

    @GET("doctores/{id}/horarios")
    fun obtenerHorariosDoctor(@Path("id") idDoctor: Int): Call<List<Horario>>

    @POST("pacientes/agendar")
    fun agendarCita(@Body body: AgendarCitaRequest): Call<AgendarCitaResponse>

    @DELETE("citas/{id}")
    fun eliminarCita(@Path("id") idCita: Int): Call<MensajeResponse>

    @GET("usuarios/{id}/perfil")
    fun obtenerPerfil(@Path("id") idUsuario: Int): Call<Perfil>

    @PUT("usuarios/{id}")
    fun actualizarPerfil(
        @Path("id") idUsuario: Int,
        @Body body: ActualizarPerfilRequest
    ): Call<MensajeResponse>
}
