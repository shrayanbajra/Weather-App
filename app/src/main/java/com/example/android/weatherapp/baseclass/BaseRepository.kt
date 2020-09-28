package com.example.android.weatherapp.baseclass

import com.example.android.weatherapp.app.AppDatabase
import com.example.android.weatherapp.network.RetrofitClient

abstract class BaseRepository {

    fun getWeatherDaoInstance() = AppDatabase.getDatabaseInstance().weatherDao()

    fun getNetworkClient() = RetrofitClient.getApiInstance()

}