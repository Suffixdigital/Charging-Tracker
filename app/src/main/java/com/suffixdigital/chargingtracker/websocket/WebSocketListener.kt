package com.suffixdigital.chargingtracker.websocket

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

/**
 * Created by Kirtikant Patadiya on 2025-03-29.
 */

class WebSocketListener(private val onMessageReceived: (String) -> Unit) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("WebSocket Connected")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("Received Text Message: $text")
        onMessageReceived(text)  // Callback to update UI
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        println("Received Byte Message: ${bytes.utf8()}")
        onMessageReceived(bytes.utf8())  // Convert bytes to string
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("WebSocket Closing: Code $code, Reason: $reason")
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("WebSocket Failure: ${t.message}")
    }
}