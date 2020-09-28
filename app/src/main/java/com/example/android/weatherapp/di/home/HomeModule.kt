package com.example.android.weatherapp.di.home

import com.example.android.weatherapp.data.local.WeatherDao
import com.example.android.weatherapp.network.OpenWeatherApi
import com.example.android.weatherapp.ui.home.HomeRepository
import dagger.Module
import dagger.Provides

@Module
class HomeModule {

    @Provides
    fun provideRepository(openWeatherApi: OpenWeatherApi, weatherDao: WeatherDao): HomeRepository {
        return HomeRepository(openWeatherApi, weatherDao)
    }

}