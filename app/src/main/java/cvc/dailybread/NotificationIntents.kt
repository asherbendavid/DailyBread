package cvc.dailybread

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri


fun createDailyBreadIntent(context: Context): PendingIntent {
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    return PendingIntent.getActivity(context, 2, intent, PendingIntent.FLAG_IMMUTABLE)
}

fun createMySwordIntent(context: Context, verseRef: String): PendingIntent {
    val intent = Intent().apply {
        component = ComponentName(
            "com.riversoft.android.mysword",
            "com.riversoft.android.mysword.MySwordLink"
        )
        data = Uri.parse("https://mysword.info/b?r=$verseRef")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)
}

fun createYouVersionIntent(context: Context, verseRef: String): PendingIntent {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("youversion://bible?reference=$verseRef"))
    return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
}