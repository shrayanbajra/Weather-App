package com.example.android.weatherapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.weatherapp.app.AppPreferences
import com.example.android.weatherapp.core.BaseRepository
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.remote.response.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import java.text.DecimalFormat

class HomeRepository private constructor() : BaseRepository() {

    companion object {
        private var instance: HomeRepository? = null
        fun getInstance() = instance ?: HomeRepository()
    }

    // Dao and LiveData
    private val weatherDao = getWeatherDaoInstance()
    private val _weatherEntity = MutableLiveData<WeatherEntity>()

    init {
        CoroutineScope(Main).launch {
            val weatherEntity = async { getCurrentWeatherFromDatabase() }
            _weatherEntity.value = weatherEntity.await()
        }
    }

    fun getCurrentWeatherLiveData() = _weatherEntity as LiveData<WeatherEntity>

    suspend fun fetchAndStoreCurrentWeather() {
        val weatherResponse = fetchCurrentWeatherFromNetwork()
        Log.d("HomeRepo", "response ld value -> $weatherResponse")
        updateDatabase(weatherResponse)
    }

    private suspend fun updateDatabase(weatherResponse: WeatherResponse) {
        deleteWeathersFromDatabase()
        val weatherEntity = transformResponseToEntity(weatherResponse)
        insertCurrentWeatherIntoDatabase(weatherEntity)
        val weatherEntityFromDatabase = getCurrentWeatherFromDatabase()
        _weatherEntity.value = weatherEntityFromDatabase
        Log.d("HomeRepo", "entity ld value -> ${getCurrentWeatherFromDatabase()}")
    }

    // Database Operations
    private suspend fun insertCurrentWeatherIntoDatabase(weatherEntity: WeatherEntity) {
        withContext(IO) {
            weatherDao.insertCurrentWeatherIntoDatabase(weatherEntity)
        }
    }

    private suspend fun getCurrentWeatherFromDatabase(): WeatherEntity {
        return withContext(IO) {
            weatherDao.getCurrentWeatherFor(AppPreferences.LOCATION) ?: WeatherEntity()
        }
    }

    private suspend fun deleteWeathersFromDatabase() {
        withContext(IO) {
            weatherDao.deleteWeathersFromDatabase()
        }
    }

    // Network Request
    private suspend fun fetchCurrentWeatherFromNetwork(): WeatherResponse {
        return getNetworkClient().getWeatherResponse(
            AppPreferences.LOCATION, AppPreferences.UNITS, AppPreferences.API_KEY
        )
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