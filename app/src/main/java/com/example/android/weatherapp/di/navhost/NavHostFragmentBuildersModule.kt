package com.example.android.weatherapp.di.navhost

import com.example.android.weatherapp.di.home.HomeModule
import com.example.android.weatherapp.di.home.HomeViewModelsModule
import com.example.android.weatherapp.ui.home.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NavHostFragmentBuildersModule {

    @ContributesAndroidInjector(
        modules = [HomeModule::class, HomeViewModelsModule::class]
    )
    abstract fun contributeHomeFragment(): HomeFragment

}