package com.example.android.weatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.android.weatherapp.core.BaseViewModel
import com.example.android.weatherapp.data.DataWrapper
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.ui.WeatherUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel : BaseViewModel() {

    private val repository = HomeRepository.getInstance()
    private val _weatherEntity = repository.getCurrentWeatherLiveData()

    fun getWeatherLiveData() = transformLiveDataForUI()

    private fun transformLiveDataForUI(): LiveData<DataWrapper<WeatherUI>> {
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
        Timber.d(exception.localizedMessage ?: "")
    }

    private fun transformEntityToUI(entityWrapper: DataWrapper<WeatherEntity>): DataWrapper<WeatherUI> {
        val weatherUi = WeatherUI().apply {
            Timber.d("Entity in wrapper ${entityWrapper.wrapperBody}")
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
        Timber.d("Transformed Weather UI $weatherUi")
        return weatherUiWrapper
    }

    private fun prepareWrapperForUI(entityWrapper: DataWrapper<WeatherEntity>): DataWrapper<WeatherUI> {
        return DataWrapper<WeatherUI>().apply {
            status = entityWrapper.status
            message = entityWrapper.message
        }
    }
}