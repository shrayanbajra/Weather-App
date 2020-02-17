package com.example.android.weatherapp.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

@Suppress("DEPRECATION")
class AppUtils {

    companion object {

        /**
         * Preferences
         */
        const val LOCATION = "Thimi"

        /*
         * Temperature
         * For Fahrenheit use units = imperial
         * For Celsius use units = metric
         */
        const val UNITS = "metric"

        const val API_KEY = "cedb0bc4eeb308672d3377ecf12724e9"

        /**
         * Application
         */
        private lateinit var APP: Application

        fun init(app: Application) {
            if (!::APP.isInitialized) {
                APP = app
            }
        }

        fun getApp(): Application {
            return APP
        }

        // Check network status
        fun isNotConnectedToInternet(): Boolean {
            val cm = getApp()
                .getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting != true
        }
    }
}