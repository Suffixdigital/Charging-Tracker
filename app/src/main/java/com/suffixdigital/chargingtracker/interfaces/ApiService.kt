package com.suffixdigital.chargingtracker.interfaces

import com.suffixdigital.chargingtracker.data.UpdateChargingStatusResponse
import retrofit2.Response
import retrofit2.http.*

/******************************************************************************
 * API list which used in UseTransit Android App to communicate with TF Cloud
 *****************************************************************************/
interface ApiService {
    @GET("/data.php")
    suspend fun updateChargingStatus(
        @Query("id") id: Int,
        @Query("status") status: String,
    ): Response<UpdateChargingStatusResponse>
}