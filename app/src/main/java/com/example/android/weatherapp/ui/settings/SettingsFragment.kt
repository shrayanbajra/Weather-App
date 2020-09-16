package com.example.android.weatherapp.ui.settings

import android.os.Bundle
import androidx.preference.*
import com.example.android.weatherapp.R
import com.example.android.weatherapp.app.AppPreferences
import com.example.android.weatherapp.utils.EMPTY_STRING
import com.example.android.weatherapp.utils.KEY_PREF_LOCATION
import com.example.android.weatherapp.utils.KEY_PREF_UNITS

class SettingsFragment : PreferenceFragmentCompat(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey)

        bindSummaryValue(findPreference(KEY_PREF_LOCATION))
        bindSummaryValue(findPreference(KEY_PREF_UNITS))
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?, pref: Preference?
    ): Boolean {
        return true
    }

    companion object {
        fun bindSummaryValue(preference: Preference?) {
            preference?.let {
                preference.onPreferenceChangeListener = preferenceChangeListener
                preferenceChangeListener.onPreferenceChange(
                    preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, EMPTY_STRING)
                )
            }
        }

        private val preferenceChangeListener: Preference.OnPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                val selectedValue = newValue.toString()

                when (preference) {
                    is ListPreference -> AppPreferences.UNITS = selectedValue
                    is EditTextPreference -> AppPreferences.LOCATION = selectedValue
                }
                true
            }
    }
}
