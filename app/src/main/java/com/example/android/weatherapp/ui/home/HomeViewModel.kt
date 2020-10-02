package com.example.android.weatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.weatherapp.data.local.CacheMapper
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.ui.WeatherUi
import com.example.android.weatherapp.utils.Resource
import com.example.android.weatherapp.utils.SingleEventLiveData
import com.example.android.weatherapp.utils.Status
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel
@Inject
constructor(var repository: HomeRepository) : ViewModel() {

    fun getCachedWeather(): LiveData<Resource<WeatherUi>> {

        val cachedWeather = SingleEventLiveData<Resource<WeatherUi>>()
        cachedWeather.value = Resource.loading(null)

        viewModelScope.launch(IO) {

            val resource = repository.getCachedWeather()

            when {
                resource.isSuccessful() -> {

                    val successResource = getSuccessResource(resource.data!!)
                    cachedWeather.postValue(successResource)

                }
                resource.status == Status.ERROR -> {

                    val errorResource = getErrorResource(resource)
                    cachedWeather.postValue(errorResource)

                }
            }

        }
        return cachedWeather

    }

    private fun getErrorResource(resource: Resource<WeatherEntity>): Resource<Nothing> {
        val errorMessage = resource.message ?: "Something went wrong"
        return Resource.error(msg = errorMessage, data = null)
    }

    fun getCurrentWeather(): LiveData<Resource<WeatherUi>> {

        val currentWeather = SingleEventLiveData<Resource<WeatherUi>>()
        viewModelScope.launch(IO) {

            repository.getCurrentWeather().collect { resource ->

                when {
                    resource.isSuccessful() -> {

                        val successResource = getSuccessResource(resource.data!!)
                        currentWeather.postValue(successResource)

                    }
                    resource.status == Status.LOADING -> {

                        val loadingResource = Resource.loading(null)
                        currentWeather.postValue(loadingResource)

                    }
                    resource.status == Status.ERROR -> {

                        val errorResource = resource.message?.let { Resource.error(msg = it, null) }
                        currentWeather.postValue(errorResource)

                    }
                }

            }

        }
        return currentWeather

    }

    private fun getSuccessResource(data: WeatherEntity): Resource<WeatherUi> {
        val weatherUi = CacheMapper.transformEntityToUi(data)
        return Resource.success(data = weatherUi)
    }

    private fun logError(exception: Exception) = Timber.d(exception.localizedMessage ?: "")

}