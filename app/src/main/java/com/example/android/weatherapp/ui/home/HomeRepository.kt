package com.example.android.weatherapp.ui.home

import androidx.lifecycle.MutableLiveData
import com.example.android.weatherapp.app.AppPreferences
import com.example.android.weatherapp.baseclass.BaseRepository
import com.example.android.weatherapp.data.DataWrapper
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.remote.currentweather.WeatherResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.math.RoundingMode
import java.text.DecimalFormat

class HomeRepository private constructor() : BaseRepository() {

    companion object {

        private var instance: HomeRepository? = null

        fun getInstance() = instance ?: HomeRepository()

    }

    // Dao and LiveData
    private val weatherDao = getWeatherDaoInstance()

    suspend fun getWeatherEntityLiveData(message: String = ""): MutableLiveData<DataWrapper<WeatherEntity>> {

        val weatherEntityFromDatabase = getCurrentWeatherFromDatabase()
        Timber.d("### Got result from database ###")
        Timber.d("Weather Entity From DB -> $weatherEntityFromDatabase")

        val entityWrapper = if (weatherEntityFromDatabase == null) {

            prepareEntityWrapperForFailure()

        } else {

            prepareEntityWrapperForSuccess(message, weatherEntityFromDatabase)

        }

        val currentWeatherLiveData = MutableLiveData<DataWrapper<WeatherEntity>>()
        currentWeatherLiveData.postValue(entityWrapper)

        return currentWeatherLiveData
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

    @Throws(Exception::class)
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

            getNetworkClient().getWeatherResponse(
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