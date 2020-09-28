package com.example.android.weatherapp.ui.home

import com.example.android.weatherapp.app.AppPreferences
import com.example.android.weatherapp.data.DataWrapper
import com.example.android.weatherapp.data.local.WeatherDao
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.remote.currentweather.WeatherResponse
import com.example.android.weatherapp.network.OpenWeatherApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.math.RoundingMode
import java.text.DecimalFormat

class HomeRepository
constructor(var openWeatherApi: OpenWeatherApi, var weatherDao: WeatherDao) {

    suspend fun getWeatherFromDB(message: String = ""): DataWrapper<WeatherEntity> {

        val weatherCacheEntity = getCurrentWeatherFromDatabase()
        Timber.d("### Got result from database ###")
        Timber.d("Weather Entity From DB -> $weatherCacheEntity")

        return if (weatherCacheEntity == null) prepareEntityWrapperForFailure()
        else prepareEntityWrapperForSuccess(message, weatherCacheEntity)

    }

    private fun prepareEntityWrapperForFailure(): DataWrapper<WeatherEntity> {

        val failureEntityWrapper = DataWrapper<WeatherEntity>()

        failureEntityWrapper.prepareFailure(
            "No Weather Information found for ${AppPreferences.LOCATION}"
        )

        return failureEntityWrapper

    }

    private fun prepareEntityWrapperForSuccess(

        message: String,
        weatherEntityFromDatabase: WeatherEntity

    ): DataWrapper<WeatherEntity> {

        val successEntityWrapper = DataWrapper<WeatherEntity>()
        successEntityWrapper.prepareSuccess(message, weatherEntityFromDatabase)
        return successEntityWrapper

    }

    @Throws(HttpException::class)
    suspend fun fetchAndStoreCurrentWeather() {

        val weatherResponse = fetchCurrentWeatherFromNetwork()
        Timber.d("### Data fetched ###")
        Timber.d("Weather Response -> $weatherResponse")

        val weatherEntity = transformResponseToEntity(weatherResponse)

        updateDatabase(weatherEntity)

    }

    private suspend fun updateDatabase(weatherEntity: WeatherEntity) {

        Timber.d("### Just before updating database ###")
        Timber.d("Weather Entity -> $weatherEntity")

        deleteWeathersFromDatabase()
        insertCurrentWeatherIntoDatabase(weatherEntity)

    }

    /*
    Dao Operations
     */
    private suspend fun insertCurrentWeatherIntoDatabase(weatherEntity: WeatherEntity) {

        withContext(IO) {

            weatherDao.insertCurrentWeather(weatherEntity)

        }
    }

    private suspend fun getCurrentWeatherFromDatabase(): WeatherEntity? {

        return withContext(IO) {

            val weather = weatherDao.getCurrentWeatherFor(AppPreferences.LOCATION)
            Timber.d("Current Weather in Database $weather")

            weather

        }
    }

    private suspend fun deleteWeathersFromDatabase() {

        withContext(IO) {

            weatherDao.deleteAll()

        }

    }

    // Network Request
    private suspend fun fetchCurrentWeatherFromNetwork(): WeatherResponse {

        return withContext(IO) {

            openWeatherApi.getWeatherResponse(
                AppPreferences.LOCATION,
                AppPreferences.UNITS,
                AppPreferences.API_KEY
            )

        }
    }

    private fun transformResponseToEntity(weatherResponse: WeatherResponse?): WeatherEntity {

        val decimalFormat = DecimalFormat("##")
        decimalFormat.roundingMode = RoundingMode.CEILING

        return WeatherEntity().apply {

            weatherResponse?.let { response ->

                location = response.name
                weatherCondition = response.weather[0].main
                weatherDescription = response.weather[0].description

                temperature = decimalFormat.format(response.main.temp).toString()
                minTemperature = decimalFormat.format(response.main.tempMin).toString()
                maxTemperature = decimalFormat.format(response.main.tempMax).toString()

                imageUri = getIconFromURI(response.weather[0].icon)

            }
        }
    }

    // Retrieving icon for Weather Condition
    private fun getIconFromURI(icon: String): String {

        return String.format("https://api.openweathermap.org/img/w/%s.png", icon)

    }
}