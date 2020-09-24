package com.example.android.weatherapp.ui.home

import androidx.lifecycle.*
import com.example.android.weatherapp.data.DataWrapper
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.ui.WeatherUI
import com.example.android.weatherapp.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class HomeViewModel : ViewModel() {

    fun getCurrentWeather(): LiveData<DataWrapper<WeatherUI>> {

        val currentWeather = MutableLiveData<DataWrapper<WeatherEntity>>()

        viewModelScope.launch(Main) {

            if (NetworkUtils.hasNoInternetConnection()) {

                val repository = HomeRepository.getInstance()

                Timber.d("### Just Before Receiving Weather from Database ###")

                val weatherFromDB = repository.getWeatherFromDB()
                currentWeather.value = weatherFromDB

                Timber.d("### After Receiving Weather from Database (Offline) ###")
                Timber.d("Value -> ${currentWeather.value}")

                return@launch
            }

            val updatedWeather = fetchAndUpdateWeather()
            currentWeather.postValue(updatedWeather)

            Timber.d("### Receiving LiveData with Entity ###")
            Timber.d("Value -> ${currentWeather.value}")

        }

        Timber.d("### Just Before Transforming Entity to UI ###")

        return transformLiveDataForUI(currentWeather)

    }

    private suspend fun fetchAndUpdateWeather(): DataWrapper<WeatherEntity>? {

        val currentWeatherLiveData = MutableLiveData<DataWrapper<WeatherEntity>>()

        try {

            val repository = HomeRepository.getInstance()

            Timber.d("### Just before fetching ###")
            repository.fetchAndStoreCurrentWeather()

            currentWeatherLiveData.value = repository.getWeatherFromDB()

            Timber.d("### Receiving Entity Inside LiveData ###")
            Timber.d("Entity -> ${currentWeatherLiveData.value}")

        } catch (exception: HttpException) {

            val wrapperForFailedWeatherUpdate = prepareWrapperForFailedFetch()
            currentWeatherLiveData.postValue(wrapperForFailedWeatherUpdate)

            logError(exception)

        } finally {

            Timber.d("### Just Before Returning LiveData with Entity ###")
            Timber.d("Entity -> ${currentWeatherLiveData.value}")
            return currentWeatherLiveData.value

        }
    }

    private fun prepareWrapperForFailedFetch(): DataWrapper<WeatherEntity> {

        val wrapperForFailedWeatherUpdate = DataWrapper<WeatherEntity>()
        wrapperForFailedWeatherUpdate.prepareFailure("Couldn't Retrieve Data From Server")

        return wrapperForFailedWeatherUpdate

    }

    private fun logError(exception: Exception) = Timber.d(exception.localizedMessage ?: "")

    private fun transformLiveDataForUI(weatherEntity: LiveData<DataWrapper<WeatherEntity>>): LiveData<DataWrapper<WeatherUI>> {

        Timber.d("### Transforming Entity to UI ###")

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