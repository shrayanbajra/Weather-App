package com.example.android.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    // Returns the WeatherDao object so that we can perform operations on database
    abstract fun weatherDao(): WeatherDao

}