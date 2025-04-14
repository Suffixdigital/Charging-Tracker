package com.suffixdigital.chargingtracker.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.suffixdigital.chargingtracker.R
import com.suffixdigital.chargingtracker.databinding.ChargingStationConnectionScreenBinding
import com.suffixdigital.chargingtracker.interfaces.SocketConnectionListener
import com.suffixdigital.chargingtracker.serverSocket.SocketServer
import com.suffixdigital.chargingtracker.utils.chargingID
import com.suffixdigital.chargingtracker.utils.connecting_time
import com.suffixdigital.chargingtracker.utils.isSocketServerStarted
import com.suffixdigital.chargingtracker.utils.receiverIPAddress
import com.suffixdigital.chargingtracker.utils.socket
import com.suffixdigital.chargingtracker.utils.startCharging_time
import com.suffixdigital.chargingtracker.websocket.WebSocketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket


class ChargingStationConnectionScreen : AppCompatActivity(), SocketConnectionListener {

    /**
     * `countDownTimer` holds the instance of the `CountDownTimer` class.
     * This object is responsible for scheduling and executing the timed task.
     *
     * It's nullable (`CountDownTimer?`) as it might not be initialized
     * until a timer is started.
     */
    var countDownTimer: CountDownTimer? = null

    private lateinit var binding: ChargingStationConnectionScreenBinding
    private lateinit var animation: Animation
    private var chargingStaionID: Int? = 1
    private var idealCountDownTimer: CountDownTimer? = null

    /***********************************************************************************************************************
     * Inject [socketServer] variable. The [SocketServer] class initialize at once only when TF Driver app started using Dagger Dependencies Injection.
     * This variable can use in whole application.
     **********************************************************************************************************************/
    private var socketServer: SocketServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.charging_station_connection_screen)

        val bundle: Bundle? = intent.extras
        chargingStaionID = bundle?.getInt("ID", 1)
        chargingID = chargingStaionID
        Log.d("ChargingApp", "ChargingApp_Android: ID: $chargingStaionID")
        animation = initializeAlphaAnimation()
        binding.slideView.setText("")
        binding.slideView.isEnabled = false
        binding.slideView.setButtonBackgroundColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@ChargingStationConnectionScreen,
                    R.color.green_3EC94A_50
                )
            )
        )
        //startTimerTask()

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.ivDirectionTop.startAnimation(animation)
                binding.ivDirectionTopConnected.visibility = View.INVISIBLE
                binding.tvArrowInstruction.visibility = View.VISIBLE
                binding.tvArrowInstruction.text = "Move truck forward..."

                delay(3000)

                binding.ivDirectionTop.clearAnimation()
                binding.ivDirectionTop.visibility = View.INVISIBLE
                binding.ivDirectionTopConnected.visibility = View.VISIBLE
                binding.slideView.isEnabled = true
                binding.slideView.setText("")
                binding.slideView.setButtonBackgroundColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@ChargingStationConnectionScreen,
                            R.color.green_3EC94A
                        )
                    )
                )
                binding.tvArrowInstruction.text = "Vehicle positioned correctly"
                // binding.ivDirection.setImageResource(R.drawable.ic_connected_arrows)
            }
        }


        binding.slideView.setOnSlideCompleteListener {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    //sendingMessagesBytes("$,3,#")
                    sendMessageToService("$,3,#")
                    binding.slideView.setText("Connecting.....")
                    binding.slideView.isEnabled = false
                    binding.ivTruckAntennaConnecting.visibility = View.VISIBLE
                    binding.ivTruckAntennaConnecting.setImageResource(R.drawable.truck_antenna_connecting_1)
                    binding.animationView.visibility = View.VISIBLE
                    binding.slideView.setButtonBackgroundColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this@ChargingStationConnectionScreen,
                                R.color.green_3EC94A_70
                            )
                        )
                    )

