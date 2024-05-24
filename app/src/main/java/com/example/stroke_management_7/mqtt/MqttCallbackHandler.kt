package com.example.stroke_management_7.mqtt

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.stroke_management_7.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.time.LocalTime
import java.time.format.DateTimeFormatter

const val DELAY_TIME = 1000L

class MqttCallbackHandler(
    private val context: Context,
    private val topicArray: Array<String>,
    private val topicLiveData: ArrayList<MutableLiveData<ArrayList<Entry>>>,
    private val binding: ActivityMainBinding
) : MqttCallback {
    private val heartRateEntries = ArrayList<Entry>()
    private val tempEntries = ArrayList<Entry>()
    private val accXEntries = ArrayList<Entry>()
    private val accYEntries = ArrayList<Entry>()
    private val accZEntries = ArrayList<Entry>()
    private val gyroXEntries = ArrayList<Entry>()
    private val gyroYEntries = ArrayList<Entry>()
    private val gyroZEntries = ArrayList<Entry>()
    private val eulerXEntries = ArrayList<Entry>()
    private val eulerYEntries = ArrayList<Entry>()
    private val eulerZEntries = ArrayList<Entry>()
    private val countEntries = ArrayList<Entry>()

    override fun connectionLost(cause: Throwable?) {
        if (cause != null) {
            Log.w(TAG, "Connection Lost: " + cause.message)
            cause.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val timestamp =
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toFloatOrNull()
                        ?: return@launch
                val payload =
                    message?.payload?.toString(Charsets.UTF_8)?.toFloatOrNull() ?: return@launch
                when (topic) {
                    topicArray[0] -> {
                        if (heartRateEntries.size <= 1 || (timestamp - heartRateEntries.last().x >= 1)) {
                            if (heartRateEntries.size == 15) {
                                heartRateEntries.removeAt(0)
                            }
                            heartRateEntries.add(Entry(timestamp, payload))
                            withContext(Dispatchers.Main) {
                                topicLiveData[0].postValue(heartRateEntries)
                                binding.tvCurrentHeartRate.text = "$payload Hz"
                            }
                        }
                    }

                    topicArray[1] -> {
                        if (tempEntries.size <= 1 || (timestamp - tempEntries.last().x >= 1)) {
                            if (tempEntries.size == 15) {
                                tempEntries.removeAt(0)
                            }
                            tempEntries.add(Entry(timestamp, payload))
                            withContext(Dispatchers.Main) {
                                topicLiveData[1].postValue(tempEntries)
                                binding.tvCurrentTemp.text = "$payload oC"
                            }
                        }
                    }

                    topicArray[2] -> {
                        if (accXEntries.size <= 1 || (timestamp - accXEntries.last().x >= 1)) {
                            if (accXEntries.size == 15) {
                                accXEntries.removeAt(0)
                            }
                            accXEntries.add(Entry(timestamp, payload))
                            withContext(Dispatchers.Main) {
                                topicLiveData[2].postValue(accXEntries)
                            }
                        }
                    }

                    topicArray[3] -> {
                        if (accYEntries.size <= 1 || (timestamp - accYEntries.last().x >= 1)) {
                            if (accYEntries.size == 15) {
                                accYEntries.removeAt(0)
                            }
                            accYEntries.add(Entry(timestamp, payload))
                            withContext(Dispatchers.Main) {
                                topicLiveData[3].postValue(accYEntries)
                            }
                        }
                    }

                    topicArray[4] -> {
                        if (accZEntries.size <= 1 || (timestamp - accZEntries.last().x >= 1)) {
                            if (accZEntries.size == 15) {
                                accZEntries.removeAt(0)
                            }
                            accZEntries.add(Entry(timestamp, payload))
                            withContext(Dispatchers.Main) {
                                topicLiveData[4].postValue(accZEntries)
                            }
                        }
                    }

                    topicArray[5] -> {
                        if (gyroXEntries.size <= 1 || (timestamp - gyroXEntries.last().x >= 1)) {
                            if (gyroXEntries.size == 15) {
                                gyroXEntries.removeAt(0)
                            }
                            gyroXEntries.add(Entry(timestamp, payload))
                            withContext(Dispatchers.Main) {
                                topicLiveData[5].postValue(gyroXEntries)
                            }
                        }
                    }

                    topicArray[6] -> {
                        if (gyroYEntries.size <= 1 || (timestamp - gyroYEntries.last().x >= 1)) {
                            if (gyroYEntries.size == 15) {
                                gyroYEntries.removeAt(0)
                            }
                            gyroYEntries.add(Entry(timestamp, payload))
                            withContext(Dispatchers.Main) {
                                topicLiveData[6].postValue(gyroYEntries)
                            }
                        }
                    }

                    topicArray[7] -> {
                        if (gyroZEntries.size <= 1 || (timestamp - gyroZEntries.last().x >= 1)) {
                            if (gyroZEntries.size == 15) {
                                gyroZEntries.removeAt(0)
                            }
                            gyroZEntries.add(Entry(timestamp, payload))
                            withContext(Dispatchers.Main) {
                                topicLiveData[7].postValue(gyroZEntries)
                            }
                        }
                    }

                    topicArray[8] -> {
                        if (eulerXEntries.size <= 1 || (timestamp - eulerXEntries.last().x >= 1)) {
                            if (eulerXEntries.size == 15) {
                                eulerXEntries.removeAt(0)
                            }
                            eulerXEntries.add(Entry(timestamp, payload))
                            withContext(Dispatchers.Main) {
                                topicLiveData[8].postValue(eulerXEntries)
                            }
                        }
                    }

                    topicArray[9] -> {
                        if (eulerYEntries.size <= 1 || (timestamp - eulerYEntries.last().x >= 1)) {
                            if (eulerYEntries.size == 15) {
                                eulerYEntries.removeAt(0)
                            }
                            eulerYEntries.add(Entry(timestamp, payload))
                            withContext(Dispatchers.Main) {
                                topicLiveData[9].postValue(eulerYEntries)
                            }
                        }
                    }

                    topicArray[10] -> {
                        if (eulerZEntries.size <= 1 || (timestamp - eulerZEntries.last().x >= 1)) {
                            if (eulerZEntries.size == 5) {
                                eulerZEntries.removeAt(0)
                            }
                            eulerZEntries.add(Entry(timestamp, payload))
                            withContext(Dispatchers.Main) {
                                topicLiveData[10].postValue(eulerZEntries)
                            }
                        }
                    }

                    else -> {
                        //Do nothing
                    }
                }

                Log.i(TAG, "Topic: $topic, Timestamp: $timestamp, Payload: $payload")

            } catch (e: MqttException) {
                e.printStackTrace()
                Log.e(TAG, "Exception for message arrived")
            }
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        //Do nothing
    }
}