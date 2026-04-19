package com.example.citamedica.data.models

import com.google.gson.annotations.SerializedName

data class AgendarCitaResponse(
    val mensaje: String?,
    val estado: String?,

    @SerializedName("id_cita")
    val idCita: Int? = null
)
