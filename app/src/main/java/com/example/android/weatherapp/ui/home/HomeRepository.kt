package com.example.android.weatherapp.ui.home

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import com.example.android.weatherapp.R
import com.example.android.weatherapp.app.AppPreferences
import com.example.android.weatherapp.core.BaseRepository
import com.example.android.weatherapp.data.DataWrapper
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.remote.response.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
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
    private val _weatherEntity = MutableLiveData<DataWrapper<WeatherEntity>>()

    init {
        CoroutineScope(IO).launch {
            updateWeatherEntityLiveData()
        }
    }

    fun getCurrentWeatherLiveData() = _weatherEntity

    private suspend fun updateWeatherEntityLiveData(message: String = "") {
        val weatherEntityFromDatabase = getCurrentWeatherFromDatabase()
        val entityWrapper = if (weatherEntityFromDatabase == null) {
            prepareEntityWrapperForFailure()
        } else {
            prepareEntityWrapperForSuccess(message, weatherEntityFromDatabase)
        }
        _weatherEntity.postValue(entityWrapper)
    }

    private fun prepareEntityWrapperForFailure(): DataWrapper<WeatherEntity> {
        val failureEntityWrapper = DataWrapper<WeatherEntity>()
        failureEntityWrapper.prepareFailure(
            "No Weather Information found for ${AppPreferences.LOCATION}"
        )
        return failureEntityWrapper
    }

    private fun prepareEntityWrapperForSuccess(
        message: String, weatherEntityFromDatabase: WeatherEntity
    ): DataWrapper<WeatherEntity> {
        val successEntityWrapper = DataWrapper<WeatherEntity>()
        successEntityWrapper.prepareSuccess(message, weatherEntityFromDatabase)
        return successEntityWrapper
    }

    @Throws(Exception::class)
    suspend fun fetchAndStoreCurrentWeather() {
        val weatherResponse = fetchCurrentWeatherFromNetwork()
        updateDatabase(weatherResponse)
    }

    private suspend fun updateDatabase(weatherResponse: WeatherResponse) {
        deleteWeathersFromDatabase()
        val weatherEntity = transformResponseToEntity(weatherResponse)
        insertCurrentWeatherIntoDatabase(weatherEntity)
        updateWeatherEntityLiveData("Successfully Updated Weather Information")
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
        val location = getLocation()
        return getNetworkClient().getWeatherResponse(
            location,
            AppPreferences.UNITS,
            AppPreferences.API_KEY
        )
    }

    private fun getLocation(): String {
        return if (AppPreferences.LOCATION.isBlank()) {
            getDefaultLocationValue()
        } else {
            AppPreferences.LOCATION
        }
    }

    private fun getDefaultLocationValue() =
        Resources.getSystem().getString(R.string.pref_location_default_value)

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