package com.example.android.weatherapp.ui

import com.example.android.sunshine.data.local.WeatherDao
import com.example.android.sunshine.data.local.WeatherDatabase
import com.example.android.sunshine.network.Api
import com.example.android.sunshine.network.RetrofitClient

abstract class BaseRepository {

    fun getWeatherDaoInstance(): WeatherDao {
        return WeatherDatabase.getDatabaseInstance().weatherDao()
    }

    fun getNetworkClient(): Api {
        return RetrofitClient.getApiInstance()
    }
}