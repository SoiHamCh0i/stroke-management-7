package com.example.stroke_management_7.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.stroke_management_7.R
import com.example.stroke_management_7.databinding.ActivityMainBinding
import com.example.stroke_management_7.mqtt.Connection
import com.example.stroke_management_7.mqtt.customLineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        binding.scrollView.isVisible = false
        binding.btnSubscribe.isVisible = false
        binding.btnConnect.isEnabled = false

        //thiêt lập quan sát vẽ biểu đồ
        heartRateLiveData.observe(this, heartRateObserver)
        tempLiveData.observe(this, tempObserver)
        accXLiveData.observe(this, accXObserver)
        accYLiveData.observe(this, accYObserver)
        accZLiveData.observe(this, accZObserver)
        gyroXLiveData.observe(this, gyroXObserver)
        gyroYLiveData.observe(this, gyroYObserver)
        gyroZLiveData.observe(this, gyroZObserver)
        eulerXLiveData.observe(this, eulerXObserver)
        eulerYLiveData.observe(this, eulerYObserver)
        eulerZLiveData.observe(this, eulerZObserver)
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
                    connection =
                        Connection.createConnection(clientId, host, this@MainActivity, binding)
                    connection!!.connect(topicArray, qos, topicLiveData)
                }
            }
        })
        binding.btnDisconnect.setOnClickListener {
            connection!!.disconnect()
        }
        binding.btnSubscribe.setOnClickListener {
            connection!!.subscribe(topicArray, qos, topicLiveData)
        }


    }

    override fun onPause() {
        super.onPause()

        //thiêt lập quan sát vẽ biểu đồ
        heartRateLiveData.observe(this, heartRateObserver)
        tempLiveData.observe(this, tempObserver)
        accXLiveData.observe(this, accXObserver)
        accYLiveData.observe(this, accYObserver)
        accZLiveData.observe(this, accZObserver)
        gyroXLiveData.observe(this, gyroXObserver)
        gyroYLiveData.observe(this, gyroYObserver)
        gyroZLiveData.observe(this, gyroZObserver)
        eulerXLiveData.observe(this, eulerXObserver)
        eulerYLiveData.observe(this, eulerYObserver)
        eulerZLiveData.observe(this, eulerZObserver)
    }

    private val heartRateObserver = Observer<ArrayList<Entry>> { dataEntries ->
        val lineDataSet = LineDataSet(dataEntries, "Heart Rate")
        customLineDataSet(lineDataSet)
        connection?.showChart(binding.chartHeartRate, lineDataSet)
    }
    private val tempObserver = Observer<ArrayList<Entry>> { dataEntries ->
        val lineDataSet = LineDataSet(dataEntries, "Temperature")
        customLineDataSet(lineDataSet)
        connection?.showChart(binding.chartTemp, lineDataSet)
    }
    private val accXObserver = Observer<ArrayList<Entry>> { dataEntries ->
        val lineDataSet = LineDataSet(dataEntries, "AccelX")
        customLineDataSet(lineDataSet)
        connection?.showChart(binding.chartAccelX, lineDataSet)
    }
    private val accYObserver = Observer<ArrayList<Entry>> { dataEntries ->
        val lineDataSet = LineDataSet(dataEntries, "AccelY")
        customLineDataSet(lineDataSet)
        connection?.showChart(binding.chartAccelY, lineDataSet)
    }
    private val accZObserver = Observer<ArrayList<Entry>> { dataEntries ->
        val lineDataSet = LineDataSet(dataEntries, "AccelZ")
        customLineDataSet(lineDataSet)
        connection?.showChart(binding.chartAccelZ, lineDataSet)
    }
    private val gyroXObserver = Observer<ArrayList<Entry>> { dataEntries ->
        val lineDataSet = LineDataSet(dataEntries, "GyroX")
        customLineDataSet(lineDataSet)
        connection?.showChart(binding.chartGyroX, lineDataSet)
    }
    private val gyroYObserver = Observer<ArrayList<Entry>> { dataEntries ->
        val lineDataSet = LineDataSet(dataEntries, "GyroY")
        customLineDataSet(lineDataSet)
        connection?.showChart(binding.chartGyroY, lineDataSet)
    }
    private val gyroZObserver = Observer<ArrayList<Entry>> { dataEntries ->
        val lineDataSet = LineDataSet(dataEntries, "GyroZ")
        customLineDataSet(lineDataSet)
        connection?.showChart(binding.chartGyroZ, lineDataSet)
    }
    private val eulerXObserver = Observer<ArrayList<Entry>> { dataEntries ->
        val lineDataSet = LineDataSet(dataEntries, "EulerX")
        customLineDataSet(lineDataSet)
        connection?.showChart(binding.chartEulerX, lineDataSet)
    }
    private val eulerYObserver = Observer<ArrayList<Entry>> { dataEntries ->
        val lineDataSet = LineDataSet(dataEntries, "EulerY")
        customLineDataSet(lineDataSet)
        connection?.showChart(binding.chartEulerY, lineDataSet)
    }
    private val eulerZObserver = Observer<ArrayList<Entry>> { dataEntries ->
        val lineDataSet = LineDataSet(dataEntries, "EulerZ")
        customLineDataSet(lineDataSet)
        connection?.showChart(binding.chartEulerZ, lineDataSet)
    }

}