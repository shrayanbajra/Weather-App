package com.example.android.weatherapp.data.remote.weather


import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("humidity")
    var humidity: Int? = 0,
    @SerializedName("pressure")
    var pressure: Int? = 0,
    @SerializedName("temp")
    var temp: Double? = 0.0,
    @SerializedName("temp_max")
    var tempMax: Double? = 0.0,
    @SerializedName("temp_min")
    var tempMin: Double? = 0.0
)