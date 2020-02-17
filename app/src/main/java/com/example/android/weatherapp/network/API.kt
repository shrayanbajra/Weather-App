package com.example.android.weatherapp.network

import com.example.android.weatherapp.data.remote.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET

interface API {

    @GET("weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22")
    fun getWeather(): Call<WeatherResponse>
}