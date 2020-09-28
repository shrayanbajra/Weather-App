package com.example.android.weatherapp.di.app.modules

import com.example.android.weatherapp.di.navhost.NavHostFragmentBuildersModule
import com.example.android.weatherapp.ui.NavHostActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(
        modules = [NavHostFragmentBuildersModule::class]
    )
    abstract fun contributeNavHostActivity(): NavHostActivity

}