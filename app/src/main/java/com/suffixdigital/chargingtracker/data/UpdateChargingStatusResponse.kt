package com.suffixdigital.chargingtracker.data

import com.google.gson.annotations.SerializedName

data class UpdateChargingStatusResponse(

    @field:SerializedName("message") val message: String = "",

    @field:SerializedName("status") val status: String = ""
)
