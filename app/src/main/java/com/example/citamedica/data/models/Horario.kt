package com.example.citamedica.data.models

import com.google.gson.annotations.SerializedName

data class Horario(
    @SerializedName("id_horario")
    val idHorario: Int,

    @SerializedName("id_doctor")
    val idDoctor: Int,

    @SerializedName("dia_semana")
    val diaSemana: String,

    @SerializedName("hora_inicio")
    val horaInicio: String,

    @SerializedName("hora_fin")
    val horaFin: String,

    val disponible: Boolean
)
