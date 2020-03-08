package com.example.android.weatherapp.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.android.weatherapp.R
import com.example.android.weatherapp.data.ui.WeatherUi
import com.example.android.weatherapp.databinding.FragmentHomeBinding
import com.example.android.weatherapp.utils.NetworkUtils
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    // TODO: Display Location in Settings (Fragment) (will add functionality to change it later)
    // TODO: Make Network Request according to Units chosen in Settings
    // TODO: Need to Make a Good Looking Empty State when there are no entries in database

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

        binding.deleteAll.setOnClickListener {
            viewModel.deleteAll()
        }

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
            Log.d("HomeFragment:", "Inside Observer")
            Log.d("HomeFragment:", "${it.status}")
            Log.d("HomeFragment:", "${it.message}")
            Log.d("HomeFragment:", "${it.wrapperBody}")
            if (it.wasFailure()) {
                Log.d("HomeFragment:", "Inside failure")
                Log.d("HomeFragment:", "${it.status}")
                Log.d("HomeFragment:", it.message)
                Log.d("HomeFragment:", "${it.wrapperBody}")
                displayFailureFeedback(it.message)
                return@Observer
            }
            if (it.wrapperBody == WeatherUi() && NetworkUtils.hasNoInternetConnection()) {
                Log.d("HomeFragment:", "Inside Empty State")
                Log.d("HomeFragment:", "${it.status}")
                Log.d("HomeFragment:", it.message)
                Log.d("HomeFragment:", "${it.wrapperBody}")
                displayEmptyState()
                return@Observer
            }
            it.wrapperBody?.let { weatherInfo ->
                Log.d("HomeFragment:", "Inside success")
                Log.d("HomeFragment:", "${it.status}")
                Log.d("HomeFragment:", it.message)
                Log.d("HomeFragment:", "${it.wrapperBody}")
                displayData(weatherInfo)
            }
        })
    }

    private fun displayFailureFeedback(failureMessage: String) {
        snackbar.setText(failureMessage)
        snackbar.show()
    }

    private fun displayEmptyState() {
        binding.imgEmptyState.visibility = VISIBLE
        binding.emptyStateDescription.visibility = VISIBLE

        binding.constraintLayoutHome.visibility = GONE
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
        binding.progressBarHome.visibility = VISIBLE
        binding.constraintLayoutHome.visibility = INVISIBLE

        binding.imgEmptyState.visibility = GONE
        binding.emptyStateDescription.visibility = GONE
    }

    private fun hideProgressBar() {
        binding.progressBarHome.visibility = GONE
        binding.constraintLayoutHome.visibility = VISIBLE

        binding.imgEmptyState.visibility = GONE
        binding.emptyStateDescription.visibility = GONE
    }
}