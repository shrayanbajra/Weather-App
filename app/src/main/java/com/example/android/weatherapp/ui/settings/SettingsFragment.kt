package com.example.android.weatherapp.ui.settings

import android.os.Bundle
import androidx.preference.*
import com.example.android.weatherapp.R

class SettingsFragment : PreferenceFragmentCompat(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey)

        bindSummaryValue(findPreference("pref_units"))
        bindSummaryValue(findPreference("pref_location"))
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
                        .getString(preference.key, "")
                )
            }
        }

        private val preferenceChangeListener: Preference.OnPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                val selectedValue = newValue.toString()

                when (preference) {
                    is ListPreference -> {
                        val listPreference: ListPreference = preference
                        val index: Int = listPreference.findIndexOfValue(selectedValue)
                        preference.summary = listPreference.entries[index]
                    }
                    is EditTextPreference -> {
                        preference.setSummary(selectedValue)
                    }
                }
                true
            }
    }
}
