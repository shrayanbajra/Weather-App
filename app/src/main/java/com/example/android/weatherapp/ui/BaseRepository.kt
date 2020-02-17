package com.example.android.weatherapp.ui

import com.example.android.weatherapp.data.local.WeatherDao
import com.example.android.weatherapp.data.local.WeatherDatabase
import com.example.android.weatherapp.network.Api
import com.example.android.weatherapp.network.RetrofitClient

abstract class BaseRepository {

    fun getWeatherDaoInstance(): WeatherDao {
        return WeatherDatabase.getDatabaseInstance().weatherDao()
    }

    fun getNetworkClient(): Api {
        return RetrofitClient.getApiInstance()
    }
}