package com.suffixdigital.chargingtracker.serverSocket

/**
 * Created by Kirtikant Patadiya on 2025-02-22.
 */
import android.app.*
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.suffixdigital.chargingtracker.R
import com.suffixdigital.chargingtracker.utils.tfsPort
import java.io.*
import java.net.ServerSocket
import java.net.Socket

class ServerService : Service() {

    private var serverSocket: ServerSocket? = null
    private var socket: Socket? = null
    private var input: BufferedReader? = null
    private var output: PrintWriter? = null
    private val handler = Handler()

    override fun onCreate() {
        super.onCreate()
        startForegroundService() // Start foreground service
        Thread { startServer() }.start() // Run server in background
    }

    private fun startServer() {
        try {
            serverSocket = ServerSocket(tfsPort)
            showToast("Server started, waiting for connections...")

            while (true) { // Infinite loop to keep listening for clients
                socket = serverSocket!!.accept() // Wait for client connection
                showToast("Client connected!")

                input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                output = PrintWriter(socket!!.getOutputStream(), true)

                var message: String?
                while (input!!.readLine().also { message = it } != null) {
                    showToast("Client: $message")
                    output!!.println("Server received: $message")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showToast(message: String) {
        handler.post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startForegroundService() {
        val channelId = "ServerChannel"
        val manager = getSystemService(NotificationManager::class.java)

        val channel =
            NotificationChannel(channelId, "Server Service", NotificationManager.IMPORTANCE_LOW)
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Server Running")
            .setContentText("Listening for connections...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Restart service if it's killed
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            serverSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        showToast("Server stopped")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
