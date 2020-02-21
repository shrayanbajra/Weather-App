package com.example.android.weatherapp.utils

import android.app.Application
import com.facebook.stetho.Stetho

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        /*
         * Providing application to AppUtils
         * for being used in different places like Database instantiation
         */
        AppUtils.init(this)
    }
}