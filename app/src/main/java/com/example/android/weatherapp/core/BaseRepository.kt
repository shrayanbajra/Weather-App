package com.example.android.weatherapp.core

import com.example.android.weatherapp.app.AppDatabase
import com.example.android.weatherapp.data.local.WeatherDao
import com.example.android.weatherapp.network.Api
import com.example.android.weatherapp.network.RetrofitClient

abstract class BaseRepository {

    fun getWeatherDaoInstance(): WeatherDao {
        return AppDatabase.getDatabaseInstance().weatherDao()
    }

    fun getNetworkClient(): Api {
        return RetrofitClient.getApiInstance()
    }
}