package com.example.android.weatherapp.di.navhost

import androidx.lifecycle.ViewModel
import com.example.android.weatherapp.di.ViewModelKey
import com.example.android.weatherapp.ui.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class NavHostViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

}