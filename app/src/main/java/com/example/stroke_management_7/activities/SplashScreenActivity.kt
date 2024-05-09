package com.example.stroke_management_7.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.stroke_management_7.R

const val SPLASH_DELAY_MS = 2500L

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //thiết lập status bar và navigation bar của máy
        window.statusBarColor= ContextCompat.getColor(this@SplashScreenActivity,R.color.Red)

        // Add any additional logic or animations here.
        // Once ready, navigate to your main activity or next screen.
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_DELAY_MS)
    }
}