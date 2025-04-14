package com.suffixdigital.chargingtracker.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.suffixdigital.chargingtracker.data.StationData
import com.suffixdigital.chargingtracker.databinding.FragmentFindChargingStationBinding
import com.suffixdigital.chargingtracker.ui.activity.ChargingStationConnectionScreen
import com.suffixdigital.chargingtracker.ui.adapter.ChargingStationAdapter
import com.suffixdigital.chargingtracker.utils.getChargingStationList

class FindChargingStationFragment : Fragment() {

    private lateinit var callback: OnBackPressedCallback
    private lateinit var binding: FragmentFindChargingStationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = FragmentFindChargingStationBinding.inflate(inflater, container, false)
        }

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back press event here
                // e.g., navigate to the previous fragment or finish the activity
                findNavController().popBackStack() // Navigate back
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }


    override fun onDestroy() {
        super.onDestroy()
        callback.remove()
    }


    private fun initViews() {
        binding.rvStationList.setHasFixedSize(true)
        binding.rvStationList.layoutManager = LinearLayoutManager(context)
        val chargingStationAdapter = ChargingStationAdapter(getChargingStationList(),
            object : ChargingStationItemSelectionListener {
                override fun onItemSelected(stationData: StationData) {
                    startChargingConnectionScreen(stationData.stationID)
                }

            })
        binding.rvStationList.adapter = chargingStationAdapter

        binding.btnCancel.setOnClickListener {
            callback.handleOnBackPressed()
        }
    }


    private fun startChargingConnectionScreen(id: Int) {
        val intent = Intent(context, ChargingStationConnectionScreen::class.java)
        intent.putExtra("ID", id)
        startActivity(intent)
    }

    interface ChargingStationItemSelectionListener {
        fun onItemSelected(stationData: StationData)
    }
}