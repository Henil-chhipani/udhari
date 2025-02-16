package com.example.udhari.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import java.util.Locale

class SpeechRecognitionHelper(
    private val context: Context,
    private var onCommandRecognized: (String) -> Unit
) {
    private val speechRecognizer: SpeechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(context)

    private val speechIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a command to manage notebooks")
    }


    fun updateHandler(newHandler: (String) -> Unit) {
        onCommandRecognized = newHandler
    }

    fun recognizeCommand(command: String) {
        onCommandRecognized(command)
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {
            stopListening()  // Ensure it stops properly when speech ends
        }

        override fun onError(error: Int) {
            Toast.makeText(context, "Error recognizing speech: $error", Toast.LENGTH_SHORT).show()
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.firstOrNull()?.let { onCommandRecognized(it) }
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    init {
        speechRecognizer.setRecognitionListener(recognitionListener) // Set once
    }

    fun startListening() {
        speechRecognizer.startListening(speechIntent)
    }

    fun stopListening() {

        speechRecognizer?.stopListening()
    }

    fun destroy() {
        speechRecognizer.destroy()
    }
}
