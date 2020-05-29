package com.example.android.weatherapp.app

import android.app.Application
import com.example.android.weatherapp.utils.AppUtils
import com.facebook.stetho.Stetho
import timber.log.Timber
import timber.log.Timber.DebugTree

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppUtils.init(this)

        Stetho.initializeWithDefaults(this)
        Timber.plant(DebugTree())

    }
}