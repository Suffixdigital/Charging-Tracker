package com.suffixdigital.chargingtracker.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.suffixdigital.chargingtracker.R
import com.suffixdigital.chargingtracker.custombottomnavigation.Model
import com.suffixdigital.chargingtracker.databinding.ActivityChargingMainScreenBinding
import com.suffixdigital.chargingtracker.serverSocket.SocketServer
import com.suffixdigital.chargingtracker.utils.isSocketServerStarted
import com.suffixdigital.chargingtracker.utils.receiverIPAddress
import com.suffixdigital.chargingtracker.utils.socket
import com.suffixdigital.chargingtracker.utils.tfsPort
import com.suffixdigital.chargingtracker.websocket.WebSocketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket

class ChargingMainScreen : AppCompatActivity() {

    private lateinit var binding: ActivityChargingMainScreenBinding

    /***********************************************************************************************************************
     * Inject [socketServer] variable. The [SocketServer] class initialize at once only when TF Driver app started using Dagger Dependencies Injection.
     * This variable can use in whole application.
     **********************************************************************************************************************/
    var socketServer: SocketServer? = null

    companion object {
        private const val ID_DASHBOARD = 0
        private const val ID_FIND_CHARGING_STATION = 1
        private const val ID_PROFILE = 2

        //        private const val ID_SERVER_SOCKET = 3
        private const val KEY_ACTIVE_INDEX = "activeIndex"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_charging_main_screen)
// Uncomment to use the bottom navigation in traditional way, such as without using Navigation Component
        setBottomNavigationWithNavController(savedInstanceState)
        socketServer = SocketServer()
        if (!isSocketServerStarted) {
            socketServer?.getStartServer()
        }
    }


    private fun setBottomNavigationWithNavController(savedInstanceState: Bundle?) {

        // If you don't pass activeIndex then by default it will take 0 position
        val activeIndex = savedInstanceState?.getInt("activeIndex") ?: ID_PROFILE

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //setupActionBarWithNavController(navController, appBarConfiguration)
        val menuItems = arrayOf(
            Model(
                icon = R.drawable.group,
                destinationId = R.id.navigation_dashboard,
                id = ID_DASHBOARD,
                text = R.string.title_map_view
            ),
            Model(
                R.drawable.charging_station_list,
                R.id.navigation_find_station,
                id = ID_FIND_CHARGING_STATION,
                text = R.string.title_find_chargers
            ),
            Model(
                R.drawable.profile,
                R.id.navigation_profile,
                id = ID_PROFILE,
                text = R.string.title_driver_profile
            )/*, Model(
                R.drawable.socket_server_icon,
                R.id.navigation_server_socket,
                id = ID_SERVER_SOCKET,
            )*/
        )

        binding.bottomNavigation.apply {
            // If you don't pass activeIndex then by default it will take 0 position
            setMenuItems(menuItems, activeIndex)
            setupWithNavController(navController = navController, exitOnBack = false)

            // manually set the active item, so from which you can control which position item should be active when it is initialized.
            // onMenuItemClick(4)

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_ACTIVE_INDEX, binding.bottomNavigation.getSelectedIndex())
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        Log.e(
            "ChargingApp",
            "ChargingApp: ChargingMainScreen.  onResume called. isSocketServerStarted:  $isSocketServerStarted"
        )
        /*socketServer = SocketServer()
        socketServer?.setCommunicationListener(this@ChargingMainScreen,
            object : SocketConnectionListener {
                override fun onConnect(socket: Socket?) {
                }

                override fun onDisconnect(ipAddress: String?) {
                }

                override fun onMessageReceive(message: String) {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            Log.e(
                                "ChargingApp",
                                "ChargingApp: ChargingMainScreen. Received Message: $message"
                            )
                            Toast.makeText(
                                this@ChargingMainScreen,
                                "Received: $message",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }

            })*/
        //socketServer?.getStartServer()
        Log.e(
            "ChargingApp",
            "ChargingApp: ChargingEndSessionScreen.  onResume called. isSocketServerStarted:  $isSocketServerStarted"
        )
        //sendingMessagesBytes("Hello Socket!!!")
        sendMessageToService("Hello Socket!!!")
    }

    private fun sendingMessagesBytes(message: String) {

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    Log.d(
                        "ChargingApp",
                        "ChargingApp: Ping: ${
                            InetAddress.getByName(receiverIPAddress).isReachable(3000)
                        }"
                    )

                    if (InetAddress.getByName(receiverIPAddress).isReachable(3000)) {
                        if (socket != null && socket?.inetAddress != null && socket?.isConnected == true) {
                            val out = PrintWriter(
                                BufferedWriter(OutputStreamWriter(socket?.getOutputStream())), true
                            )
                            out.println(message)
                            out.flush()
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@ChargingMainScreen,
                                    "Connected & '$message' sent",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            socket = Socket(receiverIPAddress, tfsPort)
                            if (socket?.inetAddress != null) {
                                val out = PrintWriter(
                                    BufferedWriter(OutputStreamWriter(socket?.getOutputStream())),
                                    true
                                )
                                out.println(message)
                                out.flush()
                                withContext(Dispatchers.Main) {

                                    Toast.makeText(
                                        this@ChargingMainScreen,
                                        "Connected & '$message' sent",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            Log.d(
                                "ChargingApp",
                                "ChargingApp: Ping: ${
                                    InetAddress.getByName(receiverIPAddress).isReachable(3000)
                                } status: ${socket?.isConnected}  socket valid: ${socket?.inetAddress != null}"
                            )
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@ChargingMainScreen,
                                "Target IP: ${receiverIPAddress} & Ping: ${
                                    InetAddress.getByName(
                                        receiverIPAddress
                                    ).isReachable(3000)
                                }",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("ChargingApp", "Send Message is failed")
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(this@ChargingMainScreen, "Send Message is failed", Toast.LENGTH_SHORT).show()
//                    }
                }

            }

        }
    }

    // Sends a message to WebSocket Service using BroadcastReceiver
    private fun sendMessageToService(message: String) {
        /*val intent = Intent(WebSocketService.ACTION_SEND_MESSAGE).apply {
            setPackage("com.suffixdigital.chargingapp") // Explicit package
            putExtra(WebSocketService.EXTRA_MESSAGE, message)
        }
        sendBroadcast(intent)*/
        if (WebSocketService.webSocket != null) {
            WebSocketService.webSocket!!.send(message)
            Toast.makeText(this, "Sent: $message", Toast.LENGTH_SHORT).show()
        }
    }

}