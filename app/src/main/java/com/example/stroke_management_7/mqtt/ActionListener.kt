package com.example.stroke_management_7.mqtt

import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.stroke_management_7.databinding.ActivityMainBinding
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken


class ActionListener(
    private val context: Context,
    private val action: Action,
    private val connection: Connection,
    private val binding: ActivityMainBinding
) : IMqttActionListener {

    private val handler = android.os.Handler(Looper.getMainLooper())

    enum class Action {
        CONNECT,
        DISCONNECT,
        SUBSCRIBE,
        UNSUBSCRIBE,
        PUBLISH
    }

    override fun onSuccess(asyncActionToken: IMqttToken?) {
        when (action) {
            Action.CONNECT -> connect()
            Action.DISCONNECT -> disconnect()
            Action.SUBSCRIBE -> subscribe()
            Action.PUBLISH -> publish()
            Action.UNSUBSCRIBE -> unsubscribe()
        }
    }

    private fun unsubscribe() {
        Log.w(TAG, "Unsubscribed from action listener")
        handler.post {

            Toast.makeText(context, "Unsubscribe", Toast.LENGTH_SHORT).show()
        }
    }

    private fun subscribe() {
        Log.w(TAG, "Subscribed From Action Listener!")
        handler.post {
            Toast.makeText(context, "Subscribed!", Toast.LENGTH_SHORT).show()
        }
        binding.scrollView.isVisible=true
        binding.btnSubscribe.isVisible=false
        binding.btnDisconnect.isVisible=true
    }

    private fun publish() {
        Log.w(TAG, "Published!")
    }

    private fun disconnect() {
        binding.btnConnect.isVisible=true
        binding.btnDisconnect.isVisible=false
        binding.edtIpAdress.text=null
        binding.edtIpAdress.isEnabled=true
        binding.scrollView.isVisible=false
        Log.w(TAG, "Disconnected!")
        handler.post {
            Toast.makeText(context, "Disconnected!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun connect() {
        val disconnectedBufferOptions = DisconnectedBufferOptions()
        disconnectedBufferOptions.isBufferEnabled = true
        disconnectedBufferOptions.bufferSize = 200
        disconnectedBufferOptions.isPersistBuffer = false
        disconnectedBufferOptions.isDeleteOldestMessages = false
        connection.getClient().setBufferOpts(disconnectedBufferOptions)

        binding.edtIpAdress.isEnabled=false
        binding.btnConnect.isVisible = false
        binding.btnSubscribe.isVisible=true
        handler.post {
            Toast.makeText(context, "Connected!", Toast.LENGTH_SHORT).show()
        }
        Log.w(TAG, "Connected From Action Listener!")
    }

    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable) {
        when (action) {
            Action.CONNECT -> connect(exception)
            Action.DISCONNECT -> disconnect(exception)
            Action.SUBSCRIBE -> subscribe(exception)
            Action.PUBLISH -> publish(exception)
            Action.UNSUBSCRIBE -> unsubscribe(exception)
        }
    }

    private fun unsubscribe(exception: Throwable) {
        Log.w(TAG, "Unsubscribe failure!")
        exception.printStackTrace()
    }

    private fun connect(exception: Throwable) {
        binding.edtIpAdress.text=null
        handler.post{
            Toast.makeText(
                context,
                "Unable to connect to server, please enter another Ip's address",
                Toast.LENGTH_SHORT
            ).show()
        }
        Log.w(TAG, "Connect Failure!")
        exception.printStackTrace()
    }

    private fun disconnect(exception: Throwable) {
        Log.w(TAG, "Disconnect Failure")
        exception.printStackTrace()
    }

    private fun publish(exception: Throwable) {
        Log.w(TAG, "Publish Failure")
        exception.printStackTrace()
    }

    private fun subscribe(exception: Throwable) {
        binding.btnConnect.isVisible=true
        binding.btnSubscribe.isVisible=false
        binding.edtIpAdress.text=null
        binding.edtIpAdress.isEnabled=true
        handler.post{
            Toast.makeText(context, "Wrong IP's address, please enter another!", Toast.LENGTH_SHORT).show()
        }
        Log.w(TAG, "Subscribe Failure")
        exception.printStackTrace()
    }

}