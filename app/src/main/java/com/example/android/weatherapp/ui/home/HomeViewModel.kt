package com.example.android.weatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.weatherapp.data.local.CacheMapper
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.ui.WeatherUi
import com.example.android.weatherapp.utils.Resource
import com.example.android.weatherapp.utils.Status
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel
@Inject
constructor(var repository: HomeRepository) : ViewModel() {

    fun getCachedWeather(): LiveData<Resource<WeatherUi>> {

        val cachedWeather = MutableLiveData<Resource<WeatherUi>>()
        cachedWeather.value = Resource.loading(null)

        viewModelScope.launch {

            val resource = repository.getCachedWeather()

            if (isResourceSuccess(resource)) {

                val successResource = getSuccessResource(resource.data!!)
                cachedWeather.postValue(successResource)

            } else {

                val errorResource = Resource.error(msg = resource.message ?: "", data = null)
                cachedWeather.postValue(errorResource)

            }

        }
        return cachedWeather

    }

    fun getCurrentWeather(): LiveData<Resource<WeatherUi>> {

        val currentWeather = MutableLiveData<Resource<WeatherUi>>()
        viewModelScope.launch {

            repository.getCurrentWeather().collect { resource ->

                if (isResourceSuccess(resource)) {

                    val successResource = getSuccessResource(resource.data!!)
                    currentWeather.postValue(successResource)

                } else {

                    val errorResource = Resource.error(msg = resource.message ?: "", null)
                    currentWeather.postValue(errorResource)

                }

            }

        }
        return currentWeather

    }

    private fun isResourceSuccess(resource: Resource<WeatherEntity>) =
        resource.status == Status.SUCCESS && resource.data != null

    private fun getSuccessResource(data: WeatherEntity): Resource<WeatherUi> {
        val weatherUi = CacheMapper.transformEntityToUI(data)
        return Resource.success(data = weatherUi)
    }

    private fun logError(exception: Exception) = Timber.d(exception.localizedMessage ?: "")

}