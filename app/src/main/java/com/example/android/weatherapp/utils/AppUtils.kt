@file:Suppress("DEPRECATION")

package com.example.android.weatherapp.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

@Suppress("DEPRECATION")
class AppUtils {

    companion object {
        // Preferences
        const val LOCATION = "London"
        const val UNITS = "metric" // metric for Celsius and imperial for Fahrenheit
        const val API_KEY = "cedb0bc4eeb308672d3377ecf12724e9"

        // Application
        private lateinit var APP: Application

        fun init(app: Application) {
            if (!::APP.isInitialized) {
                APP = app
            }
        }

        fun getApp() = APP

        // Network Status
        fun hasNoInternetConnection(): Boolean {
            val connectivityManager = getApp().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting != true
        }
    }
}