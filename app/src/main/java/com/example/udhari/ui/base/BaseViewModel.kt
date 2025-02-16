package com.example.udhari.ui.base

import androidx.lifecycle.ViewModel
import com.example.udhari.utils.SpeechRecognitionHelper
import com.example.udhari.utils.ToastManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


abstract class BaseViewModel(
    private val speechRecognitionHelper: SpeechRecognitionHelper
) : ViewModel() {
    private val _isVoiceRecognitionActive = MutableStateFlow(false)
    val isVoiceRecognitionActive = _isVoiceRecognitionActive.asStateFlow()

    init {
        speechRecognitionHelper.updateHandler { command ->
            handleVoiceCommand(command)
        }
    }


    protected open fun startVoiceRecognition() {
        if(_isVoiceRecognitionActive.value) {
            stopVoiceRecognition() //stop first to prevent busy errors
        }
        speechRecognitionHelper.startListening()
        _isVoiceRecognitionActive.value = true;
        ToastManager.showToast("Listening...", true)

    }

    protected open fun stopVoiceRecognition() {
        speechRecognitionHelper.stopListening()
        _isVoiceRecognitionActive.value = false;
        ToastManager.showToast("Stopped Listening", false)
    }

    protected open fun handleVoiceCommand(command: String) {
        // Handle voice commands here
    }

    override fun onCleared() {
        super.onCleared()
    }
}