package com.example.android.weatherapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.android.weatherapp.R
import com.example.android.weatherapp.data.ui.WeatherUi
import com.example.android.weatherapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    // Views
    private lateinit var imgWeatherCondition: ImageView
    private lateinit var btnRefresh: Button

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mappingViews(view)
    }

    private fun mappingViews(view: View) {
        imgWeatherCondition = view.findViewById(R.id.img_weather_icon)
        btnRefresh = view.findViewById(R.id.btn_refresh)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeWeather()
        refreshWeather()
    }

    private fun observeWeather() {
        viewModel.getWeatherLiveData()
            .observe(this, Observer {
                displayData(it)
            })
    }

    private fun refreshWeather() {
        btnRefresh.setOnClickListener {
            viewModel.updateWeather()
            observeWeatherUpdateStatus()
        }
    }

    private fun displayData(weatherInfo: WeatherUi) {
        binding.weatherUi = weatherInfo
        Glide.with(this)
            .load(weatherInfo.icon)
            .into(imgWeatherCondition)
    }

    private fun observeWeatherUpdateStatus() {
        viewModel.getWeatherUpdateStatus()
            .observe(this, Observer {
                Toast.makeText(activity, it.getMessage(), Toast.LENGTH_SHORT).show()
            })
    }
}
