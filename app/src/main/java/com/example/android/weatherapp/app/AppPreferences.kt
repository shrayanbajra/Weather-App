package com.example.android.weatherapp.app

import com.example.android.weatherapp.BuildConfig
import com.example.android.weatherapp.utils.EMPTY_STRING

class AppPreferences {

    companion object {

        var LOCATION: String = EMPTY_STRING
        var UNITS: String = EMPTY_STRING  // metric for Celsius and imperial for Fahrenheit

        const val API_KEY = BuildConfig.API_KEY
    }
}