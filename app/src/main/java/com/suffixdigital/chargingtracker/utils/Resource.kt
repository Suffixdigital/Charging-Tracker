package com.suffixdigital.chargingtracker.utils

sealed class Resource<T>(
    val responseCode: Int? = null,
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(responseCode: Int, data: T) : Resource<T>(responseCode, data)
    class Error<T>(responseCode: Int, message: String, data: T? = null) :
        Resource<T>(responseCode, data, message)

    class Loading<T> : Resource<T>()
}
