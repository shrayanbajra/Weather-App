package com.example.android.weatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.sunshine.data.local.WeatherEntity
import com.example.android.sunshine.data.ui.WeatherUi
import com.example.android.sunshine.ui.BaseViewModel
import com.example.android.sunshine.utils.AppUtils

class HomeViewModel : BaseViewModel() {

    private val repository by lazy {
        HomeRepository.getInstance()
    }
    private val _weatherEntity = repository.getWeatherLiveData()

    // Transforming LiveData (Entity to UI)
    private val _weatherUI =
        Transformations.map<WeatherEntity, WeatherUi>(_weatherEntity, ::transformEntityToUI)

    private val _weatherUpdateStatus = MutableLiveData<Boolean>()

    fun getWeatherLiveData() = _weatherUI

    fun updateWeather() {

        if (AppUtils.isNotConnectedToInternet()) {
            _weatherUpdateStatus.postValue(false)
        }

        viewModelScope.launch {
            async {
                _weatherUpdateStatus.postValue(repository.updateWeatherInDatabase())
            }.await()
        }
    }

    fun getWeatherUpdateStatus(): LiveData<Boolean> {
        return _weatherUpdateStatus
    }

    /**
     * Transforming Entity to Ui
     */
    private fun transformEntityToUI(weatherEntity: WeatherEntity): WeatherUi {

        val weatherUi = WeatherUi()

        val degreeSymbol = "\u00B0"

        weatherUi.apply {
            weatherEntity.let {
                location = it.location

                weatherCondition = it.weatherCondition
                weatherDescription = it.weatherDescription

                temperature = it.temperature + degreeSymbol

                minTemperature = it.minTemperature + degreeSymbol
                maxTemperature = it.maxTemperature + degreeSymbol

                icon = it.imageUri
            }
        }
        return weatherUi
    }
}