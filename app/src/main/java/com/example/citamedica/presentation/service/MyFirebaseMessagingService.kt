package com.example.citamedica.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.citamedica.R
import com.example.citamedica.data.repository.CitaMedicaRepository
import com.example.citamedica.data.session.SessionManager
import com.example.citamedica.data.models.MensajeResponse
import com.example.citamedica.presentation.activity.MainActivity
import com.example.citamedica.presentation.activity.WelcomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "citamedica_default_channel"
        private const val CHANNEL_NAME = "Notificaciones CitaMedica"
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Nuevo token FCM: $token")
        val idPaciente = SessionManager(applicationContext).obtenerIdPaciente()
        if (idPaciente != -1) {
            enviarTokenAlServidor(idPaciente, token)
        }
    }

    private fun enviarTokenAlServidor(idPaciente: Int, token: String) {
        CitaMedicaRepository().registrarTokenFcm(idPaciente, token)
            .enqueue(object : Callback<MensajeResponse> {
                override fun onResponse(
                    call: Call<MensajeResponse>,
                    response: Response<MensajeResponse>
                ) {
                    Log.d(TAG, "Token registrado en servidor: ${response.code()}")
                }

                override fun onFailure(call: Call<MensajeResponse>, t: Throwable) {
                    Log.e(TAG, "Error al registrar token FCM", t)
                }
            })
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "Mensaje recibido de: ${message.from}")

        val title = message.notification?.title ?: message.data["title"] ?: "CitaMedica"
        val body = message.notification?.body ?: message.data["body"] ?: ""

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val haySesion = SessionManager(applicationContext).obtenerIdPaciente() != -1
        val destino = if (haySesion) WelcomeActivity::class.java else MainActivity::class.java
        val intent = Intent(this, destino).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
