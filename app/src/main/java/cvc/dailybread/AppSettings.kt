package cvc.dailybread

import java.time.LocalTime

data class AppSettings(
    val morningEnabled: Boolean,
    val morningTime: LocalTime,
    val eveningEnabled: Boolean,
    val eveningTime: LocalTime,
    val actionOption: cvc.dailybread.NotificationActionOption
)