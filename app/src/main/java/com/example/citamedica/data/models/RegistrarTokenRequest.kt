package com.example.citamedica.data.models

data class RegistrarTokenRequest(
    val patientId: String,
    val fcmToken: String
)
