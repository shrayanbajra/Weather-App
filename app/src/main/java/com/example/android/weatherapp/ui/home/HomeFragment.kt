package com.example.android.weatherapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.android.weatherapp.R
import com.example.android.weatherapp.data.ui.WeatherUi
import com.example.android.weatherapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

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

        observeCurrentWeather()
        refreshCurrentWeather()
    }

    private fun observeCurrentWeather() {
        viewModel.getWeatherLiveData().observe(viewLifecycleOwner, Observer { weatherInfo ->
            displayData(weatherInfo)
            Log.d("HomeFragment", "observed weather -> $weatherInfo")
        })
    }

    private fun refreshCurrentWeather() {
        swipeRefreshListener.setOnRefreshListener {
            viewModel.updateWeather()
            observeWeatherUpdateStatus()
            swipeRefreshListener.isRefreshing = false
        }
    }

    private fun displayData(weatherInfo: WeatherUi) {
        binding.weatherUi = weatherInfo
        Glide.with(this)
            .load(weatherInfo.icon)
            .into(imgWeatherCondition)
    }

    private fun observeWeatherUpdateStatus() {
        viewModel.getWeatherUpdateStatus().observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity, it.getMessage(), Toast.LENGTH_SHORT).show()
        })
    }
}
