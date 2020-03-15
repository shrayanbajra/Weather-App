package com.example.android.weatherapp.data.ui

data class WeatherUi(
    var location: String = "",
    var weatherCondition: String = "",
    var weatherDescription: String = "",
    var temperature: String = "",
    var minTemperature: String = "",
    var maxTemperature: String = "",
    var icon: String = ""
)