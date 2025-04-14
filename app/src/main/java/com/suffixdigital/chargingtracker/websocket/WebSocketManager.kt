package com.suffixdigital.chargingtracker.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

/**
 * Created by Kirtikant Patadiya on 2025-03-29.
 */
class WebSocketManager(private val onMessageReceived: (String) -> Unit) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connectWebSocket(url: String) {
        val request = Request.Builder().url(url).build()
        val listener = WebSocketListener(onMessageReceived)
        webSocket = client.newWebSocket(request, listener)
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun closeWebSocket() {
        webSocket?.close(1000, "Goodbye!")
    }
}