package com.example.android.weatherapp.data.remote.currentweather.fields


import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all")
    val all: Int = 0
)