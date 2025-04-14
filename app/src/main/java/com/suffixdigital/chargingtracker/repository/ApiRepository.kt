package com.suffixdigital.chargingtracker.repository

import com.suffixdigital.chargingtracker.interfaces.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Kirtikant Patadiya on 2025-01-12.
 */
@Singleton
class ApiRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun updateChargingStatus(id: Int, status: String) =
        apiService.updateChargingStatus(id, status)
}