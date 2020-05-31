package com.example.android.weatherapp.baseclass

import com.example.android.weatherapp.app.AppDatabase
import com.example.android.weatherapp.data.local.WeatherDao
import com.example.android.weatherapp.network.API
import com.example.android.weatherapp.network.RetrofitClient

abstract class BaseRepository {

    fun getWeatherDaoInstance(): WeatherDao {

        return AppDatabase.getDatabaseInstance().weatherDao()

    }

    fun getNetworkClient(): API {

        return RetrofitClient.getApiInstance()

    }
}