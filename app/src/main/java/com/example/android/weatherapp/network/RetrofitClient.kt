package com.example.android.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Client responsible for sending request to server
abstract class RetrofitClient {

    companion object {
        private const val BASE_ADDRESS = "https://api.openweathermap.org/data/2.5/"
        private var instance: API? = null

        fun getApiInstance(): API {
            if (instance == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_ADDRESS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                instance = retrofit.create(API::class.java)
            }
            return instance!!
        }
    }
}