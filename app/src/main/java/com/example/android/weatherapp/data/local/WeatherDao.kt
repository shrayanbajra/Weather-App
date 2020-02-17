package com.example.android.sunshine.data.local

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
    suspend fun getWeatherInfoFromDatabase(): WeatherEntity

    @Query("SELECT * FROM weather_table WHERE location = :location")
    suspend fun getWeatherfor(location: String): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherIntoDatabase(weatherEntity: WeatherEntity)

    @Query("DELETE FROM weather_table")
    suspend fun deleteWeatherFromDatabase()
}