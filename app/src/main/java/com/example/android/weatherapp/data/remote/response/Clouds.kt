package com.example.android.sunshine.data.remote.response


import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all")
    val all: Int = 0
)