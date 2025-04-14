package com.suffixdigital.chargingtracker.custombottomnavigation

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class Model(
    @DrawableRes
    val icon: Int = 0,
    @IdRes
    val destinationId: Int = -1,
    val id: Int = -1,
    @StringRes
    val text: Int = 0,
)