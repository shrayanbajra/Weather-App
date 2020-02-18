package com.example.android.weatherapp.data.remote

// Response Wrapper - Wraps result from a network request
data class ResponseWrapper<O>(
    private var isSuccessful: Boolean = false,
    private var errorMessage: String = "",
    private var responseBody: O? = null
) {

    fun prepareSuccess(message: String, body: O?) {
        apply {
            isSuccessful = true
            errorMessage = message
            responseBody = body
        }
    }

    fun prepareFailure(message: String, body: O?) {
        apply {
            isSuccessful = false
            errorMessage = message
            responseBody = body
        }
    }

    fun getResponse(): O? {
        return responseBody
    }

    fun setErroMessage(message: String) {
        errorMessage = message
    }

    fun isNotSuccessful(): Boolean {
        return !isSuccessful || responseBody == null
    }
}