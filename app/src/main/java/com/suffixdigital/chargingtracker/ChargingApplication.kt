package com.suffixdigital.chargingtracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by Kirtikant Patadiya on 2025-01-12.
 */

@HiltAndroidApp
class ChargingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }

}