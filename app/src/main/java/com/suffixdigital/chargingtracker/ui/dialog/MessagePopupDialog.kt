package com.suffixdigital.chargingtracker.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout.LayoutParams
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.suffixdigital.chargingtracker.R
import com.suffixdigital.chargingtracker.databinding.DialogMessagePopupBinding
import com.suffixdigital.chargingtracker.interfaces.MessagePopupClickListener
import com.suffixdigital.chargingtracker.utils.connecting_time
import com.suffixdigital.chargingtracker.utils.inputFilter
import com.suffixdigital.chargingtracker.utils.startCharging_time

/**
 * ============================= [MessagePopupDialog] =============================
 *
 * [MessagePopupDialog] is a common dialog for display error message or success message to user when Retrofit API return some messages for user.
 * This popup dialog is common for displaying message for all screen's Retrofit API's Response.
 * When user press on the 'OK' button the dialog will dismiss.
 *
 */
class MessagePopupDialog : DialogFragment(), View.OnClickListener {
    private lateinit var binding: DialogMessagePopupBinding

    /******************************************************************************
     * [onCreateDialog] is a override method of [DialogFragment]. This function is used to set dialog level property when Dialog create.
     *****************************************************************************/
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    /******************************************************************************
     * [onCreateView] is a override method of [DialogFragment]. This function is used to bind UI layout to this class so
     * [MessagePopupDialog] can use all UI views to this class.
     *****************************************************************************/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_message_popup, container, false)
        return binding.root
    }

    /******************************************************************************
     * [onViewCreated] is a override method of [DialogFragment]. Once UI layout bind with [MessagePopupDialog] popup  this method
     * allowed us to use all UI components here in UI thread.
     *****************************************************************************/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        //message to display in this popup screen
        binding.tvMessage.text = message
        binding.etConnectionTime.filters = arrayOf(inputFilter)
        binding.etStartChargingTime.filters = arrayOf(inputFilter)
        binding.etConnectionTime.setText(connecting_time.toString())
        binding.etStartChargingTime.setText(startCharging_time.toString())

        binding.btnSave.setOnClickListener(this)
    }

    /******************************************************************************
     * [onStart] is a override method of [DialogFragment]. When [MessagePopupDialog] is open this method is called.
     * Here in this method we can set property for dialog screen. like dialog height and width.
     *****************************************************************************/
    override fun onStart() {
        super.onStart()
        setDialogLayout()
    }

    /******************************************************************************
     * [setDialogLayout] function is set height and width for [MessagePopupDialog] popup screen.
     *****************************************************************************/
    private fun setDialogLayout() {
        if (dialog != null) {
            dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
    }

    /******************************************************************************
     * [companion object] is a singleton instance for [MessagePopupDialog] popup screen. This object is used to create singleton
     * instance and initialize all required variable. This is called first and once when [MessagePopupDialog] is open.
     *****************************************************************************/
    companion object {
        private var message: SpannableStringBuilder = SpannableStringBuilder()
        private var listener: MessagePopupClickListener? = null
        private var buttonText: String = ""
        val TAG: String? = MessagePopupDialog::class.simpleName
        private var INSTANCE: MessagePopupDialog? = null

        /******************************************************************************
         * [getInstance] is a static method of [MessagePopupDialog] popup screen. When this popup open from [BaseActivity] we need
         * to call this static method with required data which used to display in this popup dialog screen. Here 'static' method is
         * directly call with this call, no need to create instance of this class.
         * @param message: String -> message for popup screen which need to display
         * @param listener: -> listener is a callback for this popup dialog screen. When user click on button this listener will call
         * method in [baseActivity]
         * @return [MessagePopupDialog]
         *****************************************************************************/
        @JvmStatic
        fun getInstance(
            message: SpannableStringBuilder,
            buttonText: String,
            listener: MessagePopupClickListener?,
        ): MessagePopupDialog? {
            if (INSTANCE == null) {
                INSTANCE = MessagePopupDialog()
            }
            this.message = message
            this.listener = listener
            this.buttonText = buttonText
            return INSTANCE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            /******************************************************************************
             * When user press on the 'OK' button this popup dialog is dismiss and callback method is call from which this screen is open.
             *****************************************************************************/
            R.id.btn_save -> {
                connecting_time = binding.etConnectionTime.text.toString().toLong()
                startCharging_time = binding.etStartChargingTime.text.toString().toLong()
                //receiverIPAddress = binding.etIpAddress.text?.trim().toString()
                dialog?.dismiss()
            }
        }
    }
}