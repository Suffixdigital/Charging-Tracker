package com.suffixdigital.chargingtracker.ui.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.suffixdigital.chargingtracker.R
import com.suffixdigital.chargingtracker.websocket.WebSocketViewModel
import okhttp3.*
import androidx.activity.viewModels
import androidx.lifecycle.Observer


class ClientActivity : AppCompatActivity() {
    private lateinit var webSocket: WebSocket
    private val client = OkHttpClient()
    private val viewModel: WebSocketViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
        val textView = findViewById<TextView>(R.id.messagesTextView)
        val connectButton = findViewById<Button>(R.id.connectButton)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val disconnectButton = findViewById<Button>(R.id.disconnectButton)
        val messageInput = findViewById<EditText>(R.id.messageInput)

        viewModel.messages.observe(this, Observer { messages ->
            textView.text = messages.joinToString("\n")
        })

        connectButton.setOnClickListener { viewModel.connect() }
        sendButton.setOnClickListener {
            viewModel.sendMessage(messageInput.text.toString())
            messageInput.text.clear()
        }
        disconnectButton.setOnClickListener { viewModel.disconnect() }
    }


}