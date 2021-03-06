package com.example.android.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
data class WeatherEntity(

    @PrimaryKey
    var location: String = "",
    var weatherCondition: String = "",
    var weatherDescription: String = "",
    var temperature: String = "",
    var minTemperature: String = "",
    var maxTemperature: String = "",
    var isRecent: Boolean = false,
    var imageUri: String = ""
)