package com.example.android.weatherapp.network

import com.example.android.weatherapp.data.remote.currentweather.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    @Suppress("SpellCheckingInspection")
    @GET("weather")
    suspend fun getWeatherResponse(
        @Query("q") location: String,
        @Query("units") units: String,
        @Query("APPID") apiKey: String
    ): Response<WeatherResponse>

}