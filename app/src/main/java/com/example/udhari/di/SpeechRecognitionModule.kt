package com.example.udhari.di

import android.content.Context
import com.example.udhari.utils.SpeechRecognitionHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object SpeechRecognitionModule {

    @Provides
    @Singleton
    fun provideSpeechRecognitionHelper(
        @ApplicationContext context: Context
    ): SpeechRecognitionHelper {
        return SpeechRecognitionHelper(
            context = context,
            onCommandRecognized = { command ->

            }
        )
    }
}
