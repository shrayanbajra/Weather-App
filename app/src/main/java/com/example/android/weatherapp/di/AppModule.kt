package com.example.android.weatherapp.di

import com.example.android.weatherapp.network.OpenWeatherApi
import com.example.android.weatherapp.utils.BASE_ADDRESS
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideOpenWeatherApi(): OpenWeatherApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_ADDRESS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(OpenWeatherApi::class.java)
    }

}