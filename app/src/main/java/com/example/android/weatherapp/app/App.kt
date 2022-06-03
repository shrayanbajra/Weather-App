package com.example.android.weatherapp.app

import com.example.android.weatherapp.di.app.DaggerAppComponent
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber
import timber.log.Timber.DebugTree

class App : DaggerApplication() {

    companion object {

        lateinit var instance: App

    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        Stetho.initializeWithDefaults(this)
        Timber.plant(DebugTree())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }
}