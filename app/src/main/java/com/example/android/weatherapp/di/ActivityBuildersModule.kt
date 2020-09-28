package com.example.android.weatherapp.di

import com.example.android.weatherapp.ui.NavHostActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeNavHostActivity(): NavHostActivity

}