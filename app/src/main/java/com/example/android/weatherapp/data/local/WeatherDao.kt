package com.example.android.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object
 */
@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather_table")
    suspend fun getWeatherFromDatabase(): WeatherEntity

    @Query("SELECT * FROM weather_table WHERE location = :location")
    suspend fun getWeatherFor(location: String): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherIntoDatabase(weatherEntity: WeatherEntity)

    @Query("DELETE FROM weather_table")
    suspend fun deleteWeatherFromDatabase()
}