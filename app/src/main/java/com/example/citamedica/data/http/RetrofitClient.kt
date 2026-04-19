package com.example.citamedica.data.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://clinicaapi-production.up.railway.app/"
    private const val BASE_URL_NOTIFICACIONES = "https://citas-medicas-front-mobile.vercel.app/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val retrofitNotificaciones: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_NOTIFICACIONES)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
