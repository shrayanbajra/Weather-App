package com.example.android.weatherapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.android.weatherapp.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_nav_host.*

class NavHostActivity : AppCompatActivity() {

    private lateinit var drawer: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_host)

        initToolbar()
        initDrawer()
        initNavController()
        initNavigationView()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun initDrawer() {
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
                R.id.nav_settings -> navigateToSettingsFragment()
                else -> false
            }
        }
    }

    private fun navigateToSettingsFragment(): Boolean {
        var isValidDestination = false
        if (isDestinationValid(R.id.settingsFragment)) {
            Toast.makeText(applicationContext, "Settings Selected!", Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.action_homeFragment_to_settingsFragment)
            drawer.closeDrawer(GravityCompat.START)
            isValidDestination = true
        }
        return isValidDestination
    }

    private fun isDestinationValid(destination: Int): Boolean {
        return destination != navController.currentDestination?.id
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
