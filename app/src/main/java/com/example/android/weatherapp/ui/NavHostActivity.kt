package com.example.android.weatherapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.android.weatherapp.R
import com.google.android.material.navigation.NavigationView

class NavHostActivity : AppCompatActivity() {

    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_host)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            R.string.open_navigation_drawer,
            R.string.close_navigation_drawer
        )

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_settings -> {
                    Toast.makeText(applicationContext, "Settings Selected!", Toast.LENGTH_SHORT)
                        .show()
                    true
                }
                else -> false
            }
        }

        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }


    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
