package com.suffixdigital.chargingtracker.websocket

import okhttp3.WebSocket

/**
 * Created by Kirtikant Patadiya on 2024-12-23.
 */


class SecureWebSocketClient(
    private val context: android.content.Context,
    private val serverUrl: String
) {

    private var webSocket: WebSocket? = null

    /*fun connect() {
        val sslContext = createSslContext()
        val trustManager = getTrustManager()

        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .build()

        val request = Request.Builder()
            .url(serverUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("Connected to server!")
                webSocket.send("Hello from Android!")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                println("Received message: $text")
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                println("Received binary message: ${bytes.hex()}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("Connection closing: $reason")
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                println("Connection closed: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("Connection failed: ${t.message}")
            }
        })
    }

    private fun createSslContext(): SSLContext {
        val certificateInputStream: InputStream = context.resources.openRawResource(R.raw.server_cert) // Your CA certificate
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        //keyStore.load(context.assets.open("keystore.p12"), "password".toCharArray()) // Initialize the keystore
        keyStore.load(null)

        keyStore.setCertificateEntry("server", CertificateFactory.getInstance("X.509").generateCertificate(certificateInputStream))

        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagerFactory.trustManagers, null)
        return sslContext
    }

    private fun getTrustManager(): X509TrustManager {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null)
        return trustManagerFactory.trustManagers.first { it is X509TrustManager } as X509TrustManager
    }*/
}
