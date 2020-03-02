package com.example.android.weatherapp.app

import android.app.Application
import com.example.android.weatherapp.utils.AppUtils
import com.facebook.stetho.Stetho

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        AppUtils.init(this)
    }
}