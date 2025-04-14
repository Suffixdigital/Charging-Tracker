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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.suffixdigital.chargingtracker.R
import com.suffixdigital.chargingtracker.databinding.ChargingEndSessionScreenBinding
import com.suffixdigital.chargingtracker.interfaces.SocketConnectionListener
import com.suffixdigital.chargingtracker.serverSocket.SocketServer
import com.suffixdigital.chargingtracker.utils.battery_percentage
import com.suffixdigital.chargingtracker.utils.chargingID
import com.suffixdigital.chargingtracker.utils.decimalFormat
import com.suffixdigital.chargingtracker.utils.endTime
import com.suffixdigital.chargingtracker.utils.isSocketServerStarted
import com.suffixdigital.chargingtracker.utils.power_consumed
import com.suffixdigital.chargingtracker.utils.receiverIPAddress
import com.suffixdigital.chargingtracker.utils.socket
import com.suffixdigital.chargingtracker.utils.startCharging_time
import com.suffixdigital.chargingtracker.utils.startTime
import com.suffixdigital.chargingtracker.utils.totalChargingTime
import com.suffixdigital.chargingtracker.utils.total_amount
import com.suffixdigital.chargingtracker.viewmodels.ChargingAppViewModel
import com.suffixdigital.chargingtracker.websocket.WebSocketService
import dagger.hilt.android.AndroidEntryPoint
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit.HOURS
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.MINUTES

@AndroidEntryPoint
class ChargingEndSessionScreen : AppCompatActivity(), SocketConnectionListener {
    private lateinit var binding: ChargingEndSessionScreenBinding

    /**
     * `countDownTimer` holds the instance of the `CountDownTimer` class.
     * This object is responsible for scheduling and executing the timed task.
     *
     * It's nullable (`CountDownTimer?`) as it might not be initialized
     * until a timer is started.
     */
    var countDownTimer: CountDownTimer? = null
    private var idealCountDownTimer: CountDownTimer? = null
    var chargingTime: Int = 0
    var power: Double = 0.0
    var totalAmount: Double = 0.0
    var mMilliSeconds: Long = 15 * 60 * 1000
    var batterPercentage = 30
    var isStartCommand = ""
    private lateinit var animation: Animation

    private val chargingViewModel: ChargingAppViewModel by viewModels()

