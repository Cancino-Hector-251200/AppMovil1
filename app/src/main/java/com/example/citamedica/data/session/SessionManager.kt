package com.example.citamedica.data.session

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun guardarIdPaciente(idPaciente: Int) {
        prefs.edit().putInt(KEY_ID_PACIENTE, idPaciente).apply()
    }

    fun obtenerIdPaciente(): Int = prefs.getInt(KEY_ID_PACIENTE, -1)

    fun guardarIdUsuario(idUsuario: Int) {
        prefs.edit().putInt(KEY_ID_USUARIO, idUsuario).apply()
    }

    fun obtenerIdUsuario(): Int = prefs.getInt(KEY_ID_USUARIO, -1)

    fun guardarCorreo(correo: String) {
        prefs.edit().putString(KEY_CORREO, correo).apply()
    }

    fun obtenerCorreo(): String = prefs.getString(KEY_CORREO, "") ?: ""

    fun limpiar() {
        val correo = obtenerCorreo()
        prefs.edit().clear().apply()
        if (correo.isNotBlank()) {
            prefs.edit().putString(KEY_CORREO, correo).apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "citamedica_session"
        private const val KEY_ID_PACIENTE = "id_paciente"
        private const val KEY_ID_USUARIO = "id_usuario"
        private const val KEY_CORREO = "correo"
    }
}
