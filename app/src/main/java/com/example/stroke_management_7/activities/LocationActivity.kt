package com.example.stroke_management_7.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.stroke_management_7.R
import com.example.stroke_management_7.databinding.ActivityLocationBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        //thiết lập status bar và navigation bar của máy
        window.statusBarColor = ContextCompat.getColor(this@LocationActivity, R.color.Red)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this@LocationActivity)

        //thiết lập bottom navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bot_nav_menu)
        bottomNavigationView.selectedItemId = R.id.bot_location

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bot_location -> true
                R.id.bot_person -> {
                    val animationBundle = ActivityOptions.makeCustomAnimation(
                        this@LocationActivity,
                        R.anim.slide_left,
                        R.anim.slide_right
                    ).toBundle()
                    startActivity(
                        Intent(applicationContext, MainActivity::class.java),
                        animationBundle
                    )
                    finish()
                    true
                }

                R.id.bot_setting -> {
                    val animationBundle = ActivityOptions.makeCustomAnimation(
                        this@LocationActivity,
                        R.anim.slide_in_from_left,
                        R.anim.slide_in_from_right
                    ).toBundle()
                    startActivity(
                        Intent(applicationContext, SettingsActivity::class.java),
                        animationBundle
                    )
                    finish()
                    true
                }

                else -> false
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}