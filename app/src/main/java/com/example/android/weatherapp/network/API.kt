package com.example.android.weatherapp.network

import com.example.android.weatherapp.data.remote.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

// API Endpoints
interface API {

    @Suppress("SpellCheckingInspection")
    @GET("weather")
    suspend fun getWeatherResponse(
        @Query("q") location: String,
        @Query("units") units: String,
        @Query("APPID") apiKey: String
    ): WeatherResponse
}