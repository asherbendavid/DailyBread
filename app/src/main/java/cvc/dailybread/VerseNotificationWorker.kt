package cvc.dailybread

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import cvc.dailybread.NotificationHelper.sendVerseNotification
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class VerseNotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {


    override fun doWork(): Result {
        val isMorning = inputData.getBoolean("isMorning", true)

        // Send the notification
        sendVerseNotification(applicationContext, isMorning)

        // Reschedule for tomorrow at the same time
        rescheduleForTomorrow(isMorning)

        return Result.success()
    }

    private fun rescheduleForTomorrow(isMorning: Boolean) {
        val repo = SettingsRepository(applicationContext)
        val settings = runBlocking {
            repo.settingsFlow.first()
        }

        // Check if this notification type is still enabled
        val isEnabled = if (isMorning) settings.morningEnabled else settings.eveningEnabled
        if (!isEnabled) {
            return // Don't reschedule if disabled
        }

        val time = if (isMorning) settings.morningTime else settings.eveningTime

        // Schedule for exactly 24 hours from now
        val now = LocalDateTime.now()
        val tomorrow = now.plusDays(1)
            .withHour(time.hour)
            .withMinute(time.minute)
            .withSecond(0)
            .withNano(0)

        val delay = Duration.between(now, tomorrow).toMillis()

        val data = Data.Builder()
            .putBoolean("isMorning", isMorning)
            .build()

        val request = OneTimeWorkRequestBuilder<VerseNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(if (isMorning) "morningVerse" else "eveningVerse")
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            if (isMorning) "morningVerse" else "eveningVerse",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}