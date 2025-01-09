package com.example.udhari.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException



class PreferenceDataStore( private val dataStore: DataStore<Preferences>) {
    companion object {
        val NOTEBOOK_ID = intPreferencesKey("noteBookId")
    }

    private val dataStoreFlow = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }

    val noteBookId: Flow<Int> =
        dataStoreFlow.map { preferences -> preferences[NOTEBOOK_ID] ?: 100 }.distinctUntilChanged()

    suspend fun saveNoteBookId(id: Int) {
        dataStore.edit { preferences ->
            preferences[NOTEBOOK_ID] = id
        }
    }
}