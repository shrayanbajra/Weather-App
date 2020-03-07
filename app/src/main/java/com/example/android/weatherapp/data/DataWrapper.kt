package com.example.android.weatherapp.data

data class DataWrapper<T>(
    var status: Boolean = false,
    var message: String = "",
    var wrapperBody: T? = null
) {
    fun prepareSuccess(successMessage: String, body: T) {
        status = true
        message = successMessage
        wrapperBody = body
    }

    fun prepareFailure(failureMessage: String) {
        status = false
        message = failureMessage
    }

    fun wasSuccessful(): Boolean {
        return status
    }

    fun wasFailure(): Boolean {
        return !status
    }
}