package com.suffixdigital.chargingtracker.serverSocket

import android.content.Context
import android.util.Log
import com.suffixdigital.chargingtracker.interfaces.SocketConnectionListener
import com.suffixdigital.chargingtracker.utils.isSocketServerStarted
import com.suffixdigital.chargingtracker.utils.tfsPort
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class SocketServer {
    private lateinit var serverReceiver: SocketDataReceiver
    var socketConnectionListener: SocketConnectionListener? = null
    var context: Context? = null

    fun getStartServer() {
        /* val socketServerThread = Thread(SocketServerThread())
         socketServerThread.start()*/
    }

    fun setCommunicationListener(
        context: Context,
        socketConnectionListener: SocketConnectionListener?
    ) {
        Log.d("ChargeNet", "ChargeNet_Android: SocketServer. setCommunicationListener() is called")
        this.socketConnectionListener = socketConnectionListener
        this.context = context
    }

    inner class SocketServerThread : Thread() {
        override fun run() {
            try {
                val serverSocket = ServerSocket(tfsPort)
                while (true) {
                    val socket: Socket = serverSocket.accept()
                    isSocketServerStarted = true
                    if (socketConnectionListener != null) {
                        socketConnectionListener?.onConnect(socket)
                        Log.d(
                            "ChargeNet",
                            "ChargeNet_Android: SocketServer. setCommunicationListener onConnect called"
                        )
                    }
                    serverReceiver = SocketDataReceiver(socketConnectionListener, socket)
                    try {
                        serverReceiver.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}