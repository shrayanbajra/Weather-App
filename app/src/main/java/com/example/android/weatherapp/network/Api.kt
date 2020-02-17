package com.example.android.weatherapp.network

import com.example.android.weatherapp.data.remote.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Methods in API
 */
interface Api {

    @GET("weather")
    suspend fun getWeatherResponse(
        @Query("q") location: String,
        @Query("units") units: String,
        @Query("APPID") apiKey: String
    ): WeatherResponse

}