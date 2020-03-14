package com.example.android.weatherapp.core

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.android.weatherapp.app.AppDatabase
import com.example.android.weatherapp.data.local.WeatherDao
import com.example.android.weatherapp.network.API
import com.example.android.weatherapp.network.RetrofitClient
import com.example.android.weatherapp.utils.AppUtils

abstract class BaseRepository {

    fun getWeatherDaoInstance(): WeatherDao {
        return AppDatabase.getDatabaseInstance().weatherDao()
    }

    fun getNetworkClient(): API {
        return RetrofitClient.getApiInstance()
    }

    fun getAppPreferences(): SharedPreferences? {
        val context = AppUtils.getApp().applicationContext
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}