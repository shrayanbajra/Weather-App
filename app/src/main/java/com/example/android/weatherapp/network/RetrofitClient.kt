package com.example.android.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Client responsible for defining request to server
 */
abstract class RetrofitClient {

    /**
     * Singleton pattern
     */
    companion object {

        private const val BASE_ADDRESS = "https://api.openweathermap.org/data/2.5/"
        private var instance: Api? = null

        fun getApiInstance(): Api {

            if (instance == null) {
                val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl(BASE_ADDRESS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                instance = retrofit.create(Api::class.java)
            }
            return instance!!
        }
    }
}