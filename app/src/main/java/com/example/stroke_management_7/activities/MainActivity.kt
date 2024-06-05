package com.example.stroke_management_7.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.stroke_management_7.R
import com.example.stroke_management_7.databinding.ActivityMainBinding
import com.example.stroke_management_7.mqtt.Connection
import com.example.stroke_management_7.mqtt.customLineDataSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

const val clientId = "AndroidMqttClient"
val topicArray = arrayOf(
    "heart",
    "tempC",
    "accX",
    "accY",
    "accZ",
    "gyroX",
    "gyroY",
    "gyroZ",
    "eulerX",
    "eulerY",
    "eulerZ",
    "count"
)
val qos = IntArray(12) { 1 }

private val heartRateLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
private val tempLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
private val accXLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
private val accYLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
private val accZLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
private val gyroXLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
private val gyroYLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
private val gyroZLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
private val eulerXLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
private val eulerYLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
private val eulerZLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData()
private val countLiveData: MutableLiveData<ArrayList<Entry>> = MutableLiveData()

val topicLiveData = arrayListOf(
    heartRateLiveData,
    tempLiveData,
    accXLiveData,
    accYLiveData,
    accZLiveData,
    gyroXLiveData,
    gyroYLiveData,
    gyroZLiveData,
    eulerXLiveData,
    eulerYLiveData,
    eulerZLiveData,
    countLiveData
)

class MainActivity : AppCompatActivity() {

    private var connection: Connection? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //thiết lập status bar và navigation bar của máy
        window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.Red)

        //thiết lập bottom navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bot_nav_menu)
        bottomNavigationView.selectedItemId = R.id.bot_person

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bot_person -> true
                R.id.bot_location -> {
                    val animationBundle = ActivityOptions.makeCustomAnimation(
                        this@MainActivity, R.anim.slide_in_from_left, R.anim.slide_in_from_right
                    ).toBundle()
                    startActivity(
                        Intent(applicationContext, LocationActivity::class.java),
                        animationBundle
                    )
                    finish()
                    true
                }

                R.id.bot_setting -> {
                    val animationBundle = ActivityOptions.makeCustomAnimation(
                        this@MainActivity, R.anim.slide_in_from_left, R.anim.slide_in_from_right
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
        //thiết lập connection
        binding.btnDisconnect.isVisible = false
        binding.scrollView.isVisible = true
        binding.btnSubscribe.isVisible = false
        binding.btnConnect.isEnabled = false


        binding.edtIpAdress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.btnConnect.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnConnect.isEnabled = true
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.edtIpAdress.text.isNullOrEmpty()) {
                    binding.btnConnect.isEnabled = false
                }
                binding.btnConnect.setOnClickListener {
                    val host = binding.edtIpAdress.text.toString().trim()
                    lifecycleScope.launch {
                        connection =
                            Connection.createConnection(
                                clientId,
                                host,
                                this@MainActivity,
                                binding
                            )
                        connection?.connect()
                    }
                }
            }
        })
        binding.btnDisconnect.setOnClickListener {
            lifecycleScope.launch {
                connection?.disconnect()
            }
        }
        binding.btnSubscribe.setOnClickListener {
            lifecycleScope.launch {
                connection?.subscribe(topicArray, qos, topicLiveData)
            }
        }

        observeLiveData()
    }

    private fun observeLiveData() {
        heartRateLiveData.observe(this) { updateChart(binding.chartHeartRate, it, "Heart Rate") }
        tempLiveData.observe(this) { updateChart(binding.chartTemp, it, "Temperature") }
        accXLiveData.observe(this) { updateChart(binding.chartAccelX, it, "Acc X") }
        accYLiveData.observe(this) { updateChart(binding.chartAccelY, it, "Acc Y") }
        accZLiveData.observe(this) { updateChart(binding.chartAccelZ, it, "Acc Z") }
        gyroXLiveData.observe(this) { updateChart(binding.chartGyroX, it, "Gyro X") }
        gyroYLiveData.observe(this) { updateChart(binding.chartGyroY, it, "Gyro Y") }
        gyroZLiveData.observe(this) { updateChart(binding.chartGyroZ, it, "Gyro Z") }
        eulerXLiveData.observe(this) { updateChart(binding.chartEulerX, it, "Euler X") }
        eulerYLiveData.observe(this) { updateChart(binding.chartEulerY, it, "Euler Y") }
        eulerZLiveData.observe(this) { updateChart(binding.chartEulerZ, it, "Euler Z") }

    }


    private fun updateChart(lineChart: LineChart, entries: ArrayList<Entry>, label: String) {
        val lineDataSet = customLineDataSet(entries, label)
        connection?.showChart(lineChart, lineDataSet)
    }

}