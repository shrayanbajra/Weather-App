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
        CoroutineScope(Main).launch {
            _weatherEntity.value = getWeatherFromDatabase()
        }
    }

    fun getWeatherLiveData(): LiveData<WeatherEntity> {
        return _weatherEntity
    }

    suspend fun fetchAndStoreWeather(): Boolean {
        return withContext(Main) {
            fetchWeather()

            val weatherResponse = _weatherResponse.value ?: WeatherResponse()
//        if (responseWrapper.isNotSuccessful()) {
//            wasUpdateSuccessful = false
//        }
//
//        val weatherResponse = responseWrapper.getResponse()
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
            weatherDao.getWeatherfor(AppUtils.LOCATION) ?: WeatherEntity()
        }
    }

    private suspend fun deleteWeatherFromDatabase() {
        withContext(Dispatchers.IO) {
            weatherDao.deleteWeatherFromDatabase()
        }
    }

    /**
     * Network Request
     */
    private suspend fun fetchWeather() {
        withContext(Dispatchers.IO) {
            val weatherResponse = getNetworkClient().getWeatherResponse(
                AppUtils.LOCATION,
                AppUtils.UNITS,
                AppUtils.API_KEY
            )
            _weatherResponse.postValue(weatherResponse)
        }
    }

    /**
     * Transforming Response to Entity
     */
    private fun transformResponseToEntity(weatherResponse: WeatherResponse?): WeatherEntity {

        val decimalFormat = DecimalFormat("##")
        decimalFormat.roundingMode = RoundingMode.CEILING

        return WeatherEntity().apply {
            weatherResponse?.let {
                location = it.name
                weatherCondition = it.weather[0].main
                weatherDescription = it.weather[0].description
                temperature = decimalFormat.format(it.main.temp).toString()
                minTemperature = decimalFormat.format(it.main.tempMin).toString()
                maxTemperature = decimalFormat.format(it.main.tempMax).toString()
                imageUri = getIconFromURI(it.weather[0].icon)
            }
        }
    }

    /**
     * Retrieving icon for Weather Condition
     */
    private fun getIconFromURI(icon: String): String {
        return String.format("https://api.openweathermap.org/img/w/%s.png", icon)
    }
}