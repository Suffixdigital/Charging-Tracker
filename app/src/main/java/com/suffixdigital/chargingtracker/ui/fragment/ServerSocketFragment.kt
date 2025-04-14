package com.suffixdigital.chargingtracker.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.suffixdigital.chargingtracker.databinding.FragmentServerSocketBinding
import com.suffixdigital.chargingtracker.interfaces.SocketConnectionListener
import com.suffixdigital.chargingtracker.serverSocket.SocketServer
import com.suffixdigital.chargingtracker.ui.activity.ChargingMainScreen
import com.suffixdigital.chargingtracker.utils.isSocketServerStarted
import com.suffixdigital.chargingtracker.utils.receiverIPAddress
import com.suffixdigital.chargingtracker.utils.tfsPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket


/**
 * Created by Kirtikant Patadiya on 2024-12-31.
 */
class ServerSocketFragment : Fragment(), SocketConnectionListener {

    private lateinit var binding: FragmentServerSocketBinding
    private var senderStringBuilder = StringBuilder()
    private var receiverStringBuilder = StringBuilder()

    /***********************************************************************************************************************
     * Inject [socketServer] variable. The [SocketServer] class initialize at once only when TF Driver app started using Dagger Dependencies Injection.
     * This variable can use in whole application.
     **********************************************************************************************************************/


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = FragmentServerSocketBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isSocketServerStarted) {
            (activity as ChargingMainScreen).socketServer?.getStartServer()
        }

        binding.etIpAddress.setText(receiverIPAddress)
        senderStringBuilder.clear()
        senderStringBuilder.append("Sender:\n")
        binding.tvSender.text = senderStringBuilder.toString()

        receiverStringBuilder.clear()
        receiverStringBuilder.append("Receiver:\n")
        binding.tvReceiver.text = receiverStringBuilder.toString()

        binding.tvClearSender.setOnClickListener {
            senderStringBuilder.clear()
            senderStringBuilder.append("Sender:\n")
            binding.tvSender.text = senderStringBuilder.toString()
        }

        binding.tvClearReceiver.setOnClickListener {
            receiverStringBuilder.clear()
            receiverStringBuilder.append("Receiver:\n")
            binding.tvReceiver.text = receiverStringBuilder.toString()
        }
        binding.btnSend.setOnClickListener {
            receiverIPAddress = binding.etIpAddress.text.toString()
            hideSoftKeyboard(it)
            sendingMessages(binding.etMessage.text.toString())
        }

        binding.btnSendToServer.setOnClickListener {
            receiverIPAddress = binding.etIpAddress.text.toString()
            hideSoftKeyboard(it)
            sendingMessageToServer()
        }

        binding.btnSendToEvse.setOnClickListener {
            //sendingMessageToEVSE()
        }
    }

    override fun onConnect(socket: Socket?) {

    }

    override fun onDisconnect(ipAddress: String?) {
    }

    override fun onResume() {
        super.onResume()
        Log.e(
            "ChargingApp",
            "ChargingApp: ServerSocketFragment.  onResume called. isSocketServerStarted:  $isSocketServerStarted"
        )
        (activity as ChargingMainScreen).socketServer?.setCommunicationListener(
            (activity as ChargingMainScreen),
            this
        )
    }

    override fun onMessageReceive(message: String) {
        if (activity is ChargingMainScreen) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
//                    receiverStringBuilder.append("$message\n")
//                    binding.tvReceiver.text = receiverStringBuilder.toString()
                    Log.e("ChargingApp", "ChargingApp: Received Message: $message")
                    Toast.makeText(activity, "Received: $message", Toast.LENGTH_SHORT).show()

                }
            }
        }

    }

    private fun sendingMessages(message: String) {

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Default) {
                try {
                    if (InetAddress.getByName(binding.etIpAddress.text.toString())
                            .isReachable(3000)
                    ) {
                        val socket = Socket(binding.etIpAddress.text.toString(), tfsPort)
                        if (socket.inetAddress != null) {
                            val out = PrintWriter(
                                BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true
                            )
                            out.println(message)
//                                    out.println("Hello Socket ${socket.inetAddress}  port: ${socket.port}  status: ${socket.isConnected}")
                            out.flush()
                            socket.close()
                            withContext(Dispatchers.Main) {
                                // Update UI with the sent message
                                senderStringBuilder.append("$message\n")
                                binding.tvSender.text = senderStringBuilder.toString()
                                /* Toast.makeText(
                                     context,
                                     "Message sent successfully!.",
                                     Toast.LENGTH_LONG
                                 ).show()*/
                            }
                        }
                    } else {
                        /* withContext(Dispatchers.Main) {
                             Toast.makeText(
                                 context,
                                 "IP: ${binding.etIpAddress.text.toString()} is not reachable. isSocketServerStarted:  $isSocketServerStarted",
                                 Toast.LENGTH_LONG
                             ).show()
                         }*/
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("ChargingApp", "Send Message is failed")
                    /* withContext(Dispatchers.Main) {
                         Toast.makeText(
                             context,
                             "IP: ${binding.etIpAddress.text.toString()}. isSocketServerStarted:  $isSocketServerStarted Message sending failed.\n Exception: ${e.message}",
                             Toast.LENGTH_LONG
                         ).show()
                     }*/
                }

            }

        }
    }

    private fun sendingMessagesBytes(message: String) {

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Default) {
                try {
                    if (InetAddress.getByName(binding.etIpAddress.text.toString())
                            .isReachable(3000)
                    ) {
                        val socket = Socket(binding.etIpAddress.text.toString(), tfsPort)
                        if (socket.inetAddress != null) {
                            val out = PrintWriter(
                                BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true
                            )
                            out.println(message)
//                                    out.println("Hello Socket ${socket.inetAddress}  port: ${socket.port}  status: ${socket.isConnected}")
                            out.flush()
                            socket.close()
                            withContext(Dispatchers.Main) {
                                // Update UI with the sent message
                                senderStringBuilder.append("$message\n")
                                binding.tvSender.text = senderStringBuilder.toString()
                                /*Toast.makeText(
                                    context,
                                    "Message sent successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()*/
                            }
                        }
                    } else {
                        /*withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "IP: ${binding.etIpAddress.text.toString()} is not reachable",
                                Toast.LENGTH_SHORT
                            ).show()
                        }*/
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("ChargingApp", "Send Message is failed")
                    /*withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "IP: ${binding.etIpAddress.text.toString()}. Message sending failed.\n Exception: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }*/
                }

            }

        }
    }


    private fun sendingMessageToServer() {
        //val bytearrayData = createHardwareMessage()

        // Create the message
        val message = "$,01020304,72623859790382856,1000,300,998,299,20,1,3,0,1,1,0,0,0,1,1,#"

        // Send data as ByteString
        sendingMessagesBytes(message)
    }

    /*private fun sendingMessageToEVSE() {
        val startMessage = "$0x2C0x010x2C#"
        sendingMessagesBytes(startMessage)
    }
*/


    fun hideSoftKeyboard(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Example in Java for Android
    fun createHardwareMessage(): ByteArray {
        // Header
        val header = byteArrayOf(0x24, 0x2C)

        // OH_ACD ID (4 bytes)
        val ohAcdId = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x2C)

        // Session ID (8 bytes)
        val sessionId = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x2C)

        // Voltage Demand (2 bytes)
        val voltageDemand = byteArrayOf(0x03, 0xE8.toByte(), 0x2C) // 1000V = 0x03E8

        // Current Demand (2 bytes)
        val currentDemand = byteArrayOf(0x01, 0x2C, 0x2C) // 300A = 0x012C

        // Voltage Feedback (2 bytes)
        val voltageFeedback = byteArrayOf(0x03, 0xE6.toByte(), 0x2C) // 998V = 0x03E6

        // Current Feedback (2 bytes)
        val currentFeedback = byteArrayOf(0x01, 0x2B, 0x2C) // 299A = 0x012B

        // State of Charge (SOC) (1 byte)
        val soc = byteArrayOf(0x14, 0x2C) // 20% = 0x14

        // CCS Charger State (1 byte)
        val ccsChargerState = byteArrayOf(0x01, 0x2C) // Charger Power On = 0x01

        // CCS Charging State (1 byte)
        val ccsChargingState = byteArrayOf(0x03, 0x2C) // Charging = 0x03

        // Error Code (1 byte)
        val errorCode = byteArrayOf(0x00, 0x2C) // No Error = 0x00

        // Vehicle Confirmation (1 byte)
        val vehicleConfirmation = byteArrayOf(0x01, 0x2C) // Confirmed = 0x01

        // Contactor Confirmation (1 byte)
        val contactorConfirmation = byteArrayOf(0x01, 0x2C) // Confirmed = 0x01

        // Footer
        val footer = byteArrayOf(0x23)

        // Combine all segments into one message
        val outputStream = ByteArrayOutputStream()
        try {
            outputStream.write(header)
            outputStream.write(ohAcdId)
            outputStream.write(sessionId)
            outputStream.write(voltageDemand)
            outputStream.write(currentDemand)
            outputStream.write(voltageFeedback)
            outputStream.write(currentFeedback)
            outputStream.write(soc)
            outputStream.write(ccsChargerState)
            outputStream.write(ccsChargingState)
            outputStream.write(errorCode)
            outputStream.write(vehicleConfirmation)
            outputStream.write(contactorConfirmation)
            outputStream.write(footer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Return the complete message as a byte array
        return outputStream.toByteArray()
    }

    fun parseMessage(byteArray: ByteArray): Map<String, Any> {
        var index = 0

        // Read Header
        val header = byteArray[index++]

        // OH_ACD ID (4 bytes)
        val ohAcdId = ((byteArray[index++].toInt() and 0xFF) shl 24) or
                ((byteArray[index++].toInt() and 0xFF) shl 16) or
                ((byteArray[index++].toInt() and 0xFF) shl 8) or
                (byteArray[index++].toInt() and 0xFF)

        // Session ID (8 bytes)
        val sessionId = (0..7).map {
            byteArray[index++].toInt() and 0xFF
        }.joinToString(separator = "") { it.toString(16).padStart(2, '0') }

        // Voltage Demand (2 bytes)
        val voltageDemand = ((byteArray[index++].toInt() and 0xFF) shl 8) or
                (byteArray[index++].toInt() and 0xFF)

        // Current Demand (2 bytes)
        val currentDemand = ((byteArray[index++].toInt() and 0xFF) shl 8) or
                (byteArray[index++].toInt() and 0xFF)

        // Voltage Feedback (2 bytes)
        val voltageFeedback = ((byteArray[index++].toInt() and 0xFF) shl 8) or
                (byteArray[index++].toInt() and 0xFF)

        // Current Feedback (2 bytes)
        val currentFeedback = ((byteArray[index++].toInt() and 0xFF) shl 8) or
                (byteArray[index++].toInt() and 0xFF)

        // State of Charge (SOC) (1 byte)
        val soc = byteArray[index++].toInt() and 0xFF

        // CCS Charger State (1 byte)
        val ccsChargerState = byteArray[index++].toInt() and 0xFF

        // CCS Charging State (1 byte)
        val ccsChargingState = byteArray[index++].toInt() and 0xFF

        // Error Code (1 byte)
        val errorCode = byteArray[index++].toInt() and 0xFF

        // Vehicle Confirmation (1 byte)
        val vehicleConfirmation = byteArray[index++].toInt() and 0xFF

        // Contactor Confirmation (1 byte)
        val contactorConfirmation = byteArray[index++].toInt() and 0xFF

        // Return parsed values as a map for readability
        return mapOf(
            "Header" to header,
            "OH_ACD ID" to ohAcdId,
            "Session ID" to sessionId,
            "Voltage Demand (V)" to voltageDemand,
            "Current Demand (A)" to currentDemand,
            "Voltage Feedback (V)" to voltageFeedback,
            "Current Feedback (A)" to currentFeedback,
            "SOC (%)" to soc,
            "CCS Charger State" to ccsChargerState,
            "CCS Charging State" to ccsChargingState,
            "Error Code" to errorCode,
            "Vehicle Confirmation" to vehicleConfirmation,
            "Contactor Confirmation" to contactorConfirmation
        )
    }


}
