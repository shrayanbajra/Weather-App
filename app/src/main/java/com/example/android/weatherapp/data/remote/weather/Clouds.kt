package com.example.android.weatherapp.data.remote.weather


import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all")
    var all: Int? = 0
)