    /***********************************************************************************************************************
     * Inject [socketServer] variable. The [SocketServer] class initialize at once only when TF Driver app started using Dagger Dependencies Injection.
     * This variable can use in whole application.
     **********************************************************************************************************************/
    private var socketServer: SocketServer? = null
    private var chargingStaionID: Int? = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.charging_end_session_screen)
        val bundle: Bundle? = intent.extras
        chargingStaionID = bundle?.getInt("ID", 1)
        chargingID = chargingStaionID
        Log.d("ChargingApp", "ChargingApp_Android: ID: $chargingStaionID")

        binding.slideView.isEnabled = false
        animation = initializeAlphaAnimation()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                // sendingMessagesBytes("$,4,#")
            }
        }

        binding.btnStartStop.setOnClickListener {
            if (mMilliSeconds == 900000L) {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        if (binding.btnStartStop.text.toString().startsWith("Start", true)) {
                            isStartCommand = "$,1,#"
                            // val bytearrayData = createStartStopMessage(isStart = true)
                            //sendingMessagesBytes(isStartCommand)
                            sendMessageToService(isStartCommand)
                            startTime = SimpleDateFormat(
                                "MM/dd/yyyy hh:mm:ss aa",
                                Locale.getDefault()
                            ).format(
                                Calendar.getInstance().time
                            )
                            binding.btnStartStop.text = "Stop the charging"
                            binding.slideView.isEnabled = false
                            binding.slideView.setButtonBackgroundColor(
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        this@ChargingEndSessionScreen,
                                        R.color.red_F61D25
                                    )
                                )
                            )
//                            binding.loadingProcess.appIcon.startAnimation(animation)
                            binding.tvChargingStatus.text = "Cable Check"
                            //sendingMessagesBytes("$,4,#")
                            idealStartTimerTask(startCharging_time * 1000)
                            delay(startCharging_time * 1000)
//                            binding.loadingProcess.appIcon.clearAnimation()
                            idealCountDownTimer?.onFinish()
                            idealCountDownTimer?.cancel()
                            idealCountDownTimer = null
                            binding.tvChargingStatus.text = "Charging Status"

                            //  binding.tvArrowTitle.text = "Charging"
                            chargingViewModel.doUpdateChargingStatus(
                                this@ChargingEndSessionScreen,
                                chargingStaionID!!,
                                "Start"
                            )
                            startTimerTask()
                        }
                    }
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        if (binding.btnStartStop.text.toString().startsWith("Stop", true)) {
                            chargingViewModel.doUpdateChargingStatus(
                                this@ChargingEndSessionScreen,
                                chargingStaionID!!,
                                "Stop"
                            )
                            // binding.tvArrowTitle.text = "ARAI & Cohzet: OHACD Device"
                            isStartCommand = "$,5,#"
                            //val bytearrayData = createStartStopMessage(isStart = false)
//                            sendingMessagesBytes(isStartCommand)
                            sendMessageToService(isStartCommand)
                            //sendingMessagesBytes("$,4,#")
                            //idealStartTimerTask(100 * 1000)
//                            binding.loadingProcess.appIcon.startAnimation(animation)
                            binding.tvChargingStatus.text = "End of Charging Session"
                            delay(1000L)
                            binding.btnStartStop.text = "Start the charging"
                            binding.btnStartStop.alpha = 0.65f
                            countDownTimer?.onFinish()
                            countDownTimer?.cancel()
                            countDownTimer = null

                            delay(4000L)
                            binding.tvChargingStatus.text = "The connector rail is moving up."
                            delay(15000L)
                            binding.tvChargingStatus.text = "Charging Status"
//                            binding.loadingProcess.appIcon.clearAnimation()
                            binding.ivTruck1.visibility = View.GONE
                            binding.ivTruck2.visibility = View.VISIBLE
                            binding.slideView.isEnabled = true
                            binding.slideView.setButtonBackgroundColor(
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        this@ChargingEndSessionScreen,
                                        R.color.green_3EC94A
                                    )
                                )
                            )

                        }
                    }
                }


                /*if (binding.btnStartStop.text.toString().startsWith("Start", true)) {
                    binding.tvArrowTitle.text = "Charging"
                    isStartCommand = "$,1,#"
                    // val bytearrayData = createStartStopMessage(isStart = true)
                    sendingMessagesBytes(isStartCommand)
                    binding.btnStartStop.text = "Stop the charging"
                    binding.slideView.isEnabled = false
                    binding.slideView.setButtonBackgroundColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this@ChargingEndSessionScreen,
                                R.color.red_F61D25
                            )
                        )
                    )
                    startTimerTask()

                }
                else {
                    binding.tvArrowTitle.text = "Vehicle positioned correctly"
                    isStartCommand = "$,0,#"
                    //val bytearrayData = createStartStopMessage(isStart = false)
                    sendingMessagesBytes(isStartCommand)
                    binding.slideView.isEnabled = true
                    binding.slideView.setButtonBackgroundColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this@ChargingEndSessionScreen,
                                R.color.green_3EC94A
                            )
                        )
                    )
                    binding.btnStartStop.text = "Start the charging"
                    countDownTimer?.onFinish()
                    countDownTimer?.cancel()
                    countDownTimer = null
                }*/
            }

        }

        binding.slideView.setOnSlideCompleteListener {
            idealCountDownTimer?.onFinish()
            idealCountDownTimer?.cancel()
            idealCountDownTimer = null
            endTime = SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault()).format(
                Calendar.getInstance().time
            )
            startActivity(Intent(this, ChargingSummaryScreen::class.java))
        }
    }

    private fun startTimerTask() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(mMilliSeconds, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(milliSeconds: Long) {
                mMilliSeconds = milliSeconds
                if (batterPercentage == 80) {

                    isStartCommand = "$,5,#"
                    //val bytearrayData = createStartStopMessage(isStart = false)
                    //sendingMessagesBytes(isStartCommand)
                    sendMessageToService(isStartCommand)
                    binding.slideView.isEnabled = true
                    binding.slideView.setButtonBackgroundColor(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this@ChargingEndSessionScreen,
                                R.color.green_3EC94A
                            )
                        )
                    )
                    binding.btnStartStop.text = "Start the charging"
                    //sendingMessagesBytes("$,4,#")
                    countDownTimer?.onFinish()
                    countDownTimer?.cancel()
                    countDownTimer = null
                } else {
                    //sendingMessagesBytes(isStartCommand)
                    sendMessageToService(isStartCommand)
                    chargingTime += 1000
                    totalAmount += 100
                    power += 2.5
                    if (chargingTime % 18000 == 0) {
                        batterPercentage += 1
                    }

                    battery_percentage = batterPercentage
                    total_amount = "₹${decimalFormat.format(totalAmount / 100)}"
                    power_consumed = "${decimalFormat.format(power / 100)} kwh"
                    totalChargingTime = chargingTime
                    binding.tvChargeTime.text = hmsTimeFormatter(chargingTime.toLong())
                    binding.tvRemainingTime.text = hmsTimeFormatter(milliSeconds)
                    binding.tvAmount.text = total_amount
                    binding.tvPower.text = power_consumed
                    binding.tvBatteryPercentage.text = " ${batterPercentage}% Charged"
                }


            }

            override fun onFinish() {
                countDownTimer?.cancel()
                countDownTimer = null
            }

        }
        countDownTimer?.start()
    }

    fun hmsTimeFormatter(milliSeconds: Long): String {
        val hours = 0L
        val minutes = MILLISECONDS.toMinutes(milliSeconds) % HOURS.toMinutes(1)
        val seconds = MILLISECONDS.toSeconds(milliSeconds) % MINUTES.toSeconds(1)

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onResume() {
        super.onResume()
        socketServer = SocketServer()
        if (!isSocketServerStarted) {
            socketServer?.getStartServer()
        }
        socketServer?.setCommunicationListener(this@ChargingEndSessionScreen, this)
        Log.e(
            "ChargingApp",
            "ChargingApp: ChargingEndSessionScreen.  onResume called. isSocketServerStarted:  $isSocketServerStarted"
        )

    }

    override fun onConnect(socket: Socket?) {
    }

    override fun onDisconnect(ipAddress: String?) {
    }

    override fun onMessageReceive(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                Log.e("ChargingApp", "ChargingApp: ChargingEndSession. Received Message: $message")
                Toast.makeText(
                    this@ChargingEndSessionScreen,
                    "Received: $message",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        //Toast.makeText(this@ChargingEndSessionScreen,"Received Message: $message",Toast.LENGTH_SHORT).show()
        /*CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                val base64Regex = Regex("^[A-Za-z0-9+/]+={0,2}$")
                if (base64Regex.matches(message)) {
                    val receivedBytes: ByteArray = Base64.decode(message, Base64.DEFAULT)
                    val parseData = parseStartStopMessage(receivedBytes)
                    Toast.makeText(this@ChargingEndSessionScreen,"Received: $parseData",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@ChargingEndSessionScreen,"Received: $message",Toast.LENGTH_SHORT).show()

                }

            }
        }*/
    }

    private fun parseStartStopMessage(message: ByteArray): String {
        if (message.size != 5) {
            throw IllegalArgumentException("Invalid message length")
        }

        val header = message[0]
        val startStopDemand = message[2]
        val footer = message[4]

        // Validate header and footer
        if (header != 0x24.toByte() || footer != 0x23.toByte()) {
            throw IllegalArgumentException("Invalid message format")
        }

        // Parse the Start/Stop demand
        return when (startStopDemand) {
            0x00.toByte() -> "Stop Command Received"
            0x01.toByte() -> "Start Command Received"
            else -> "Unknown Command"
        }
    }


    fun createStartStopMessage(isStart: Boolean): ByteArray {
        val header: Byte = 0x24  // '$'
        val separator: Byte = 0x2C // ','
        val startStopDemand: Byte = if (isStart) 0x01 else 0x00 // 0x01 for Start, 0x00 for Stop
        val footer: Byte = 0x23 // '#'

        // Construct the byte array
        return byteArrayOf(
            header,       // Start of the message
            separator,    // Separator
            startStopDemand, // Start/Stop command
            separator,    // Separator
            footer        // End of the message
        )
    }

    private fun sendingMessagesBytes(message: String) {

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Default) {
                try {
                    //Log.d("ChargingApp", "ChargingApp: Ping: ${InetAddress.getByName(receiverIPAddress).isReachable(3000)} status: ${socket?.isConnected}  socket valid: ${socket?.inetAddress != null}")

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
        /* val intent = Intent(WebSocketService.ACTION_SEND_MESSAGE).apply {
             setPackage("com.suffixdigital.chargingapp") // Explicit package
             putExtra(WebSocketService.EXTRA_MESSAGE, message)
         }
         sendBroadcast(intent)*/
    }

    private fun idealStartTimerTask(connectingTime: Long) {
        idealCountDownTimer?.onFinish()
        idealCountDownTimer?.cancel()
        idealCountDownTimer = null
        idealCountDownTimer = object : CountDownTimer(connectingTime, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(milliSeconds: Long) {
                //sendingMessagesBytes("$,4,#")
                Log.e("ChargingApp", "Charging App....milliSeconds:$milliSeconds")
                if (milliSeconds <= 15000L) {
                    binding.tvChargingStatus.text = "Precharge"
                }
                if (milliSeconds <= 10000L) {
                    binding.tvChargingStatus.text = "Power Delivery Request"
                }
                if (milliSeconds <= 5000L) {
                    binding.tvChargingStatus.text = "Charging"
                }

            }

            override fun onFinish() {
                //binding.loadingProcess.appIcon.clearAnimation()
                binding.tvChargingStatus.visibility = View.VISIBLE
                binding.tvChargingStatus.text = "Charging Status"
            }
        }
        idealCountDownTimer?.start()
    }

    /******************************************************************************
     * [initializeAlphaAnimation] function is used to initialize instance of blinking animation.
     *****************************************************************************/
    private fun initializeAlphaAnimation(): Animation {
        //alpha for animation is between 0.2 to 1.0
        val animation: Animation = AlphaAnimation(0.2f, 1.0f)
        animation.duration = 500                         //animation duration 0.5sec
        animation.startOffset = 20                       //animation start after 0.02sec
        animation.repeatMode = Animation.REVERSE         //animation mode is reverse
        animation.repeatCount = Animation.INFINITE       //animation count is infinite
        return animation
    }

}