package com.example.android.weatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.remote.response.WeatherResponse
import com.example.android.weatherapp.ui.BaseRepository
import com.example.android.weatherapp.utils.AppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
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
    private val _weatherResponse = MutableLiveData<WeatherResponse>()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            _weatherEntity.postValue(getWeatherFromDatabase())
        }
    }

    fun getWeatherLiveData(): LiveData<WeatherEntity> {
        return _weatherEntity
    }

    suspend fun fetchAndStoreWeather(): Boolean {
        return withContext(Main) {
            fetchWeather()
            val weatherResponse = _weatherResponse.value ?: WeatherResponse()
            updateDatabase(weatherResponse)
        }
    }

    private suspend fun updateDatabase(weatherResponse: WeatherResponse): Boolean {
        val weatherEntity = transformResponseToEntity(weatherResponse)

        deleteWeatherFromDatabase()
        insertWeatherIntoDatabase(weatherEntity)

        _weatherEntity.postValue(getWeatherFromDatabase())
        return true
    }

    // Database Operations
    private suspend fun insertWeatherIntoDatabase(weatherEntity: WeatherEntity) {
        withContext(Dispatchers.IO) {
            weatherDao.insertWeatherIntoDatabase(weatherEntity)
        }
    }

    private suspend fun getWeatherFromDatabase(): WeatherEntity {
        return withContext(Dispatchers.IO) {
            weatherDao.getWeatherFor(AppUtils.LOCATION) ?: WeatherEntity()
        }
    }

    private suspend fun deleteWeatherFromDatabase() {
        withContext(Dispatchers.IO) {
            weatherDao.deleteWeatherFromDatabase()
        }
    }

    // Network Request
    private suspend fun fetchWeather() {
        withContext(Dispatchers.IO) {
            val weatherResponse: WeatherResponse = getNetworkClient().getWeatherResponse(
                AppUtils.LOCATION,
                AppUtils.UNITS,
                AppUtils.API_KEY
            )
            _weatherResponse.postValue(weatherResponse)
        }
    }

    // Transforming Response to Entity
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