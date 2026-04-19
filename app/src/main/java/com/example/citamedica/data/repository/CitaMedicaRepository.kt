package com.example.citamedica.data.repository

import com.example.citamedica.data.http.CitaMedicaService
import com.example.citamedica.data.http.NotificacionesService
import com.example.citamedica.data.http.RetrofitClient
import com.example.citamedica.data.models.ActualizarPerfilRequest
import com.example.citamedica.data.models.AgendarCitaRequest
import com.example.citamedica.data.models.AgendarCitaResponse
import com.example.citamedica.data.models.Cita
import com.example.citamedica.data.models.Doctor
import com.example.citamedica.data.models.EliminarTokenRequest
import com.example.citamedica.data.models.Horario
import com.example.citamedica.data.models.LoginRequest
import com.example.citamedica.data.models.MensajeResponse
import com.example.citamedica.data.models.Perfil
import com.example.citamedica.data.models.RegisterRequest
import com.example.citamedica.data.models.RegisterResponse
import com.example.citamedica.data.models.RegistrarTokenRequest
import com.example.citamedica.data.models.Usuario
import retrofit2.Call

class CitaMedicaRepository {

    private val service = RetrofitClient.retrofit.create(CitaMedicaService::class.java)
    private val notificacionesService = RetrofitClient.retrofitNotificaciones
        .create(NotificacionesService::class.java)

    fun iniciarSesion(correo: String, password: String): Call<Usuario> {
        return service.iniciarSesion(LoginRequest(correo, password))
    }

    fun registrar(
        nombre: String,
        correo: String,
        password: String,
        rol: String,
        especialidad: String = ""
    ): Call<RegisterResponse> {
        return service.registrar(
            RegisterRequest(
                correo = correo,
                password = password,
                rol = rol,
                nombre = nombre,
                especialidad = especialidad
            )
        )
    }

    fun obtenerCitasPaciente(idPaciente: Int): Call<List<Cita>> {
        return service.obtenerCitasPaciente(idPaciente)
    }

    fun obtenerDoctores(): Call<List<Doctor>> {
        return service.obtenerDoctores()
    }

    fun obtenerHorariosDoctor(idDoctor: Int): Call<List<Horario>> {
        return service.obtenerHorariosDoctor(idDoctor)
    }

    fun agendarCita(
        idHorario: Int,
        idPaciente: Int,
        idDoctor: Int,
        motivoCita: String,
        diaSemana: String,
        horaInicio: String
    ): Call<AgendarCitaResponse> {
        return service.agendarCita(
            AgendarCitaRequest(
                idCita = idHorario,
                idPaciente = idPaciente,
                idDoctor = idDoctor,
                motivoCita = motivoCita,
                estado = "PENDIENTE",
                fecha = diaSemana,
                hora = horaInicio
            )
        )
    }

    fun eliminarCita(idCita: Int): Call<MensajeResponse> {
        return service.eliminarCita(idCita)
    }

    fun obtenerPerfil(idUsuario: Int): Call<Perfil> {
        return service.obtenerPerfil(idUsuario)
    }

    fun actualizarPerfil(
        idUsuario: Int,
        nombre: String,
        edad: String,
        sexo: String,
        telefono: String
    ): Call<MensajeResponse> {
        return service.actualizarPerfil(
            idUsuario,
            ActualizarPerfilRequest(
                nombre = nombre,
                edad = edad,
                sexo = sexo,
                telefono = telefono
            )
        )
    }

    fun registrarTokenFcm(idPaciente: Int, fcmToken: String): Call<MensajeResponse> {
        return notificacionesService.registrarTokenFcm(
            RegistrarTokenRequest(
                patientId = idPaciente.toString(),
                fcmToken = fcmToken
            )
        )
    }

    fun eliminarTokenFcm(idPaciente: Int): Call<MensajeResponse> {
        return notificacionesService.eliminarTokenFcm(
            EliminarTokenRequest(patientId = idPaciente.toString())
        )
    }
}
