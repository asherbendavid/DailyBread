package cvc.dailybread

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import cvc.dailybread.createMySwordIntent
import cvc.dailybread.createYouVersionIntent
import kotlinx.coroutines.runBlocking

class NotificationWorker(private val context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        NotificationHelper.createChannel(applicationContext)

        val repo = SettingsRepository(context)
        val settings = runBlocking {
            repo.settingsFlow.first()
        }

        val now = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val message = "GOD is Good\n$now"
        val title = inputData.getString("title") ?: "Daily Inspiration"
        val id = inputData.getInt("id", 0)

        //NotificationHelper.showNotification(applicationContext, title, message, id)
        return Result.success()
    }
}
