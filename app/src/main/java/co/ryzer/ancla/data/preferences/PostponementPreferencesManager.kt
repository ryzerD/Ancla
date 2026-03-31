package co.ryzer.ancla.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val POSTPONEMENT_PREFERENCES_NAME = "postponement_preferences"
private val Context.postponementDataStore: DataStore<Preferences> by preferencesDataStore(
    name = POSTPONEMENT_PREFERENCES_NAME
)

object PostponementPreferencesManager {
    private val POSTPONEMENT_MINUTES_KEY = longPreferencesKey("postponement_minutes")

    fun getPostponementMinutesFlow(context: Context): Flow<Long> {
        return context.postponementDataStore.data.map { preferences ->
            preferences[POSTPONEMENT_MINUTES_KEY] ?: 0L
        }
    }

    suspend fun savePostponementMinutes(context: Context, minutes: Long) {
        context.postponementDataStore.edit { preferences ->
            preferences[POSTPONEMENT_MINUTES_KEY] = minutes.coerceAtLeast(0L)
        }
    }

    suspend fun clearPostponement(context: Context) {
        context.postponementDataStore.edit { preferences ->
            preferences[POSTPONEMENT_MINUTES_KEY] = 0L
        }
    }
}

