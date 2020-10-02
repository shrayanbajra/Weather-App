package com.example.android.weatherapp.ui.home

import com.example.android.weatherapp.app.AppPreferences
import com.example.android.weatherapp.data.local.WeatherDao
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.remote.NetworkMapper
import com.example.android.weatherapp.data.remote.currentweather.WeatherResponse
import com.example.android.weatherapp.network.OpenWeatherApi
import com.example.android.weatherapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class HomeRepository
constructor(var openWeatherApi: OpenWeatherApi, var weatherDao: WeatherDao) {

    suspend fun getCachedWeather(): Resource<WeatherEntity> {

        val cacheEntity = weatherDao.getCurrentWeatherFor(AppPreferences.LOCATION)
        return if (cacheEntity != null) Resource.success(cacheEntity)
        else Resource.error(
            msg = "No weather information found for ${AppPreferences.LOCATION}",
            data = null
        )

    }

    suspend fun getCurrentWeather(): Flow<Resource<WeatherEntity?>> = flow {

        emit(Resource.loading(null))

        try {
            val weatherResponse = openWeatherApi.getWeatherResponse(
                AppPreferences.LOCATION, AppPreferences.UNITS, AppPreferences.API_KEY
            )

            if (!weatherResponse.isSuccessful) {
                emit(Resource.error(weatherResponse.message(), null))
                return@flow
            }

            val body = weatherResponse.body()

            if (body == null) {
                emit(Resource.error("Couldn't get weather information", null))
                return@flow
            }

            updateDb(body)

            val updatedWeather = getUpdatedWeather()
            if (updatedWeather == null) {
                val errorResource = Resource.error(
                    msg = "Not Found",
                    data = null
                )
                emit(errorResource)
                return@flow
            }

            emit(Resource.success(updatedWeather))

        } catch (ex: HttpException) {
            emit(Resource.error(ex.message(), null))
            return@flow
        }

    }

    private suspend fun updateDb(body: WeatherResponse) {
        val cacheEntity = NetworkMapper.transformResponseToEntity(body)
        weatherDao.insertCurrentWeather(cacheEntity)
    }

    private suspend fun getUpdatedWeather(): WeatherEntity? {
        return weatherDao.getCurrentWeatherFor(AppPreferences.LOCATION)
    }

}