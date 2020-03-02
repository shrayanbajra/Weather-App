package com.example.android.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeatherIntoDatabase(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM weather_table WHERE location = :location")
    suspend fun getCurrentWeatherFor(location: String): WeatherEntity?

    @Query("DELETE FROM weather_table")
    suspend fun deleteWeathersFromDatabase()
}