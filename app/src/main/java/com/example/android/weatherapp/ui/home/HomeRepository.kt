package com.example.android.weatherapp.ui.home

import com.example.android.weatherapp.app.AppPreferences
import com.example.android.weatherapp.data.local.WeatherDao
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.remote.NetworkMapper
import com.example.android.weatherapp.data.remote.currentweather.WeatherResponse
import com.example.android.weatherapp.network.OpenWeatherApi
import com.example.android.weatherapp.utils.Resource
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

class HomeRepository
constructor(var openWeatherApi: OpenWeatherApi, var weatherDao: WeatherDao) {

    suspend fun getCachedWeather(): Resource<WeatherEntity> {

        Timber.d("Getting cache info for ${AppPreferences.LOCATION}")
        val cacheEntity = withContext(IO) {
            weatherDao.getCurrentWeatherFor(AppPreferences.LOCATION)
        }
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
                location = AppPreferences.LOCATION,
                units = AppPreferences.UNITS,
                apiKey = AppPreferences.API_KEY
            )

            if (!weatherResponse.isSuccessful) {
                val errorResource = Resource.error(
                    msg = weatherResponse.message(),
                    data = null
                )
                emit(errorResource)
                return@flow
            }

            val body = weatherResponse.body()

            if (body == null) {
                val errorResource = Resource.error(
                    msg = "Couldn't get weather information",
                    data = null
                )
                emit(errorResource)
                return@flow
            }

            insertIntoDb(body)

            val updatedWeather = getUpdatedWeather()
            if (updatedWeather == null) {
                val errorResource = Resource.error(msg = "Not Found", data = null)
                emit(errorResource)
                return@flow
            }

            emit(Resource.success(updatedWeather))

        } catch (ex: HttpException) {
            emit(Resource.error(ex.message(), null))
            return@flow
        }

    }

    private suspend fun insertIntoDb(body: WeatherResponse) {
        val cacheEntity = NetworkMapper.transformResponseToEntity(body)
        weatherDao.insertCurrentWeather(cacheEntity)
    }

    private suspend fun getUpdatedWeather(): WeatherEntity? {
        return weatherDao.getCurrentWeatherFor(location = AppPreferences.LOCATION)
    }

}