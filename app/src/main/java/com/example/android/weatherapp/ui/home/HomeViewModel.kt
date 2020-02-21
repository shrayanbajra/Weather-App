package com.example.android.weatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.ui.WeatherUi
import com.example.android.weatherapp.ui.BaseViewModel
import com.example.android.weatherapp.utils.AppUtils
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {

    private val repository: HomeRepository = HomeRepository.getInstance()
    private val _weatherEntity: LiveData<WeatherEntity> = repository.getWeatherLiveData()
    private val _weatherUpdateStatus = MutableLiveData<Boolean>()

    fun getWeatherLiveData() = transformLiveDataForUI()

    private fun transformLiveDataForUI(): LiveData<WeatherUi> {
        return Transformations.map<WeatherEntity, WeatherUi>(_weatherEntity, ::transformEntityToUI)
    }

    fun updateWeather() {
        if (AppUtils.isNotConnectedToInternet()) {
            _weatherUpdateStatus.postValue(false)
        } else {
            fetchAndUpdateWeather()
        }
    }

    private fun fetchAndUpdateWeather() {
        viewModelScope.launch {
            try {
                val updateStatus: Boolean = repository.fetchAndStoreWeather()
                _weatherUpdateStatus.postValue(updateStatus)
            } catch (exception: Exception) {
                _weatherUpdateStatus.postValue(false)
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