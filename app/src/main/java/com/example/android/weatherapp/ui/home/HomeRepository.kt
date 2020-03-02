package com.example.android.weatherapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.weatherapp.app.AppPreferences
import com.example.android.weatherapp.core.BaseRepository
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.remote.response.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
            val weatherEntity = async(IO) { getCurrentWeatherFromDatabase() }
            _weatherEntity.value = weatherEntity.await()
        }
    }

    fun getCurrentWeatherLiveData() = _weatherEntity as LiveData<WeatherEntity>

    fun fetchAndStoreCurrentWeather() {
        CoroutineScope(Default).launch {
            val weatherResponse = async(IO) {
                fetchCurrentWeatherFromNetwork()
            }
            Log.d("HomeRepo", "response ld value -> $weatherResponse")
            updateDatabase(weatherResponse.await())
        }
    }

    private suspend fun updateDatabase(weatherResponse: WeatherResponse) {
        CoroutineScope(Main).launch {
            async(IO) { deleteWeathersFromDatabase() }.await()
            async(IO) {
                val weatherEntity = transformResponseToEntity(weatherResponse)
                insertCurrentWeatherIntoDatabase(weatherEntity)
            }.await()
            val weatherEntityFromDatabase = async(IO) {
                getCurrentWeatherFromDatabase()
            }.await()
            _weatherEntity.value = weatherEntityFromDatabase
        }
        Log.d("HomeRepo", "entity ld value -> ${getCurrentWeatherFromDatabase()}")
    }

    // Database Operations
    private suspend fun insertCurrentWeatherIntoDatabase(weatherEntity: WeatherEntity) {
        weatherDao.insertCurrentWeatherIntoDatabase(weatherEntity)
    }

    private suspend fun getCurrentWeatherFromDatabase(): WeatherEntity {
        return weatherDao.getCurrentWeatherFor(AppPreferences.LOCATION) ?: WeatherEntity()
    }

    private suspend fun deleteWeathersFromDatabase() {
        weatherDao.deleteWeathersFromDatabase()
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