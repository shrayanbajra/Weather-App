package com.example.android.weatherapp.ui.home

import androidx.lifecycle.*
import com.example.android.weatherapp.data.DataWrapper
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.ui.WeatherUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class HomeViewModel : ViewModel() {

    fun getCurrentWeather(): LiveData<DataWrapper<WeatherUI>> {

        var currentWeather = MutableLiveData<DataWrapper<WeatherEntity>>()

        viewModelScope.launch(Main) {

            currentWeather = withContext(Dispatchers.Default) {

                fetchAndUpdateWeather()

            }

        }

        return transformLiveDataForUI(currentWeather)
    }

    private suspend fun fetchAndUpdateWeather(): MutableLiveData<DataWrapper<WeatherEntity>> {

        var currentWeatherLiveData = MutableLiveData<DataWrapper<WeatherEntity>>()

        try {

            val repository = HomeRepository.getInstance()

            repository.fetchAndStoreCurrentWeather()
            currentWeatherLiveData = repository.getWeatherEntityLiveData()

        } catch (exception: Exception) {

            val wrapperForFailedWeatherUpdate = prepareWrapperForFailedFetch()
            currentWeatherLiveData.postValue(wrapperForFailedWeatherUpdate)

            logError(exception)

        } finally {

            return currentWeatherLiveData

        }
    }

    private fun prepareWrapperForFailedFetch(): DataWrapper<WeatherEntity> {

        val wrapperForFailedWeatherUpdate = DataWrapper<WeatherEntity>()
        wrapperForFailedWeatherUpdate.prepareFailure("Couldn't Retrieve Data From Server")

        return wrapperForFailedWeatherUpdate

    }

    private fun logError(exception: Exception) {

        Timber.d(exception.localizedMessage ?: "")

    }

    private fun transformLiveDataForUI(weatherEntity: LiveData<DataWrapper<WeatherEntity>>): LiveData<DataWrapper<WeatherUI>> {

        return Transformations.map(weatherEntity, ::transformEntityToUI)

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