//                    binding.ivTruckAntennaConnected.visibility = View.VISIBLE
                    // startTimerTask(connecting_time * 1000)
                    //sendingMessagesBytes("$,3,#")
                    idealStartTimerTask(startCharging_time * 1000)
                    delay(connecting_time * 1000)
                    idealCountDownTimer?.onFinish()
                    idealCountDownTimer?.cancel()
                    idealCountDownTimer = null
                    binding.animationView.visibility = View.GONE
                    binding.slideView.setText("Connected")
                    val intent = Intent(
                        this@ChargingStationConnectionScreen,
                        ChargingEndSessionScreen::class.java
                    )
                    intent.putExtra("ID", chargingStaionID)
                    startActivity(intent)
                }
            }

        }
    }

    private fun startTimerTask(connectingTime: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(connectingTime, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(milliSeconds: Long) {
                //sendingMessagesBytes("$,3,#")
                sendMessageToService("$,3,#")
            }

            override fun onFinish() {
                countDownTimer?.onFinish()
                countDownTimer?.cancel()
                countDownTimer = null
            }
        }
    }


    /******************************************************************************
     * [initializeAlphaAnimation] function is used to initialize instance of blinking animation.
     *****************************************************************************/
    private fun initializeAlphaAnimation(): Animation {
        //alpha for animation is between 0.2 to 1.0
        val animation: Animation = AlphaAnimation(0.05f, 1.0f)
        animation.duration = 500                         //animation duration 0.5sec
        animation.startOffset = 20                       //animation start after 0.02sec
        animation.repeatMode = Animation.REVERSE         //animation mode is reverse
        animation.repeatCount = Animation.INFINITE       //animation count is infinite
        return animation
    }

    override fun onResume() {
        super.onResume()
        socketServer = SocketServer()
        if (!isSocketServerStarted) {
            socketServer?.getStartServer()
        }
        socketServer?.setCommunicationListener(this@ChargingStationConnectionScreen, this)
        Log.e(
            "ChargingApp",
            "ChargingApp: ChargingStationConnectionScreen  onResume called. isSocketServerStarted:  $isSocketServerStarted"
        )
    }

    private fun sendingMessagesBytes(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Default) {
                try {
                    Log.d(
                        "ChargingApp",
                        "ChargingApp: Ping: ${
                            InetAddress.getByName(receiverIPAddress).isReachable(3000)
                        } status: ${socket?.isConnected}  socket valid: ${socket?.inetAddress != null}"
                    )

                    if (InetAddress.getByName(receiverIPAddress).isReachable(3000)) {
                        //val socket = Socket(receiverIPAddress, tfsPort)
                        if (socket?.inetAddress != null) {
                            val out = PrintWriter(
                                BufferedWriter(OutputStreamWriter(socket?.getOutputStream())), true
                            )
                            out.println(message)
//                                    out.println("Hello Socket ${socket.inetAddress}  port: ${socket.port}  status: ${socket.isConnected}")
                            out.flush()
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("ChargingApp", "Send Message is failed")

                }

            }

        }
    }

    // Sends a message to WebSocket Service using BroadcastReceiver
    private fun sendMessageToService(message: String) {
        if (WebSocketService.webSocket != null) {
            WebSocketService.webSocket!!.send(message)
            Toast.makeText(this, "Sent: $message", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onConnect(socket: Socket?) {
    }

    override fun onDisconnect(ipAddress: String?) {
    }

    override fun onMessageReceive(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                Log.e(
                    "ChargingApp",
                    "ChargingApp: ChargingStationConnectionScreen. Received Message: $message"
                )
                Toast.makeText(
                    this@ChargingStationConnectionScreen,
                    "Received: $message",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    /*private fun startTimerTask() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(10 * 1000, 1000) {
            override fun onTick(milliSeconds: Long) {
                binding.tvArrowInstruction.visibility = View.VISIBLE
                val differenceInSeconds: Int = Math.round(milliSeconds / 1000.0).toInt()
                if (differenceInSeconds % 4 == 1) {
                    binding.ivDirection.rotation = 90f
                    binding.tvArrowInstruction.text = "Move backward..."
                } else if (differenceInSeconds % 4 == 2) {
                    binding.ivDirection.rotation = 270f
                    binding.tvArrowInstruction.text = "Move forword..."
                } else if (differenceInSeconds % 4 == 3) {
//                    binding.ivDirection.rotation = 180f
//                    binding.tvArrowInstruction.text = "Steer left..."
                } else if (differenceInSeconds % 4 == 0) {
//                    binding.ivDirection.rotation = 360f
//                    binding.tvArrowInstruction.text = "Steer right..."
                }
            }

            override fun onFinish() {
                binding.slideView.isEnabled = true
                binding.slideView.setText("")
                binding.slideView.setButtonBackgroundColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@ChargingStationConnectionScreen,
                            R.color.green_3EC94A
                        )
                    )
                )
                binding.tvArrowInstruction.visibility = View.INVISIBLE
                binding.ivDirection.setImageResource(R.drawable.ic_connected_arrows)
                // binding.ivDirection.background = ContextCompat.getDrawable(this@ChargingStationConnectionScreen,R.drawable.ic_connected_arrows)
                //binding.ivTruckAntenna.background = ContextCompat.getDrawable(this@ChargingStationConnectionScreen,R.drawable.connected_truck)
                countDownTimer?.cancel()
                countDownTimer = null
            }

        }
        countDownTimer?.start()
    }*/

    private fun idealStartTimerTask(connectingTime: Long) {
        idealCountDownTimer?.onFinish()
        idealCountDownTimer?.cancel()
        idealCountDownTimer = null
        idealCountDownTimer = object : CountDownTimer(connectingTime, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(milliSeconds: Long) {
                //sendingMessagesBytes("$,4,#")
                Log.e("ChargingApp", "Charging App....milliSeconds:$milliSeconds")
                if (milliSeconds <= 25000L) {
                    binding.ivTruckAntennaConnecting.setImageResource(R.drawable.truck_antenna_connecting_1)
                }
                if (milliSeconds <= 20000L) {
                    binding.ivTruckAntennaConnecting.setImageResource(R.drawable.truck_antenna_connecting_2)
                }
                if (milliSeconds <= 15000L) {
                    binding.ivTruckAntennaConnecting.setImageResource(R.drawable.truck_antenna_connecting_3)
                }
                if (milliSeconds <= 10000L) {
                    binding.ivTruckAntennaConnecting.setImageResource(R.drawable.truck_antenna_connecting_4)
                }
                if (milliSeconds <= 2000L) {
                    binding.ivTruckAntennaConnecting.setImageResource(R.drawable.truck_antenna_connecting_5)
                }

            }

            override fun onFinish() {
            }
        }
        idealCountDownTimer?.start()
    }
}