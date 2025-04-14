package com.suffixdigital.chargingtracker.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.suffixdigital.chargingtracker.data.UpdateChargingStatusResponse
import com.suffixdigital.chargingtracker.network.NetworkProvider.hasInternetConnection
import com.suffixdigital.chargingtracker.repository.ApiRepository
import com.suffixdigital.chargingtracker.utils.Event
import com.suffixdigital.chargingtracker.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * Created by Kirtikant Patadiya on 2025-01-12.
 */
@HiltViewModel
class ChargingAppViewModel @Inject constructor(
    application: Application,
    private val apiRepository: ApiRepository
) : AndroidViewModel(application) {
    private val statusUpdateResponse =
        MutableLiveData<Event<Resource<UpdateChargingStatusResponse>>>()


    fun doUpdateChargingStatus(context: Context?, id: Int, status: String) = viewModelScope.launch {
        try {
            if (hasInternetConnection(context)) {
                apiRepository.updateChargingStatus(id, status)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> statusUpdateResponse.postValue(
                    Event(
                        Resource.Error(
                            500,
                            t.message.toString()
                        )
                    )
                )

                else -> statusUpdateResponse.postValue(
                    Event(Resource.Error(500, "Something went wrong."))
                )
            }
        }
    }
}