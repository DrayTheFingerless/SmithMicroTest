package com.ferreirarobert.smithmicrotest.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        val USER = stringPreferencesKey("username")
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[USER] = username
        }
    }

    fun getUsername(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[USER] ?: ""
        }
    }
}