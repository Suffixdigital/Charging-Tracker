package com.suffixdigital.chargingtracker.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.suffixdigital.chargingtracker.R
import com.suffixdigital.chargingtracker.databinding.ActivityPaymentSummaryScreenBinding
import com.suffixdigital.chargingtracker.utils.chargingID
import com.suffixdigital.chargingtracker.utils.endTime
import com.suffixdigital.chargingtracker.utils.getChargingStationList
import com.suffixdigital.chargingtracker.utils.power_consumed
import com.suffixdigital.chargingtracker.utils.startTime
import com.suffixdigital.chargingtracker.utils.totalChargingTime
import com.suffixdigital.chargingtracker.utils.total_amount
import java.util.Locale
import java.util.concurrent.TimeUnit.HOURS
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.MINUTES

class PaymentSummaryScreen : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentSummaryScreenBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_summary_screen)
//₹375 (25 kwh x ₹4/kwh)
        binding.tvStartTime.text = startTime
        val stationData = getChargingStationList()[chargingID!! - 1]
        binding.tvStationName.text = stationData.stationName
        binding.tvStationAddress.text = stationData.stationAddress
        binding.tvEndTime.text = endTime
        binding.tvChargingDuration.text = hmsTimeFormatter(totalChargingTime.toLong())

        binding.tvPower.text = power_consumed
        binding.tvTotal.text = total_amount
        binding.tvTotalAmount.text = "$total_amount ($power_consumed x ₹4/kwh)"
        val message =
            "Thank you for using ${stationData.stationName}. \nWe hope to serve you again soon!"
        binding.tvThankYouMessage.text = message
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this@PaymentSummaryScreen, ChargingMainScreen::class.java))
            finish()
        }
    }

    private fun hmsTimeFormatter(milliSeconds: Long): String {
        val hours = 0L
        val minutes = MILLISECONDS.toMinutes(milliSeconds) % HOURS.toMinutes(1)
        val seconds = MILLISECONDS.toSeconds(milliSeconds) % MINUTES.toSeconds(1)
        val minutesLabel = if (minutes > 1) "minutes" else "minute"
        return String.format(
            Locale.getDefault(),
            "%02d $minutesLabel %02d seconds",
            minutes,
            seconds
        )
    }

}