package com.example.android.weatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.ui.WeatherUi
import com.example.android.weatherapp.ui.BaseViewModel
import com.example.android.weatherapp.utils.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : BaseViewModel() {

    private val repository = HomeRepository.getInstance()
    private val _weatherEntity = repository.getWeatherLiveData()
    private val _weatherUI =
        Transformations.map<WeatherEntity, WeatherUi>(_weatherEntity, ::transformEntityToUI)

    private val _weatherUpdateStatus = MutableLiveData<Boolean>()

    fun getWeatherLiveData() = _weatherUI

    fun updateWeather() {
        if (AppUtils.isNotConnectedToInternet()) {
            _weatherUpdateStatus.postValue(false)
        } else {
            fetchAndUpdateWeather()
        }
    }

    private fun fetchAndUpdateWeather() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val updateStatus = repository.fetchAndStoreWeather()
                _weatherUpdateStatus.postValue(updateStatus)
            }
        }
    }

    fun getWeatherUpdateStatus(): LiveData<Boolean> {
        return _weatherUpdateStatus
    }

    private fun transformEntityToUI(weatherEntity: WeatherEntity): WeatherUi {
        val degreeSymbol = "\u00B0"
        return WeatherUi().apply {
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
    }
}