package com.suffixdigital.chargingtracker.websocket

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

/**
 * Created by Kirtikant Patadiya on 2025-04-01.
 */
class WebSocketService : Service() {

    private lateinit var client: OkHttpClient


    companion object {
        const val ACTION_SEND_MESSAGE = "com.suffixdigital.websocket.SEND_MESSAGE"
        const val EXTRA_MESSAGE = "message"
        var webSocket: WebSocket? = null
    }

    override fun onCreate() {
        super.onCreate()
        //startForegroundService() // Start foreground service
        client = OkHttpClient()
        connectWebSocket()
    }


    private fun connectWebSocket() {
        val request = Request.Builder().url("ws://162.240.106.76:4044").build()
        val listener = WebSocketServiceListener(this)
        webSocket = client.newWebSocket(request, listener)
    }


    /*private fun startForegroundService() {
        val CHANNEL_ID = "WebSocketServiceChannel"
        val manager = getSystemService(NotificationManager::class.java)

        val channel = NotificationChannel(CHANNEL_ID, "Server Service", NotificationManager.IMPORTANCE_LOW)
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WebSocket Running")
            .setContentText("Listening for messages...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1, notification)
    }*/

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Restart service if it's killed
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket?.close(1000, "Service Stopped")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}