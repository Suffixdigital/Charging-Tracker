package com.suffixdigital.chargingtracker.interfaces

import java.net.Socket

interface SocketConnectionListener {
    fun onConnect(socket: Socket?)
    fun onDisconnect(ipAddress: String?)
    fun onMessageReceive(message: String)
}