package com.example.citamedica.data.models

data class RegisterRequest(
    val correo: String,
    val password: String,
    val rol: String,
    val nombre: String,
    val especialidad: String = ""
)
