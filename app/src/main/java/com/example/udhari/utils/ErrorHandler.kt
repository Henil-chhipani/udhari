package com.example.udhari.utils

import android.util.Log

object ErrorHandler {
    fun handleError(e: Throwable) {
        Log.e("ErrorHandler", "An error occurred", e)
        // Optionally trigger an event to show an error message to the user
    }
}
