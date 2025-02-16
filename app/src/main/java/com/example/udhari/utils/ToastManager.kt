package com.example.udhari.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object ToastManager {
    private val _toastMessage = MutableStateFlow("")
    val toastMessage: StateFlow<String> = _toastMessage

    private val _showMessage = MutableStateFlow(false)
    val showMessage: StateFlow<Boolean> = _showMessage

    private val _isSuccess = MutableStateFlow(true)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    fun showToast(message: String, isSuccess: Boolean) {
        _toastMessage.update { message }
        _isSuccess.update { isSuccess }
        _showMessage.update { true }
    }

    fun hideToast() {
        _showMessage.update { false }
        _toastMessage.update { "" } // Optional: Reset message to avoid flickering
    }
}
