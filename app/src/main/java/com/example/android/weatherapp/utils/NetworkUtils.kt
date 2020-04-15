@file:Suppress("DEPRECATION")

package com.example.android.weatherapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object NetworkUtils {

    fun hasNoInternetConnection(): Boolean {
        val connectivityManager =
            AppUtils
                .getApp()
                .getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting != true
    }
}