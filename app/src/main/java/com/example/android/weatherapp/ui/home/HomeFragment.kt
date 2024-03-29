package com.example.android.weatherapp.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.android.weatherapp.R
import com.example.android.weatherapp.app.AppPreferences
import com.example.android.weatherapp.data.ui.WeatherUi
import com.example.android.weatherapp.databinding.FragmentHomeBinding
import com.example.android.weatherapp.di.ViewModelProviderFactory
import com.example.android.weatherapp.utils.*
import com.example.android.weatherapp.utils.extension_functions.getEmptyIfNull
import com.example.android.weatherapp.utils.extension_functions.showShortSnackbar
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_home.view.*
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : DaggerFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    private val viewModel by lazy {
        ViewModelProvider(this, providerFactory).get(HomeViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideAllViews()
        val sharedPref = getSharedPreferences()
        initPreferences(sharedPref)
    }

    private fun initPreferences(sharedPref: SharedPreferences) {
        AppPreferences.LOCATION =
            sharedPref.getString(KEY_PREF_LOCATION, DEFAULT_LOCATION).getEmptyIfNull()
        AppPreferences.UNITS = sharedPref.getString(KEY_PREF_UNITS, DEFAULT_UNITS).getEmptyIfNull()
    }

    private fun hideAllViews() {
        setEmptyStateVisibility(GONE)
        setProgressBarVisibility(GONE)
        setWeatherInformationVisibility(GONE)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val hasNoInternet = NetworkUtils.hasNoInternetConnection(requireContext())
        if (hasNoInternet) getCachedWeather()
        else getCurrentWeather()

        swipeRefreshListener()
    }

    private fun getCachedWeather() {
        viewModel.getCachedWeather().observe(viewLifecycleOwner) {

            when {
                it.isLoading() -> {
                    showProgressBar()
                }

                it.isFailure() -> {
                    prepareEmptyStateForNoInternetConnection()
                    showEmptyState()
                }

                it.isSuccessful() -> {
                    makeWeatherInfoVisible()
                    it.data?.let { weatherInfo -> displayCurrentWeather(weatherInfo) }
                }
            }
        }

    }

    private fun prepareEmptyStateForNoInternetConnection() {
        binding.imgEmptyState.setImageResource(R.drawable.img_no_internet_connection)
        val title = getString(R.string.no_internet_connection)
        binding.emptyStateDescription.tvEmptyStateDescriptionTitle.text = title
        val description = getString(R.string.please_check_your_internet_connection_and_try_again)
        binding.emptyStateDescription.tvEmptyStateDescriptionBody.text = description
    }

    override fun onResume() {
        super.onResume()

        val sharedPref = getSharedPreferences()
        sharedPref.registerOnSharedPreferenceChangeListener(this)
    }

    private fun displayFailureFeedback(failureMessage: String) = showShortSnackbar(failureMessage)

    private fun showEmptyState() {
        setEmptyStateVisibility(VISIBLE)

        setWeatherInformationVisibility(GONE)
        setProgressBarVisibility(GONE)
    }

    private fun displayCurrentWeather(weatherInfo: WeatherUi) {
        Timber.d("Weather for UI $weatherInfo")

        binding.weatherUi = weatherInfo

        Glide.with(this)
            .load(weatherInfo.icon)
            .into(binding.imgWeatherIcon)
    }

    private fun swipeRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {

            val hasNoInternet = NetworkUtils.hasNoInternetConnection(requireContext())
            if (hasNoInternet) displayNoInternetFeedback()
            else getCurrentWeather()

            binding.swipeRefreshLayout.isRefreshing = false

        }
    }

    private fun getCurrentWeather() {
        viewModel.getCurrentWeather().observe(viewLifecycleOwner) { it ->

            when {
                it.isLoading() -> {
                    showProgressBar()
                }

                it.isFailure() -> {
                    prepareEmptyState(it)
                    showEmptyState()
                    it.message?.let { msg -> displayFailureFeedback(msg) }
                }

                it.isSuccessful() -> {
                    makeWeatherInfoVisible()
                    it.data?.let { displayCurrentWeather(it) }
                    showShortSnackbar(R.string.weather_information_updated)
                }
            }

        }
    }

    private fun prepareEmptyState(it: Resource<WeatherUi>) {
        binding.imgEmptyState.setImageResource(R.drawable.ic_not_found)
        binding.emptyStateDescription.tvEmptyStateDescriptionTitle.text = it.message
        val description = getString(
            R.string.couldnt_find_weather_information_for_location, AppPreferences.LOCATION
        )
        binding.emptyStateDescription.tvEmptyStateDescriptionBody.text = description
    }

    private fun displayNoInternetFeedback() =
        showShortSnackbar(getString(R.string.no_internet_connection))

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        sharedPreferences?.let { sharedPref ->

            when (key) {

                KEY_PREF_UNITS -> {
                    AppPreferences.UNITS =
                        sharedPref.getString(KEY_PREF_UNITS, EMPTY_STRING).getEmptyIfNull()
                }

                KEY_PREF_LOCATION -> {
                    AppPreferences.LOCATION =
                        sharedPref.getString(KEY_PREF_LOCATION, EMPTY_STRING).getEmptyIfNull()
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun getSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
    }

    private fun showProgressBar() {
        setProgressBarVisibility(VISIBLE)

        setWeatherInformationVisibility(INVISIBLE)
        setEmptyStateVisibility(GONE)
    }

    private fun makeWeatherInfoVisible() {
        setWeatherInformationVisibility(VISIBLE)

        setProgressBarVisibility(GONE)
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