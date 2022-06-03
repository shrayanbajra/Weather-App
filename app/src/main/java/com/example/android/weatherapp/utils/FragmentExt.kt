package com.example.android.weatherapp.utils

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showShortSnackbar(message: String, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(requireView(), message, length).show()
}

fun Fragment.showShortSnackbar(@StringRes message: Int, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(requireView(), message, length).show()
}