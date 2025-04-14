package com.suffixdigital.chargingtracker.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.suffixdigital.chargingtracker.R
import com.suffixdigital.chargingtracker.databinding.DashboardHomeBinding
import com.suffixdigital.chargingtracker.ui.activity.ChargingStationConnectionScreen

class DashboardFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: DashboardHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = DashboardHomeBinding.inflate(inflater, container, false)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAvailableStation1.setOnClickListener(this)
        binding.btnAvailableStation2.setOnClickListener(this)
        binding.btnAvailableStation3.setOnClickListener(this)
        binding.btnAvailableStation4.setOnClickListener(this)
        binding.btnAvailableStation5.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_available_station_1 -> {
                startChargingConnectionScreen(5)
            }

            R.id.btn_available_station_2 -> {
                startChargingConnectionScreen(2)
            }

            R.id.btn_available_station_3 -> {
                startChargingConnectionScreen(3)
            }

            R.id.btn_available_station_4 -> {
                startChargingConnectionScreen(4)
            }

            R.id.btn_available_station_5 -> {
                startChargingConnectionScreen(1)
            }
        }
    }

    private fun startChargingConnectionScreen(id: Int) {
        val intent = Intent(context, ChargingStationConnectionScreen::class.java)
        intent.putExtra("ID", id)
        startActivity(intent)
    }
}