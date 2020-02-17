package com.example.android.weatherapp.data.remote


import com.example.android.weatherapp.data.remote.weather.*
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("base")
    var base: String? = "",
    @SerializedName("clouds")
    var clouds: Clouds? = Clouds(),
    @SerializedName("cod")
    var cod: Int? = 0,
    @SerializedName("coord")
    var coord: Coord? = Coord(),
    @SerializedName("dt")
    var dt: Int? = 0,
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("main")
    var main: Main? = Main(),
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("sys")
    var sys: Sys? = Sys(),
    @SerializedName("visibility")
    var visibility: Int? = 0,
    @SerializedName("weather")
    var weather: List<Weather?>? = listOf(),
    @SerializedName("wind")
    var wind: Wind? = Wind()
)