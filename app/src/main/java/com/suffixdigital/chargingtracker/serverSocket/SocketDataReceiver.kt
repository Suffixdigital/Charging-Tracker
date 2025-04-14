package com.suffixdigital.chargingtracker.serverSocket

import android.util.Log
import com.suffixdigital.chargingtracker.interfaces.SocketConnectionListener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

class SocketDataReceiver(
    private val socketConnectionListener: SocketConnectionListener?,
    private val socket: Socket?
) : Thread() {
    private var isConnected: Boolean = true
    private var bufferReader: BufferedReader? = null

    override fun run() {
        try {
            while (isConnected) {
                bufferReader = BufferedReader(InputStreamReader(socket?.getInputStream()))
                val message: String? = bufferReader?.readLine()
                if (message != null) {
                    socketConnectionListener?.onMessageReceive(message)
                    Log.d(
                        "ChargeNet",
                        "ChargeNet_Android: SocketDataReceiver. Received Message: $message  socketConnectionListener = ${socketConnectionListener == null}"
                    )

                } else {
                    isConnected = false
                    socketConnectionListener?.onDisconnect(socket?.inetAddress?.hostAddress)
                    close()
                }
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun close() {
        socket?.close()
        if (bufferReader != null) {
            bufferReader?.close()
        }
    }

}