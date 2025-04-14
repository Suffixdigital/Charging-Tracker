package com.suffixdigital.chargingtracker.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.suffixdigital.chargingtracker.BuildConfig
import com.suffixdigital.chargingtracker.R
import com.suffixdigital.chargingtracker.databinding.ChargingSummaryScreenBinding
import com.suffixdigital.chargingtracker.utils.battery_percentage
import com.suffixdigital.chargingtracker.utils.power_consumed
import com.suffixdigital.chargingtracker.utils.total_amount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChargingSummaryScreen : AppCompatActivity() {
    private lateinit var binding: ChargingSummaryScreenBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.charging_summary_screen)
        val batteryPercentage = "battery_$battery_percentage"
        val drawableId =
            resources.getIdentifier(batteryPercentage, "drawable", BuildConfig.APPLICATION_ID)
        binding.ivBattery.setImageResource(drawableId)
        binding.tvBatteryPercentage.text = "${battery_percentage}%"
        binding.tvPower.text = power_consumed
        binding.tvAmount.text = total_amount

        binding.btnPayNow.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                binding.btnPayNow.isEnabled = false
                binding.animationView.visibility = View.VISIBLE
                delay(2500)
                binding.animationView.visibility = View.GONE
                binding.btnPayNow.isEnabled = true
                startActivity(Intent(this@ChargingSummaryScreen, PaymentSummaryScreen::class.java))
            }
        }
    }
}