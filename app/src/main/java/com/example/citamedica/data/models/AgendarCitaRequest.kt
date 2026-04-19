package com.example.citamedica.data.models

import com.google.gson.annotations.SerializedName

data class AgendarCitaRequest(
    @SerializedName("id_cita")
    val idCita: Int,

    @SerializedName("id_paciente")
    val idPaciente: Int,

    @SerializedName("id_doctor")
    val idDoctor: Int,

    @SerializedName("motivo_cita")
    val motivoCita: String,

    val estado: String,
    val fecha: String,
    val hora: String
)
