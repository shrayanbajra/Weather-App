package com.example.android.weatherapp.data.local

import com.example.android.weatherapp.data.ui.WeatherUi

object CacheMapper {

    fun transformEntityToUi(cacheEntity: WeatherEntity): WeatherUi {

        return WeatherUi().apply {

            cacheEntity.let { entity ->

                location = entity.location
                weatherCondition = entity.weatherCondition
                weatherDescription = entity.weatherDescription

                temperature = entity.temperature
                minTemperature = entity.minTemperature
                maxTemperature = entity.maxTemperature

                icon = entity.imageUri

            }
        }

    }

}