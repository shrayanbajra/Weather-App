package com.example.android.weatherapp.di.app.modules

import android.app.Application
import androidx.room.Room
import com.example.android.weatherapp.app.AppDatabase
import com.example.android.weatherapp.data.local.WeatherDao
import com.example.android.weatherapp.network.OpenWeatherApi
import com.example.android.weatherapp.utils.BASE_ADDRESS
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideOpenWeatherApi(): OpenWeatherApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_ADDRESS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(OpenWeatherApi::class.java)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "weather_database")
            .build()
    }

    @Singleton
    @Provides
    fun provideWeatherDao(appDatabase: AppDatabase): WeatherDao {
        return appDatabase.weatherDao()
    }

}