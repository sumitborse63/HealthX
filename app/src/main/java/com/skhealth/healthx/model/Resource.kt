package com.skhealth.healthx.model

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Loading<T> : Resource<T>() // No data or message needed for Loading

    class Success<T>(data: T) : Resource<T>(data = data) // Pass data to the superclass

    class Error<T>(message: String) : Resource<T>(message = message) // Pass message to the superclass
}
