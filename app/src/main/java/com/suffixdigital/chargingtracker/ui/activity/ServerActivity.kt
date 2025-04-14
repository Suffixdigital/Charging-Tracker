package com.suffixdigital.chargingtracker.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.suffixdigital.chargingtracker.R
import java.net.InetAddress
import javax.websocket.CloseReason
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.ServerEndpoint

@ServerEndpoint("/ws")
class WebSocketServer {

    @OnOpen
    fun onOpen(session: Session) {
        println("Client connected: ${session.id}")
        session.asyncRemote.sendText("Welcome to the secure WebSocket server!")
    }

    @OnMessage
    fun onMessage(message: String, session: Session) {
        println("Received message: $message")
        session.asyncRemote.sendText("Echo: $message")
    }

    @OnClose
    fun onClose(session: Session, reason: CloseReason) {
        println("Connection closed: ${session.id}, Reason: ${reason.reasonPhrase}")
    }

    @OnError
    fun onError(session: Session, throwable: Throwable) {
        println("Error: ${throwable.message}")
    }
}

class ServerActivity : AppCompatActivity() {
    //private lateinit var webSocketServer: SecureWebSocketServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)
        // Start the WebSocket server
        // Start the secure WebSocket server
//        webSocketServer = SecureWebSocketServer(8443, assets.open("keystore.p12"), "password")
//        webSocketServer.start()
        Log.d(
            "WebSocketServer",
            "Server started on wss://${InetAddress.getLocalHost().hostAddress}:8443"
        )
    }


    /*  private suspend fun startSecureWebSocketServer() = withContext(Dispatchers.IO) {
          try {
              val sslContext = createSSLContext("keystore.p12", "password")
              val server = Server("0.0.0.0", 8443, "/websockets", null, WebSocketServer::class.java)

              server.properties["org.glassfish.tyrus.server.SSL_CONTEXT"] = sslContext
              println("Starting secure WebSocket server on wss://0.0.0.0:8443/ws")
              server.start()
          } catch (e: Exception) {
              println("Error starting secure server: ${e.message}")
          }
      }


      private fun createSSLContext(keystorePath: String, password: String): SSLContext {
          val keyStore = KeyStore.getInstance("PKCS12")
          keyStore.load(FileInputStream(keystorePath), password.toCharArray())

          val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
          keyManagerFactory.init(keyStore, password.toCharArray())

          val sslContext = SSLContext.getInstance("TLS")
          sslContext.init(keyManagerFactory.keyManagers, null, null)
          return sslContext
      }*/
}