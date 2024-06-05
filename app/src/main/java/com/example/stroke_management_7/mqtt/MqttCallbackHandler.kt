package com.example.stroke_management_7.mqtt

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.example.stroke_management_7.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.time.LocalTime
import java.time.format.DateTimeFormatter

const val DELAY_TIME = 1000L
const val SKIP_TIME = 2F

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

    override fun connectionLost(cause: Throwable?) {
        if (cause != null) {
            Log.w(TAG, "Connection Lost: " + cause.message)
            cause.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val timestamp =
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toFloatOrNull()
                        ?: return@launch
                val payload =
                    message?.payload?.toString(Charsets.UTF_8)?.toFloatOrNull() ?: return@launch
                withContext(Dispatchers.Main){
                when (topic) {
                    topicArray[0] -> updateEntries(heartRateEntries,  timestamp, payload, 0, binding.tvCurrentHeartRate, "$payload Hz")
                    topicArray[1] -> updateEntries(tempEntries, timestamp, payload, 1, binding.tvCurrentTemp, "$payload °C")
                    topicArray[2] -> updateEntries(accXEntries,  timestamp, payload, 2)
                    topicArray[3] -> updateEntries(accYEntries,  timestamp, payload, 3)
                    topicArray[4] -> updateEntries(accZEntries,  timestamp, payload, 4)
                    topicArray[5] -> updateEntries(gyroXEntries,  timestamp, payload, 5)
                    topicArray[6] -> updateEntries(gyroYEntries,  timestamp, payload, 6)
                    topicArray[7] -> updateEntries(gyroZEntries, timestamp, payload, 7)
                    topicArray[8] -> updateEntries(eulerXEntries,  timestamp, payload, 8)
                    topicArray[9] -> updateEntries(eulerYEntries,  timestamp, payload, 9)
                    topicArray[10] -> updateEntries(eulerZEntries,  timestamp, payload, 10)
                    else -> Log.i(TAG, "Unhandled topic: $topic")
                }
                }


            } catch (e: MqttException) {
                e.printStackTrace()
                Log.e(TAG, "Exception for message arrived")
            }
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        //Do nothing
    }
    private fun updateEntries(entries: ArrayList<Entry>, timestamp: Float, payload: Float, liveDataIndex: Int, textView: TextView? = null, text: String? = null) {
        if (entries.size<=1||(timestamp-entries.last().x >= SKIP_TIME)) {
            if (entries.size == 15) entries.removeAt(0)
            entries.add(Entry(timestamp, payload))
            topicLiveData[liveDataIndex].postValue(entries)
            textView?.text = text
            Log.i(TAG, "Topic: ${{ topicArray[liveDataIndex] }}, Timestamp: $timestamp, Payload: $payload")

        }
    }
}
