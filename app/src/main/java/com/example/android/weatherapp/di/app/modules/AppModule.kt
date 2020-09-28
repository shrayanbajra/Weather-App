package com.example.android.weatherapp.di.app.modules

import android.app.Application
import androidx.room.Room
import com.example.android.weatherapp.data.local.WeatherDao
import com.example.android.weatherapp.data.local.WeatherDatabase
import com.example.android.weatherapp.network.OpenWeatherApi
import com.example.android.weatherapp.utils.BASE_ADDRESS
import com.example.android.weatherapp.utils.DATABASE_NAME
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
    fun provideAppDatabase(application: Application): WeatherDatabase {
        return Room.databaseBuilder(application, WeatherDatabase::class.java, DATABASE_NAME).build()
    }

    @Singleton
    @Provides
    fun provideWeatherDao(weatherDatabase: WeatherDatabase): WeatherDao {
        return weatherDatabase.weatherDao()
    }

}