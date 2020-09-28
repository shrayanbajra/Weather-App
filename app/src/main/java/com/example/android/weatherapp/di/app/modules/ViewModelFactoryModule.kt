package com.example.android.weatherapp.di.app.modules

import androidx.lifecycle.ViewModelProvider
import com.example.android.weatherapp.di.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelProviderFactory(providerFactory: ViewModelProviderFactory): ViewModelProvider.Factory

}