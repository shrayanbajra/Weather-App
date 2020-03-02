package com.example.android.weatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.weatherapp.core.BaseViewModel
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.ui.WeatherUi
import com.example.android.weatherapp.ui.DataWrapper
import com.example.android.weatherapp.utils.NetworkUtils

class HomeViewModel : BaseViewModel() {

    private val repository = HomeRepository.getInstance()
    private val _weatherEntity = repository.getCurrentWeatherLiveData()
    private val _weatherUpdateStatus = MutableLiveData<DataWrapper>()

    fun getWeatherLiveData() = transformLiveDataForUI()

    private fun transformLiveDataForUI(): LiveData<WeatherUi> {
        return Transformations.map<WeatherEntity, WeatherUi>(_weatherEntity, ::transformEntityToUI)
    }

    fun updateWeather() {
        if (NetworkUtils.hasNoInternetConnection()) {
            prepareStatusForNoInternet()
        } else {
            fetchAndUpdateWeather()
        }
    }

    private fun prepareStatusForNoInternet() {
        val statusWrapper = DataWrapper()
        statusWrapper.prepareFailure("No Internet Connection")
        _weatherUpdateStatus.value = statusWrapper
    }

    private fun fetchAndUpdateWeather() {
        try {
            repository.fetchAndStoreCurrentWeather()
            prepareStatusForSuccessfulResponse()
        } catch (exception: Exception) {
            prepareStatusForFailureResponse()
        }
    }

    private fun prepareStatusForSuccessfulResponse() {
        val statusWrapper = DataWrapper()
        statusWrapper.prepareSuccess("Weather Updated")
        _weatherUpdateStatus.value = statusWrapper
    }

    private fun prepareStatusForFailureResponse() {
        val statusWrapper = DataWrapper()
        statusWrapper.prepareFailure("Could not retrieve data from server")
        _weatherUpdateStatus.value = statusWrapper
    }

    fun getWeatherUpdateStatus() = _weatherUpdateStatus as LiveData<DataWrapper>

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