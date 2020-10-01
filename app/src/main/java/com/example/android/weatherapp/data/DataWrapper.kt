package com.example.android.weatherapp.data

data class DataWrapper<T>(
    var status: Boolean = false,
    var message: String = "",
    var wrapperBody: T? = null
) {
    fun prepareSuccess(successMessage: String, body: T): DataWrapper<T> {
        status = true
        message = successMessage
        wrapperBody = body
        return this
    }

    fun prepareFailure(failureMessage: String): DataWrapper<T> {
        status = false
        message = failureMessage
        return this
    }

    fun wasSuccessful(): Boolean {
        return status
    }

    fun wasFailure(): Boolean {
        return !status
    }
}