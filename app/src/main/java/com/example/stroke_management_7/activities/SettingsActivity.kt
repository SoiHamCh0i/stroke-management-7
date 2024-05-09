package com.example.stroke_management_7.activities

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.stroke_management_7.R
import com.example.stroke_management_7.databinding.ActivitySettingsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //thiết lập status bar và navigation bar của máy
        window.statusBarColor= ContextCompat.getColor(this@SettingsActivity,R.color.Red)

        //thiết lập bottom navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bot_nav_menu)
        bottomNavigationView.selectedItemId = R.id.bot_setting

        bottomNavigationView.setOnItemSelectedListener {item ->
            when(item.itemId){
                R.id.bot_setting -> true
                R.id.bot_person -> {
                    val animationBundle= ActivityOptions.makeCustomAnimation(this@SettingsActivity,R.anim.slide_left,R.anim.slide_right).toBundle()
                    startActivity(Intent(applicationContext, MainActivity::class.java),animationBundle)
                    finish()
                    true
                }
                R.id.bot_location -> {
                    val animationBundle= ActivityOptions.makeCustomAnimation(this@SettingsActivity,R.anim.slide_left,R.anim.slide_right).toBundle()
                    startActivity(Intent(applicationContext, LocationActivity::class.java),animationBundle)
                    finish()
                    true
                }
                else->false
            }
        }

    }
}