package com.example.citamedica.data.models

import com.google.gson.annotations.SerializedName

data class Doctor(
    @SerializedName("id_doctor")
    val idDoctor: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("especialidad")
    val especialidad: String
)
