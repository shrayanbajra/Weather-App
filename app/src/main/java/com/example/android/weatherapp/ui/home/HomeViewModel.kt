package com.example.android.weatherapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.android.weatherapp.core.BaseViewModel
import com.example.android.weatherapp.data.DataWrapper
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.ui.WeatherUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {

    private val repository = HomeRepository.getInstance()
    private val _weatherEntity = repository.getCurrentWeatherLiveData()

    fun getWeatherLiveData() = transformLiveDataForUI()

    private fun transformLiveDataForUI(): LiveData<DataWrapper<WeatherUi>> {
        return Transformations.map(_weatherEntity, ::transformEntityToUI)
    }

    fun updateWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchAndUpdateWeather()
        }
    }

    private suspend fun fetchAndUpdateWeather() {
        try {
            repository.fetchAndStoreCurrentWeather()
        } catch (exception: Exception) {
            prepareWrapperForFailedFetch()
            logError(exception)
        }
    }

    private fun prepareWrapperForFailedFetch() {
        val wrapperForFailedWeatherUpdate = DataWrapper<WeatherEntity>()
        wrapperForFailedWeatherUpdate.prepareFailure("Couldn't Retrieve Data From Server")
        _weatherEntity.postValue(wrapperForFailedWeatherUpdate)
    }

    private fun logError(exception: Exception) {
        Log.d("HomeViewModel", exception.localizedMessage ?: "")
    }

    private fun transformEntityToUI(entityWrapper: DataWrapper<WeatherEntity>): DataWrapper<WeatherUi> {
        val weatherUi = WeatherUi().apply {
            entityWrapper.wrapperBody.let { entity ->
                location = entity?.location ?: ""
                weatherCondition = entity?.weatherCondition ?: ""
                weatherDescription = entity?.weatherDescription ?: ""
                temperature = entity?.temperature ?: ""
                minTemperature = entity?.minTemperature ?: ""
                maxTemperature = entity?.maxTemperature ?: ""
                icon = entity?.imageUri ?: ""
            }
        }
        val weatherUiWrapper = prepareWrapperForUI(entityWrapper)
        weatherUiWrapper.wrapperBody = weatherUi
        return weatherUiWrapper
    }

    private fun prepareWrapperForUI(entityWrapper: DataWrapper<WeatherEntity>): DataWrapper<WeatherUi> {
        return DataWrapper<WeatherUi>().apply {
            status = entityWrapper.status
            message = entityWrapper.message
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteWeathersFromDatabase()
        }
    }
}