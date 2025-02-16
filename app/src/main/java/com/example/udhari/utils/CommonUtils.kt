package com.example.udhari.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun ViewModel.elseCaseHandling(message: String, onEvent: () -> Unit) {
    viewModelScope.launch {
        ToastManager.showToast(message = message, isSuccess = false)
        delay(900)
        onEvent() // Execute the event dynamically
    }
}