package cvc.dailybread

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cvc.dailybread.VerseRepository.getEveningVerse
import cvc.dailybread.VerseRepository.getMorningVerse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object NotificationHelper {
    private const val CHANNEL_ID = "daily_inspiration_channel"
    private const val TAG = "NotificationHelper"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Inspiration"
            val descriptionText = "Morning and evening inspirational quotes"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }

    fun sendVerseNotification(context: Context, isMorning: Boolean) {
        // Check permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "POST_NOTIFICATIONS permission not granted")
                return
            }
        }

        val verse = if (isMorning) getMorningVerse(context) else getEveningVerse(context)

        val settings = runBlocking {
            SettingsRepository(context).settingsFlow.first()
        }

        val launchIntent: PendingIntent? = when (settings.actionOption) {
            NotificationActionOption.YOUVERSION -> createYouVersionIntent(context, verse.youVersion)
            NotificationActionOption.MYSWORD -> createMySwordIntent(context, verse.mySword)
            NotificationActionOption.NONE -> createDailyBreadIntent(context)
            NotificationActionOption.BOTH -> null
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(if (isMorning) "Morning Inspiration" else "Evening Reflection")
            .setContentText("Your verse for ${if (isMorning) "this morning" else "tonight"} is ${verse.info}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (launchIntent != null) {
            builder.setContentIntent(launchIntent)
        } else {
            builder.addAction(
                R.drawable.ic_youversion,
                "YouVersion",
                createYouVersionIntent(context, verse.youVersion)
            )
            builder.addAction(
                R.drawable.ic_mysword,
                "MySword",
                createMySwordIntent(context, verse.mySword)
            )
        }

        try {
            NotificationManagerCompat.from(context).notify(
                if (isMorning) 1001 else 1002,
                builder.build()
            )
            Log.d(TAG, "Notification sent: ${if (isMorning) "Morning" else "Evening"}")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when posting notification", e)
        }
    }
}