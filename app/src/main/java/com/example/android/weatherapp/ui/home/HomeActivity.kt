package com.example.android.weatherapp.ui.home

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.android.weatherapp.R
import com.example.android.weatherapp.data.ui.WeatherUi
import com.example.android.weatherapp.databinding.ActivityMainBinding
import org.jetbrains.anko.toast

class HomeActivity : AppCompatActivity() {

    // Views
    private lateinit var imgWeatherCondition: ImageView
    private lateinit var btnRefresh: Button

    private lateinit var binding: ActivityMainBinding
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mappingViews()
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
            .observe(this, Observer { wasSuccess ->
                if (wasSuccess) {
                    toast("Weather Information Updated")
                } else {
                    toast("Unable to update Weather Information")
                }
            })
    }

    private fun mappingViews() {
        imgWeatherCondition = findViewById(R.id.img_weather_icon)
        btnRefresh = findViewById(R.id.btn_refresh)
    }
}
