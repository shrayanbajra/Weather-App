package com.example.android.weatherapp.ui

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.android.weatherapp.R
import com.google.android.material.navigation.NavigationView
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_nav_host.*

class NavHostActivity : DaggerAppCompatActivity() {

    private lateinit var drawer: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_host)

        initToolbar()
        initNavDrawer()
        initNavController()
        initNavigationView()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun initNavDrawer() {
        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, R.string.open_navigation_drawer, R.string.close_navigation_drawer
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initNavController() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawer)
        NavigationUI.setupWithNavController(toolbar, navController, drawer)
    }

    private fun initNavigationView() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.nav_settings -> {
                    if (isDestinationValid(R.id.settingsFragment)) {
                        navigateToSettingsFragment()
                        true
                    } else
                        false
                }
                else -> false

            }
        }
    }

    private fun isDestinationValid(destination: Int): Boolean {
        return destination != navController.currentDestination?.id
    }

    private fun navigateToSettingsFragment() {
        navController.navigate(R.id.action_homeFragment_to_settingsFragment)
        drawer.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }
}
