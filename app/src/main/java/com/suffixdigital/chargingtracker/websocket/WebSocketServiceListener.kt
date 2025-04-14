package com.suffixdigital.chargingtracker.websocket

import android.content.Context
import android.os.Handler
import android.os.Looper.getMainLooper
import android.widget.Toast
import okhttp3.*
import okhttp3.WebSocketListener
import okio.ByteString

/**
 * Created by Kirtikant Patadiya on 2025-04-01.
 */
class WebSocketServiceListener(private val context: Context) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        showToast("WebSocket Connected")
        println("WebSocket Connected")
        webSocket.send("WebSocket Connected!")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("Received Text Message: $text")
        showToast("Received: $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        showToast("Received Binary Data")
        println("Received Byte Message: ${bytes.utf8()}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        showToast("WebSocket Closing: $reason")
        println("WebSocket Closing: Code $code, Reason: $reason")
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        showToast("WebSocket Failure: ${t.message}")
        println("WebSocket Failure: ${t.message}")
    }

    private fun showToast(message: String) {
        Handler(getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}