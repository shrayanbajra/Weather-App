package com.example.android.weatherapp.ui

data class DataWrapper(
    private var status: Boolean = false,
    private var message: String = ""
) {

    fun prepareSuccess(successMessage: String) {
        status = true
        message = successMessage
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

    fun getMessage(): String {
        return message
    }
}