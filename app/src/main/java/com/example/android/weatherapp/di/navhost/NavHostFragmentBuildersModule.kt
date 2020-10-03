package com.example.android.weatherapp.di.navhost

import com.example.android.weatherapp.di.home.HomeModule
import com.example.android.weatherapp.ui.home.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NavHostFragmentBuildersModule {

    @ContributesAndroidInjector(
        modules = [HomeModule::class, NavHostViewModelsModule::class]
    )
    abstract fun contributeHomeFragment(): HomeFragment

}