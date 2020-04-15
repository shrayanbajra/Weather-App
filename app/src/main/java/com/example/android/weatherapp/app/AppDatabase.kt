package com.example.android.weatherapp.app

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.weatherapp.data.local.WeatherDao
import com.example.android.weatherapp.data.local.WeatherEntity
import com.example.android.weatherapp.utils.AppUtils

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Returns the WeatherDao object so that we can perform operations on database
    abstract fun weatherDao(): WeatherDao

    companion object {

        // Volatile restricts from creating multiple instances of database in different threads
        @Volatile
        private var instance: AppDatabase? = null

        // Used synchronized to prevent multiple threads to access database instance
        fun getDatabaseInstance(): AppDatabase {
            synchronized(this) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        AppUtils.getApp().applicationContext,
                        AppDatabase::class.java,
                        "weather_database"
                    ).build()
                }
                return instance!!
            }
        }
    }
}