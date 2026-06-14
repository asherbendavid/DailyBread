package cvc.dailybread

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime

val Context.settingsDataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val MORNING_ENABLED = booleanPreferencesKey("morning_enabled")
    val MORNING_TIME = stringPreferencesKey("morning_time")
    val EVENING_ENABLED = booleanPreferencesKey("evening_enabled")
    val EVENING_TIME = stringPreferencesKey("evening_time")
    val ACTION_OPTION = stringPreferencesKey("notification_action")
}

class SettingsRepository(private val context: Context) {

    val settingsFlow: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            morningEnabled = prefs[SettingsKeys.MORNING_ENABLED] ?: true,
            morningTime = prefs[SettingsKeys.MORNING_TIME]?.let { LocalTime.parse(it) } ?: LocalTime.of(7, 30),
            eveningEnabled = prefs[SettingsKeys.EVENING_ENABLED] ?: false,
            eveningTime = prefs[SettingsKeys.EVENING_TIME]?.let { LocalTime.parse(it) } ?: LocalTime.of(19, 0),
            actionOption = prefs[SettingsKeys.ACTION_OPTION]?.let {
                NotificationActionOption.valueOf(it)
            } ?: NotificationActionOption.NONE
        )
    }

    suspend fun saveSettings(settings: AppSettings) {
        context.settingsDataStore.edit { prefs ->
            prefs[SettingsKeys.MORNING_ENABLED] = settings.morningEnabled
            prefs[SettingsKeys.MORNING_TIME] = settings.morningTime.toString()
            prefs[SettingsKeys.EVENING_ENABLED] = settings.eveningEnabled
            prefs[SettingsKeys.EVENING_TIME] = settings.eveningTime.toString()
            prefs[SettingsKeys.ACTION_OPTION] = settings.actionOption.name
        }
    }
}
