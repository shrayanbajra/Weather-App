package com.example.android.weatherapp.baseclass

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment() {

    fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

}