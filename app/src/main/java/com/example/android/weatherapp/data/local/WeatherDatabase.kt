package com.example.android.weatherapp.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.weatherapp.utils.AppUtils

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    // Returns the WeatherDao object so that we can perform operations on database
    abstract fun weatherDao(): WeatherDao

    companion object {
        // Volatile restricts from creating multiple instances of database in different threads
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        // Synchronized to prevent multiple threads to access database instance
        fun getDatabaseInstance(): WeatherDatabase {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        AppUtils.getApp().applicationContext,
                        WeatherDatabase::class.java,
                        "weather_database"
                    ).build()
                }
                return INSTANCE!!
            }
        }
    }
}