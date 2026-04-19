package com.example.citamedica.data.models

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id_usuario")
    val idUsuario: Int,

    @SerializedName("id_especifico")
    val idEspecifico: Int,

    @SerializedName("id_paciente")
    val idPaciente: Int? = null,

    @SerializedName("id_doctor")
    val idDoctor: Int? = null,

    @SerializedName("rol")
    val rol: String,

    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("status")
    val status: String
)
