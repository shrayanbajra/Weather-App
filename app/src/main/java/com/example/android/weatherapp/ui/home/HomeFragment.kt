package com.example.android.weatherapp.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.view.View.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.android.weatherapp.R
import com.example.android.weatherapp.data.DataWrapper
import com.example.android.weatherapp.data.ui.WeatherUi
import com.example.android.weatherapp.databinding.FragmentHomeBinding
import com.example.android.weatherapp.utils.NetworkUtils
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class HomeFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    // TODO: Display Location in Settings (Fragment) (will add functionality to change it later)
    // TODO: Make Network Request according to Units chosen in Settings

    private lateinit var binding: FragmentHomeBinding
    private lateinit var snackbar: Snackbar

    private val viewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

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
        snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        showProgressBar()
        setupSharedPreferences()
        observeCurrentWeather()
        refreshCurrentWeather()
    }

    private fun setupSharedPreferences() {
        val sharedPref = PreferenceManager
            .getDefaultSharedPreferences(activity?.applicationContext)
        sharedPref.registerOnSharedPreferenceChangeListener(this)
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

    private fun checkForEmptyState(it: DataWrapper<WeatherUi>): Boolean {
        return it.wasFailure()
                && it.wrapperBody == WeatherUi()
                && NetworkUtils.hasNoInternetConnection()
    }

    private fun logStatus(it: DataWrapper<WeatherUi>, message: String) {
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

    private fun displayCurrentWeather(it: DataWrapper<WeatherUi>) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_weathers -> viewModel.deleteAll()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayNoInternetFeedback() {
        snackbar.setText("No Internet Connection!")
        snackbar.show()
    }

    private fun displayData(weatherInfo: WeatherUi) {
        binding.weatherUi = weatherInfo
        Glide.with(this)
            .load(weatherInfo.icon)
            .into(binding.imgWeatherIcon)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val sharedPrefKey = key ?: ""
        sharedPreferences?.let { sharedPref ->
            if (sharedPrefKey == "pref_units") {
                val unit = sharedPref.getString("pref_units", "")
                Toast.makeText(activity?.applicationContext, "$unit selected", Toast.LENGTH_SHORT)
                    .show()
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
        binding.progressBarHome.visibility = visibility
    }

    private fun setWeatherInformationVisibility(visibility: Int) {
        binding.constraintLayoutHome.visibility = visibility
    }
}