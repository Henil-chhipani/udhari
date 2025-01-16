package com.example.udhari.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException


class PreferenceDataStore(private val dataStore: DataStore<Preferences>) {
    companion object {
        val NOTEBOOK_ID = intPreferencesKey("noteBookId")
    }

    private val dataStoreFlow = dataStore.data

    val noteBookId: Flow<Int> =
        dataStoreFlow.map { preferences -> preferences[NOTEBOOK_ID] ?: 1 }.distinctUntilChanged()

    suspend fun saveNoteBookId(id: Int) = withContext(Dispatchers.IO) {
        dataStore.edit { preferences ->
            preferences[NOTEBOOK_ID] = id
        }
    }
}