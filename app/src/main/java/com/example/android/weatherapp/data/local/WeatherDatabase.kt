package com.example.android.weatherapp.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.weatherapp.utils.AppUtils

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    /**
     * Return the WeatherDao object so that we can perform data access operations
     */
    abstract fun weatherDao(): WeatherDao

    /**
     * Singleton pattern
     */
    companion object {

        /**
         * Single Instance of Database
         */
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        /**
         * Synchronized to prevent multiple threads to access database instance
         */
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