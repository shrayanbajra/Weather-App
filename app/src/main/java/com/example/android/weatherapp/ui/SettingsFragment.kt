package com.example.android.weatherapp.ui

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.android.weatherapp.R

class SettingsFragment : PreferenceFragmentCompat(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey)
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?, pref: Preference?
    ): Boolean {
        return true
    }
}
