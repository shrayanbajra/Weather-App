package com.example.android.weatherapp.di.home

import androidx.lifecycle.ViewModel
import com.example.android.weatherapp.di.ViewModelKey
import com.example.android.weatherapp.ui.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class HomeViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

}