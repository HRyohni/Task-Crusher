package hr.fipu.organizationtool.data

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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding_prefs")

class OnboardingRepository(private val context: Context) {

    private object PreferencesKeys {
        val HAS_SEEN_BACK_TAP_GUIDE = booleanPreferencesKey("has_seen_back_tap_guide")
    }

    val hasSeenBackTapGuide: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.HAS_SEEN_BACK_TAP_GUIDE] ?: false
        }

    suspend fun setHasSeenBackTapGuide(seen: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_SEEN_BACK_TAP_GUIDE] = seen
        }
    }
}
