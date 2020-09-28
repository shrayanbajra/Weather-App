package com.example.android.weatherapp.app

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android.weatherapp.data.local.WeatherDao
import com.example.android.weatherapp.data.local.WeatherEntity

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Returns the WeatherDao object so that we can perform operations on database
    abstract fun weatherDao(): WeatherDao

}