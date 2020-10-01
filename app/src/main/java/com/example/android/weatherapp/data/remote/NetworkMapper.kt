package com.example.android.weatherapp.data.remote

import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.data.remote.currentweather.WeatherResponse
import java.math.RoundingMode
import java.text.DecimalFormat

object NetworkMapper {

    fun transformResponseToEntity(weatherResponse: WeatherResponse): WeatherEntity {

        val formatter = DecimalFormat("##")
        formatter.roundingMode = RoundingMode.CEILING

        return WeatherEntity().apply {

            weatherResponse.let { response ->

                location = response.name
                weatherCondition = response.weather[0].main
                weatherDescription = response.weather[0].description

                temperature = formatter.format(response.main.temp).toString()
                minTemperature = formatter.format(response.main.tempMin).toString()
                maxTemperature = formatter.format(response.main.tempMax).toString()

                imageUri = getIconFromUri(response.weather[0].icon)

            }
        }
    }

    // Retrieving icon for Weather Condition
    private fun getIconFromUri(icon: String): String {

        return String.format("https://api.openweathermap.org/img/w/%s.png", icon)

    }

}