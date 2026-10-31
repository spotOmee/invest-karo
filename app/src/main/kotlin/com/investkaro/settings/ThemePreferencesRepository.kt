package com.investkaro.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.investKaroDataStore: DataStore<Preferences> by preferencesDataStore(name = "invest_karo_prefs")

/**
 * Local-first persistence repository for user preferences such as Dark/Light theme.
 */
class ThemePreferencesRepository(private val context: Context) {

    private val darkThemeKey = booleanPreferencesKey("pref_dark_theme")

    val isDarkThemeFlow: Flow<Boolean?> = context.investKaroDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[darkThemeKey]
        }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.investKaroDataStore.edit { preferences ->
            preferences[darkThemeKey] = isDark
        }
    }
}
