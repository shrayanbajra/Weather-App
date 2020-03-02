package com.example.android.weatherapp.utils

import android.app.Application

object AppUtils {
    private lateinit var APP: Application

    fun init(app: Application) {
        if (!::APP.isInitialized) {
            APP = app
        }
    }

    fun getApp() = APP
}