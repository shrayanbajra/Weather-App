package com.example.android.weatherapp.di.app

import android.app.Application
import com.example.android.weatherapp.app.App
import com.example.android.weatherapp.di.app.modules.ActivityBuildersModule
import com.example.android.weatherapp.di.app.modules.AppModule
import com.example.android.weatherapp.di.app.modules.ViewModelFactoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBuildersModule::class, AppModule::class, ViewModelFactoryModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent

    }

}