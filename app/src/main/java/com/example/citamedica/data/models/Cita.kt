package com.example.citamedica.data.models

import com.google.gson.annotations.SerializedName

data class Cita(
    @SerializedName("id_cita")
    val idCita: Int,

    @SerializedName("id_paciente")
    val idPaciente: Int,

    @SerializedName("id_doctor")
    val idDoctor: Int,

    @SerializedName("nombre_paciente")
    val nombrePaciente: String?,

    @SerializedName("nombre_doctor")
    val nombreDoctor: String,

    @SerializedName("motivo_cita")
    val motivoCita: String,

    val estado: String,
    val fecha: String,
    val hora: String
)
