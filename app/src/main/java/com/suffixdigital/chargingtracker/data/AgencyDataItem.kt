package com.suffixdigital.chargingtracker.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Kirtikant Patadiya on 2024-12-11.
 */
data class AgencyDataItem(
    /*Agency Number of Agency*/
    @field:SerializedName("agencyNumber") val agencyNumber: Int = 0,

    /*Agency's Short Name*/
    @field:SerializedName("agencyShortName") val agencyShortName: String? = null,

    /*Agency's public url*/
    @field:SerializedName("agencyPublicURL") val agencyPublicURL: String? = null,

    /*Agency Name*/
    @field:SerializedName("agencyName") val agencyName: String = "",

    /*Agency's phone number*/
    @field:SerializedName("agencyPhone") val agencyPhone: String = ""
)
