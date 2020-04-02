package com.example.android.weatherapp.data.remote.response.weatherresponsefields


import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all")
    val all: Int = 0
)