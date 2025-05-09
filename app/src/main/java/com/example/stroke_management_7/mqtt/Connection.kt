package com.example.stroke_management_7.mqtt

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.stroke_management_7.databinding.ActivityMainBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException

const val TAG = "MQTT"

class Connection private constructor(
    private val clientId: String,
    private val host: String,
    private val context: Context,
    private val client: MqttAndroidClient,
    private val binding: ActivityMainBinding,
) {


    companion object {
        fun createConnection(
            clientId: String,
            host: String,
            context: Context,
            binding: ActivityMainBinding
        ): Connection {
            val uri = "tcp://$host:1883"
            val client = MqttAndroidClient(context, uri, clientId)
            return Connection(
                clientId,
                host,
                context,
                client,
                binding
            )
        }
    }


    fun getClient(): MqttAndroidClient {
        return client
    }

    @Throws(MqttException::class)
    fun connect(
        topicArray: Array<String>,
        qos: IntArray,
        topicLiveData: ArrayList<MutableLiveData<ArrayList<Entry>>>
    ) {
        if (this.getClient().isConnected) {
            Log.i(TAG, "Client is already connected!")
            return
        }
        try {
            val connOpts = MqttConnectOptions()
            connOpts.isAutomaticReconnect = true
            connOpts.isCleanSession = false
            connOpts.connectionTimeout = 80
            connOpts.keepAliveInterval = 200

            val callback = ActionListener(context, ActionListener.Action.CONNECT, this, binding)
            this.getClient().connect(connOpts, this, callback)
//            subscribe(topicArray,qos,topicLiveData)
        } catch (e: MqttException) {
            e.printStackTrace()
            Log.e(TAG, "Exception with connection!")
            throw MqttException(e)
        }
    }


    @Throws(MqttException::class)
    fun subscribe(
        topicArray: Array<String>,
        qos: IntArray,
        topicLiveData: ArrayList<MutableLiveData<ArrayList<Entry>>>
    ) {
        try {
            val callback =
                ActionListener(this.context, ActionListener.Action.SUBSCRIBE, this, binding)
            this.getClient()
                .setCallback(MqttCallbackHandler(context,topicArray, topicLiveData, binding))
            this.getClient().subscribe(topicArray, qos, context, callback)
        } catch (e: MqttException) {
            throw MqttException(e)

        }
    }

    @Throws(MqttException::class)
    fun disconnect() {
        val callback = ActionListener(
            this.context,
            ActionListener.Action.DISCONNECT,
            this,
            binding
        )
        this.getClient().disconnect(context,callback)
    }

    fun showChart(lineChart: LineChart, lineDataSet: LineDataSet) {
        val dataSet = ArrayList<ILineDataSet>()
        dataSet.add(lineDataSet)

        val legend = lineChart.legend
        customLegend(legend)

        val description = lineChart.description
        description.text = ""
        customDescription(description)

        val xAxis = lineChart.xAxis
        val yAxisLeft = lineChart.axisLeft
        val yAxisRight = lineChart.axisRight
        yAxisLeft.valueFormatter = MyLeftYAxisValueFormatter()
        xAxis.valueFormatter = MyAxisValueFormatter()
        yAxisRight.valueFormatter = MyYAxisValueFormatter()
//custom đồ thị
        val data = LineData(lineDataSet)
        data.setValueFormatter(MyValueFormatter())
        data.notifyDataChanged()
        lineChart.setDrawGridBackground(false)
        lineChart.setBorderColor(Color.BLACK)
        lineChart.setBorderWidth(8f)
        lineChart.data = data
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }
}

fun customLineDataSet(lineDataSet: LineDataSet) {
    lineDataSet.lineWidth = 2f
    lineDataSet.setDrawCircleHole(false)
    lineDataSet.circleRadius = 3f
    lineDataSet.valueTextSize = 0f
    lineDataSet.color = Color.rgb(232,188,5)
    lineDataSet.setCircleColor(Color.rgb(232,188,5))


}

fun customLegend(legend: Legend?) {
    legend?.isEnabled = true
    legend?.textColor = Color.BLACK
    legend?.textSize = 15f
    legend?.form = Legend.LegendForm.LINE
    legend?.formSize = 20f
    legend?.xEntrySpace = 10f
    legend?.formToTextSpace = 5f
}

fun customDescription(description: Description?) {
    description?.yOffset = 0f
    description?.textColor = Color.BLACK
    description?.textSize = 20f
}

class MyYAxisValueFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return ""
    }
}

class MyAxisValueFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        axis!!.setLabelCount(5, true)
        return if (value.toInt().toString().length == 5) '0' + value.toInt().toString()
        else value.toInt().toString()
    }
}

class MyValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return String.format("%.3f", value)
    }
}

class MyLeftYAxisValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return value.toInt().toString()
    }
}