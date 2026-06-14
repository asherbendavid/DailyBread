package cvc.dailybread

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

fun scheduleVerseNotifications(context: Context) {
    val repo = SettingsRepository(context)
    val settings = runBlocking {
        repo.settingsFlow.first()
    }

    // Cancel any existing scheduled work first
    WorkManager.getInstance(context).cancelUniqueWork("morningVerse")
    WorkManager.getInstance(context).cancelUniqueWork("eveningVerse")

    // Schedule morning notification if enabled
    if (settings.morningEnabled) {
        scheduleDailyWorker(context, settings.morningTime, true)
    }

    // Schedule evening notification if enabled
    if (settings.eveningEnabled) {
        scheduleDailyWorker(context, settings.eveningTime, false)
    }
}

private fun scheduleDailyWorker(context: Context, time: LocalTime, isMorning: Boolean) {
    val now = LocalDateTime.now()
    var triggerTime = now.withHour(time.hour).withMinute(time.minute).withSecond(0).withNano(0)

    // If the time has already passed today, schedule for tomorrow
    if (triggerTime.isBefore(now)) {
        triggerTime = triggerTime.plusDays(1)
    }

    val delay = Duration.between(now, triggerTime).toMillis()

    val data = Data.Builder()
        .putBoolean("isMorning", isMorning)
        .build()

    val request = OneTimeWorkRequestBuilder<VerseNotificationWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .addTag(if (isMorning) "morningVerse" else "eveningVerse")
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        if (isMorning) "morningVerse" else "eveningVerse",
        ExistingWorkPolicy.REPLACE,
        request
    )
}