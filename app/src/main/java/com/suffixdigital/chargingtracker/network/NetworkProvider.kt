package com.suffixdigital.chargingtracker.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI

/**
 * **[NetworkProvider]** class will help to check internet connectivity.
 * This class will check internet type like wifi or cellular or ethernet.
 * When we use Retrofit for API calling to get or post data from/to cloud we need to check does android
 * device has internet connectivity are available or not. If internet connectivity not available then not
 * able to call Retrofit API.
 */

object NetworkProvider {
    fun hasInternetConnection(context: Context?): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(TRANSPORT_WIFI) -> true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}