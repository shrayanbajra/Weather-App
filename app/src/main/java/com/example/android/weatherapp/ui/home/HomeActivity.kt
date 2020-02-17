package com.example.android.weatherapp.ui.home

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.android.sunshine.R
import com.example.android.sunshine.data.ui.WeatherUi
import com.example.android.sunshine.databinding.ActivityMainBinding
import org.jetbrains.anko.toast

class HomeActivity : AppCompatActivity() {

    // Views
    private lateinit var imgWeatherCondition: ImageView
    private lateinit var btnRefresh: Button

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mappingViews()

        val viewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)

        viewModel.getWeatherLiveData().observe(this, Observer { weatherInfo ->
            displayData(weatherInfo)
        })

        btnRefresh.setOnClickListener {
            viewModel.updateWeather()

            viewModel.getWeatherUpdateStatus().observe(this, Observer { itWasSuccessful ->
                if (itWasSuccessful) {
                    toast("Weather Information Updated")
                } else {
                    toast("Unable to update Weather Information")
                }
            })
        }
    }

    private fun displayData(weatherInfo: WeatherUi?) {
        binding.weatherUi = weatherInfo
        Glide.with(this)
            .load(weatherInfo!!.icon)
            .into(imgWeatherCondition)
    }

    private fun mappingViews() {
        imgWeatherCondition = findViewById(R.id.img_weather_icon)
        btnRefresh = findViewById(R.id.btn_refresh)
    }
}
