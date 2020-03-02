package com.example.android.weatherapp.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.android.weatherapp.R
import com.example.android.weatherapp.data.ui.WeatherUi
import com.example.android.weatherapp.databinding.FragmentHomeBinding
import com.example.android.weatherapp.utils.NetworkUtils

class HomeFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var imgWeatherCondition: ImageView
    private lateinit var swipeRefreshListener: SwipeRefreshLayout
    private lateinit var binding: FragmentHomeBinding
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

        initViews(view)
    }

    private fun initViews(view: View) {
        imgWeatherCondition = view.findViewById(R.id.img_weather_icon)
        swipeRefreshListener = view.findViewById(R.id.swipe_refresh_layout_home)
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
        viewModel.getWeatherLiveData().observe(viewLifecycleOwner, Observer { weatherInfo ->
            hideProgressBar()
            displayData(weatherInfo)
            Log.d("HomeFragment", "observed weather -> $weatherInfo")
        })
    }

    private fun refreshCurrentWeather() {
        swipeRefreshListener.setOnRefreshListener {
            if (NetworkUtils.hasNoInternetConnection()) {
                Toast.makeText(
                    activity?.applicationContext,
                    "No Internet Connection",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showProgressBar()
                viewModel.updateWeather()
            }
            swipeRefreshListener.isRefreshing = false
        }
    }

    private fun displayData(weatherInfo: WeatherUi) {
        binding.weatherUi = weatherInfo
        Glide.with(this)
            .load(weatherInfo.icon)
            .into(imgWeatherCondition)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val sharedPrefKey = key ?: ""
        sharedPreferences?.let {
            if (sharedPrefKey == "pref_units") {
                val units = sharedPreferences.getString("pref_units", "")
                Toast.makeText(activity?.applicationContext, "$units selected", Toast.LENGTH_SHORT)
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
    }

    private fun hideProgressBar() {
        binding.progressBarHome.visibility = GONE
        binding.constraintLayoutHome.visibility = VISIBLE
    }
}