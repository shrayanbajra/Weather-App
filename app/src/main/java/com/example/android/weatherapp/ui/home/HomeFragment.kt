package com.example.android.weatherapp.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.android.weatherapp.R
import com.example.android.weatherapp.app.AppPreferences
import com.example.android.weatherapp.app.EMPTY_STRING
import com.example.android.weatherapp.app.KEY_PREF_LOCATION
import com.example.android.weatherapp.app.KEY_PREF_UNITS
import com.example.android.weatherapp.data.DataWrapper
import com.example.android.weatherapp.data.ui.WeatherUI
import com.example.android.weatherapp.databinding.FragmentHomeBinding
import com.example.android.weatherapp.utils.NetworkUtils
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class HomeFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var snackbar: Snackbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSnackBar(view)
    }

    private fun initSnackBar(view: View) {
        snackbar = Snackbar.make(view, EMPTY_STRING, Snackbar.LENGTH_LONG)
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        showProgressBar()
        setupSharedPreferences()

        observeCurrentWeather()
        refreshCurrentWeather()
    }

    private fun setupSharedPreferences() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
        initPreferences(sharedPref)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
    }

    private fun initPreferences(sharedPref: SharedPreferences) {
        AppPreferences.LOCATION = sharedPref.getString(KEY_PREF_LOCATION, EMPTY_STRING) ?: ""
        AppPreferences.UNITS = sharedPref.getString(KEY_PREF_UNITS, EMPTY_STRING) ?: ""
    }

    override fun onResume() {
        super.onResume()
        setupSharedPreferences()
    }

    private fun observeCurrentWeather() {
        viewModel.getWeatherLiveData().observe(viewLifecycleOwner, Observer {
            hideProgressBar()
            logStatus(it, "Inside Observer")
            if (checkForEmptyState(it)) {
                logStatus(it, "Inside Empty State")
                displayEmptyState()
                return@Observer
            }
            if (it.wasFailure()) {
                logStatus(it, "Inside failure")
                setWeatherInformationVisibility(GONE)
                displayFailureFeedback(it.message)
                return@Observer
            }
            displayCurrentWeather(it)
        })
    }

    private fun checkForEmptyState(it: DataWrapper<WeatherUI>): Boolean {
        return it.wasFailure()
                && it.wrapperBody == WeatherUI()
                && NetworkUtils.hasNoInternetConnection()
    }

    private fun logStatus(it: DataWrapper<WeatherUI>, message: String) {
        Timber.d("Location -> ${AppPreferences.LOCATION}")
        Timber.d("Units -> ${AppPreferences.UNITS}")
        Timber.d(message)
        Timber.d("${it.status}")
        Timber.d(it.message)
        Timber.d("${it.wrapperBody}")
    }

    private fun displayFailureFeedback(failureMessage: String) {
        snackbar.setText(failureMessage)
        snackbar.show()
    }

    private fun displayEmptyState() {
        setEmptyStateVisibility(VISIBLE)
        setWeatherInformationVisibility(GONE)
    }

    private fun displayCurrentWeather(it: DataWrapper<WeatherUI>) {
        it.wrapperBody?.let { weatherInfo ->
            logStatus(it, "Inside success")
            displayData(weatherInfo)
        }
    }

    private fun refreshCurrentWeather() {
        binding.swipeRefreshLayoutHome.setOnRefreshListener {
            if (NetworkUtils.hasNoInternetConnection()) {
                displayNoInternetFeedback()
            } else {
                showProgressBar()
                viewModel.updateWeather()
            }
            binding.swipeRefreshLayoutHome.isRefreshing = false
        }
    }

    private fun displayNoInternetFeedback() {
        snackbar.setText("No Internet Connection!")
        snackbar.show()
    }

    private fun displayData(weatherInfo: WeatherUI) {
        binding.weatherUi = weatherInfo
        Timber.d("Weather for UI $weatherInfo")
        Glide.with(this)
            .load(weatherInfo.icon)
            .into(binding.imgWeatherIcon)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val sharedPrefKey = key ?: ""
        sharedPreferences?.let { sharedPref ->
            when (sharedPrefKey) {
                KEY_PREF_UNITS -> {
                    AppPreferences.UNITS = sharedPref.getString(KEY_PREF_UNITS, EMPTY_STRING) ?: ""
                }
                KEY_PREF_LOCATION -> {
                    AppPreferences.LOCATION =
                        sharedPref.getString(KEY_PREF_LOCATION, EMPTY_STRING) ?: ""
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun showProgressBar() {
        setProgressBarVisibility(VISIBLE)
        setWeatherInformationVisibility(INVISIBLE)
        setEmptyStateVisibility(GONE)
    }

    private fun hideProgressBar() {
        setProgressBarVisibility(GONE)
        setWeatherInformationVisibility(VISIBLE)
        setEmptyStateVisibility(GONE)
    }

    private fun setEmptyStateVisibility(visibility: Int) {
        binding.imgEmptyState.visibility = visibility
        binding.emptyStateDescription.visibility = visibility
    }

    private fun setProgressBarVisibility(visibility: Int) {
        binding.progressBar.visibility = visibility
    }

    private fun setWeatherInformationVisibility(visibility: Int) {
        binding.constraintLayoutHome.visibility = visibility
    }
}