package com.example.android.weatherapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.android.weatherapp.core.BaseViewModel
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.ui.WeatherUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {

    private val repository = HomeRepository.getInstance()
    private val _weatherEntity = repository.getCurrentWeatherLiveData()

    fun getWeatherLiveData() = transformLiveDataForUI()

    private fun transformLiveDataForUI(): LiveData<WeatherUi> {
        return Transformations.map<WeatherEntity, WeatherUi>(_weatherEntity, ::transformEntityToUI)
    }

    fun updateWeather() {
        viewModelScope.launch(Dispatchers.Main) {
            fetchAndUpdateWeather()
        }
    }

    private suspend fun fetchAndUpdateWeather() {
        try {
            repository.fetchAndStoreCurrentWeather()
        } catch (exception: Exception) {
            // TODO: Wrap the data from repository and show error according to the response status
            Log.d("HomeViewModel", "Exception Caught")
        }
    }

    private fun transformEntityToUI(weatherEntity: WeatherEntity): WeatherUi {
        val degreeSymbol = "\u00B0"
        return WeatherUi().apply {
            weatherEntity.let { entity ->
                location = entity.location
                weatherCondition = entity.weatherCondition
                weatherDescription = entity.weatherDescription
                temperature = entity.temperature + degreeSymbol
                minTemperature = entity.minTemperature + degreeSymbol
                maxTemperature = entity.maxTemperature + degreeSymbol
                icon = entity.imageUri
            }
        }
    }
}