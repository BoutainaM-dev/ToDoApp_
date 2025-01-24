package com.boutaina.todo.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDatastore by preferencesDataStore("user")

class TokenRepository(private val context: Context) {

    private val tokenKey = stringPreferencesKey("token")

    val tokenFlow: Flow<String?> = context.userDatastore.data
        .map { preferences ->
            preferences[tokenKey]
        }

    suspend fun store(token: String) {
        context.userDatastore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }


    suspend fun clear() {
        context.userDatastore.edit { preferences ->
            preferences.remove(tokenKey)
        }
    }
}
