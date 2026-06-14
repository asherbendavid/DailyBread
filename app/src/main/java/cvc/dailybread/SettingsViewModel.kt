package cvc.dailybread

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = SettingsRepository(application)

    val settings: StateFlow<AppSettings> = repo.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings(
            morningEnabled = true,
            morningTime = LocalTime.of(7, 30),
            eveningEnabled = false,
            eveningTime = LocalTime.of(19, 0),
            actionOption = NotificationActionOption.NONE
        ))

    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            repo.saveSettings(newSettings)
        }
    }
}
