package com.suffixdigital.chargingtracker.websocket

/**
 * Created by Kirtikant Patadiya on 2025-03-29.
 */
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WebSocketViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<String>>(emptyList())
    val messages: LiveData<List<String>> get() = _messages

    private val webSocketManager = WebSocketManager { message ->
        _messages.postValue(_messages.value?.plus(message))
    }

    fun connect() {
        webSocketManager.connectWebSocket("ws://162.240.106.76:4044")
    }

    fun sendMessage(message: String) {
        webSocketManager.sendMessage(message)
    }

    fun disconnect() {
        webSocketManager.closeWebSocket()
    }
}