package com.example.android.weatherapp.app

fun String?.nullToEmpty(): String {
    return ""
}

fun Int?.nullToZero(): Int {
    return 0
}

fun Double?.nullToZero(): Double {
    return 0.0
}

fun Float?.nullToZero(): Float {
    return 0F
}

fun Long?.nullToZero(): Long {
    return 0L
}