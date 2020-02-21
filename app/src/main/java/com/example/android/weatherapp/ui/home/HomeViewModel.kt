package com.example.android.weatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.android.weatherapp.core.BaseViewModel
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.ui.WeatherUi
import com.example.android.weatherapp.ui.DataWrapper
import com.example.android.weatherapp.utils.AppUtils
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {

    private val repository: HomeRepository = HomeRepository.getInstance()
    private val _weatherEntity: LiveData<WeatherEntity> = repository.getWeatherLiveData()
    private val _weatherUpdateStatus = MutableLiveData<DataWrapper>()

    fun getWeatherLiveData() = transformLiveDataForUI()

    private fun transformLiveDataForUI(): LiveData<WeatherUi> {
        return Transformations.map<WeatherEntity, WeatherUi>(_weatherEntity, ::transformEntityToUI)
    }

    fun updateWeather() {
        if (AppUtils.isNotConnectedToInternet()) {
            prepareStatusForNoInternet()
        } else {
            fetchAndUpdateWeather()
        }
    }

    private fun prepareStatusForNoInternet() {
        val statusWrapper = DataWrapper()
        statusWrapper.prepareFailure("No Internet Connection")
        _weatherUpdateStatus.postValue(statusWrapper)
    }

    private fun fetchAndUpdateWeather() {
        viewModelScope.launch {
            try {
                repository.fetchAndStoreWeather()
                prepareStatusForSuccessfulResponse()
            } catch (exception: Exception) {
                prepareStatusForFailureResponse()
            }
        }
    }

    private fun prepareStatusForSuccessfulResponse() {
        val statusWrapper = DataWrapper()
        statusWrapper.prepareSuccess("Weather Updated")
        _weatherUpdateStatus.postValue(statusWrapper)
    }

    private fun prepareStatusForFailureResponse() {
        val statusWrapper = DataWrapper()
        statusWrapper.prepareFailure("Could not retrieve data from server")
        _weatherUpdateStatus.postValue(statusWrapper)
    }

    fun getWeatherUpdateStatus(): LiveData<DataWrapper> {
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