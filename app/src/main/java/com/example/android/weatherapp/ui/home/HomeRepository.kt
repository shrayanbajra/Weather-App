package com.example.android.weatherapp.ui.home

import com.example.android.weatherapp.app.AppPreferences
import com.example.android.weatherapp.data.local.WeatherDao
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.remote.NetworkMapper
import com.example.android.weatherapp.data.remote.currentweather.WeatherResponse
import com.example.android.weatherapp.network.OpenWeatherApi
import com.example.android.weatherapp.utils.Resource
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class HomeRepository
constructor(var openWeatherApi: OpenWeatherApi, var weatherDao: WeatherDao) {

    suspend fun getCachedWeather(): Resource<WeatherEntity> {

        val cacheEntity = weatherDao.getCurrentWeatherFor(AppPreferences.LOCATION)
        cacheEntity?.let {
            return Resource.success(it)
        } ?: run {
            return Resource.error(
                msg = "No weather information found for ${AppPreferences.LOCATION}",
                data = null
            )
        }

    }

    suspend fun getCurrentWeather() = flow {

        emit(Resource.loading(null))

        try {
            val weatherResponse = openWeatherApi.getWeatherResponse(
                AppPreferences.LOCATION, AppPreferences.UNITS, AppPreferences.API_KEY
            )

            if (weatherResponse.isSuccessful) {

                val body = weatherResponse.body()
                body?.let {

                    updatedDb(body)
                    val updatedWeather = getUpdatedWeather()
                    emit(Resource.success(updatedWeather))

                } ?: run {
                    emit(Resource.error("Couldn't get weather information", null))
                }

            } else emit(Resource.error("Bad Response", null))

        } catch (ex: HttpException) {
            emit(Resource.error(ex.message(), null))
        }

    }

    private suspend fun updatedDb(body: WeatherResponse) {
        val cacheEntity = NetworkMapper.transformResponseToEntity(body)
        weatherDao.insertCurrentWeather(cacheEntity)
    }

    private suspend fun getUpdatedWeather(): WeatherEntity? {
        return weatherDao.getCurrentWeatherFor(AppPreferences.LOCATION)
    }

}