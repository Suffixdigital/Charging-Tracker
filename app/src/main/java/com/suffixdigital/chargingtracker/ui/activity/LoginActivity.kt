package com.suffixdigital.chargingtracker.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.PatternsCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.textfield.TextInputEditText
import com.suffixdigital.chargingtracker.R
import com.suffixdigital.chargingtracker.databinding.ActivityLoginBinding
import com.suffixdigital.chargingtracker.interfaces.MessagePopupClickListener
import com.suffixdigital.chargingtracker.interfaces.SocketConnectionListener
import com.suffixdigital.chargingtracker.serverSocket.ServerService
import com.suffixdigital.chargingtracker.serverSocket.SocketServer
import com.suffixdigital.chargingtracker.ui.dialog.MessagePopupDialog
import com.suffixdigital.chargingtracker.utils.inputFilter
import com.suffixdigital.chargingtracker.utils.isSocketServerStarted
import com.suffixdigital.chargingtracker.websocket.WebSocketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Socket


class LoginActivity : AppCompatActivity(), View.OnClickListener, SocketConnectionListener,
    MessagePopupClickListener {
    private lateinit var binding: ActivityLoginBinding

    /******************************************************************************
     * [isEmailValid] is a boolean variable to validate does entered Email-ID is valid or not. Default value is 'False'
     *****************************************************************************/
    private var isEmailValid = false

    /******************************************************************************
     * [isPasswordValid] is a boolean variable to validate does entered Password is valid or not. Default value is 'False'
     *****************************************************************************/
    private var isPasswordValid = false

    var socketServer: SocketServer? = null

    private var clickCount = 0

    /******************************************************************************
     * [emailIDTextWatcher] is a instance of [TextInputEditText]. Here [TextInputEditText] has override class [TextWatcher] and it has
     * below over-ride functions to real time validate inserted data in [TextInputEditText]. Here This [TextWatcher] class is validate
     * entered Email-ID is valid or not. If Email-ID is not valid then display Error message to user
     *****************************************************************************/
    private val emailIDTextWatcher = object : TextWatcher {
        /******************************************************************************
         * [beforeTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        /******************************************************************************
         * [onTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        /******************************************************************************
         * [afterTextChanged], When user insert/delete character by character in [TextInputEditText] this function is validate does
         * Email-ID is empty or not also Email-ID is with valid pattern or not. If Email is valid then [isEmailValid] becomes true but if
         * Email-ID is not valid then display Error message to user. Once Email-ID and Password are valid then 'Login' button becomes enable and Change button color
         * and text color.
         *****************************************************************************/
        override fun afterTextChanged(editable: Editable?) {
            val currentText = editable.toString()
            val convertedLowerCase = currentText.lowercase()
            if (currentText != convertedLowerCase) {
                binding.etUsername.setText(convertedLowerCase)
                binding.etUsername.setSelection(convertedLowerCase.length)
            }

            when {
                /******************************************************************************
                 * If Email-ID empty then display error message to user. [isEmailValid] becomes a false
                 *****************************************************************************/
                editable.toString().isEmpty() -> {
                    isEmailValid = false
                    binding.tvUsernameError.visibility = View.VISIBLE
                    binding.tvUsernameError.text = resources.getString(R.string.err_empty_email)
                    binding.etUsername.isCursorVisible = true
                    displayErrorMessage()
                }
                /******************************************************************************
                 * If Email-ID is not valid then display error message to user. [isEmailValid] becomes a false
                 *****************************************************************************/
                !PatternsCompat.EMAIL_ADDRESS.matcher(editable.toString()).matches() -> {
                    isEmailValid = false
                    binding.tvUsernameError.visibility = View.VISIBLE
                    binding.tvUsernameError.text = resources.getString(R.string.err_invalid_email)
                    binding.etUsername.isCursorVisible = true
                    displayErrorMessage()
                }
                /******************************************************************************
                 * If Email-ID is valid then [isEmailValid] becomes a True. The Login button becomes enable and change button's background
                 * color and text color.
                 *****************************************************************************/
                else -> {
                    isEmailValid = true
                    binding.tvUsernameError.visibility = View.GONE
                    binding.tvPasswordError.visibility = View.GONE
                    if (isEmailValid && isPasswordValid) {
                        binding.btnLogIn.isEnabled = true
                        binding.btnLogIn.alpha = 1.0f
                    }
                }
            }
        }
    }

    /******************************************************************************
     * [passwordTextWatcher] is a instance of [TextInputEditText]. Here [TextInputEditText] has override class [TextWatcher] and it has
     * below over-ride functions to real time validate inserted data in [TextInputEditText]. Here This [TextWatcher] class is validate
     * entered Password is valid or not. If Password is not valid then display Error message to user
     *****************************************************************************/
    private val passwordTextWatcher = object : TextWatcher {
        /******************************************************************************
         * [beforeTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        /******************************************************************************
         * [onTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        /******************************************************************************
         * [afterTextChanged], When user insert/delete character by character in [TextInputEditText] this function is validate does
         * Password is empty or not. If Password is valid then [isPasswordValid] becomes true but if Password is not valid then
         * display Error message to user. Once Email-ID and Password are valid then 'Login' button becomes enable and Change button color
         * and text color.
         *****************************************************************************/
        override fun afterTextChanged(editable: Editable?) {

            when {
                /******************************************************************************
                 * If Password is empty then display error message to user. [isPasswordValid] becomes a false
                 *****************************************************************************/
                editable.toString().trim().isEmpty() -> {
                    isPasswordValid = false
                    binding.tvPasswordError.visibility = View.VISIBLE
                    binding.tvPasswordError.text = resources.getString(R.string.err_empty_password)
                    binding.etPassword.isCursorVisible = true
                    displayErrorMessage()
                }
                /******************************************************************************
                 * If Password is valid then [isPasswordValid] becomes a True. The Login button becomes enable and change button's background
                 * color and text color.
                 *****************************************************************************/
                else -> {
                    isPasswordValid = true
                    binding.tvUsernameError.visibility = View.GONE
                    binding.tvPasswordError.visibility = View.GONE
                    if (isEmailValid && isPasswordValid) {
                        binding.btnLogIn.isEnabled = true
                        binding.btnLogIn.alpha = 1.0f
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding.etUsername.addTextChangedListener(emailIDTextWatcher)
        socketServer?.getStartServer()
        socketServer?.setCommunicationListener(this@LoginActivity, this)
        binding.etPassword.addTextChangedListener(passwordTextWatcher)
        binding.etUsername.filters = arrayOf(inputFilter)
        binding.etPassword.filters = arrayOf(inputFilter)

        checkPermissions()

        binding.btnLogIn.setOnClickListener(this)
        binding.tvSettings.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        socketServer?.getStartServer()
        if (!isSocketServerStarted) {
            socketServer?.getStartServer()
        }
        socketServer?.setCommunicationListener(this@LoginActivity, this)
//        sendingMessagesBytes("Hello Socket!!!")
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_log_in -> {

                startService(Intent(this, ServerService::class.java))
                startService(Intent(this, WebSocketService::class.java))
                Intent(this@LoginActivity, ChargingMainScreen::class.java).apply {
                    startActivity(this)
                    //finish()
                }
            }

            R.id.tv_settings -> {
                clickCount++
                if (clickCount == 5) {
                    clickCount = 0
                    val messagePopupDialog =
                        MessagePopupDialog.getInstance(SpannableStringBuilder(""), "Save", this)
                    messagePopupDialog?.isCancelable = false
                    if (messagePopupDialog != null && supportFragmentManager.findFragmentByTag(
                            MessagePopupDialog.TAG
                        ) == null
                    ) {
                        messagePopupDialog.show(supportFragmentManager, MessagePopupDialog.TAG)
                    }
                }
            }
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API 33+)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+ (API 34+)
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC)
            }
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 101)
        }
    }

    private fun displayErrorMessage() {
        binding.btnLogIn.alpha = 0.65f
        binding.btnLogIn.isEnabled = false
    }

    override fun onConnect(socket: Socket?) {
    }

    override fun onDisconnect(ipAddress: String?) {
    }

    override fun onMessageReceive(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                Log.e("ChargingApp", "ChargingApp: LoginScreen. Received Message: $message")
                Toast.makeText(this@LoginActivity, "Received: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onButtonClick() {

    }


}