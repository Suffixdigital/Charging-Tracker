package com.suffixdigital.chargingtracker.ui.adapter

import android.content.res.ColorStateList
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.suffixdigital.chargingtracker.R
import com.suffixdigital.chargingtracker.data.StationData
import com.suffixdigital.chargingtracker.databinding.ChargingStationItemBinding
import com.suffixdigital.chargingtracker.ui.fragment.FindChargingStationFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Kirtikant Patadiya on 2024-11-17.
 */
class ChargingStationAdapter(
    private var chargingStationList: MutableList<StationData>,
    private val listener: FindChargingStationFragment.ChargingStationItemSelectionListener
) :
    RecyclerView.Adapter<ChargingStationAdapter.ChargingStationViewHolder>() {


    inner class ChargingStationViewHolder(var binding: ChargingStationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChargingStationViewHolder {

        return ChargingStationViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.charging_station_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ChargingStationAdapter.ChargingStationViewHolder,
        position: Int
    ) {
        val chargingStation = chargingStationList[position]

        holder.binding.tvStationName.text = chargingStation.stationName
        holder.binding.tvStationAddress.text = chargingStation.stationAddress

        holder.binding.tvAvailableChargers.text =
            SpannableStringBuilder().append("Available Chargers: ")
                .color(ContextCompat.getColor(holder.itemView.context, R.color.green_3EC94A)) {
                    append("3")
                }
                .append("/5")
        holder.binding.tvAmenities.text = SpannableStringBuilder().bold { append("Amenities") }
            .append(" : Cafe, Restrooms, Wi-Fi, Nearby Parking")


        if (chargingStation.status.equals("busy", true)) {
            holder.binding.ivStatus.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.busy_status)
            holder.binding.btnBookSlot.text = "Booked"
            holder.binding.btnBookSlot.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.orange_EA8A45
                )
            )

        } else {
            holder.binding.ivStatus.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.open)
            holder.binding.btnBookSlot.text = "Book Your Slot"
            holder.binding.btnBookSlot.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.blue_3970FF
                )
            )

        }

        holder.binding.btnBookSlot.setOnClickListener {
            if (chargingStation.status.equals("open", true)) {
                chargingStation.status = "busy"
                CoroutineScope(Dispatchers.Main).launch {
                    holder.binding.btnBookSlot.isClickable = false
                    holder.binding.progressCircular.visibility = View.VISIBLE
                    delay(1500)
                    holder.binding.progressCircular.visibility = View.INVISIBLE
                    holder.binding.btnBookSlot.isClickable = true
                    notifyDataSetChanged()
                }
            }
        }

        holder.binding.btnPaymentSummary.setOnClickListener {
            listener.onItemSelected(chargingStation)
        }


    }


    override fun getItemCount(): Int {
        return chargingStationList.size

    }

}