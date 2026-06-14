package cvc.dailybread

import android.content.Context
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object VerseRepository {
    private var cachedJson: JSONObject? = null

    private fun loadJson(context: Context): JSONObject {
        if (cachedJson == null) {
            val jsonString = context.assets.open("Gideons.json").bufferedReader().use { it.readText() }
            cachedJson = JSONObject(jsonString)
        }
        return cachedJson!!
    }

    private fun getKeyForDate(date: LocalDate): String {
        val baseKey = date.format(DateTimeFormatter.ofPattern("MM-dd"))
        return when (baseKey) {
            "02-28", "03-01" -> baseKey + if (date.isLeapYear) "L" else "N"
            else -> baseKey
        }
    }

    fun getMorningVerse(context: Context, date: LocalDate = LocalDate.now()): VerseData {
        val json = loadJson(context)
        val key = getKeyForDate(date)
        val morning = json.getJSONObject(key).getJSONObject("morning")
        return VerseData(
            youVersion = morning.getString("youVersion"),
            mySword = morning.getString("mySword"),
            info = morning.optString("info", "")
        )
    }

    fun getEveningVerse(context: Context, date: LocalDate = LocalDate.now()): VerseData {
        val json = loadJson(context)
        val key = getKeyForDate(date)
        val evening = json.getJSONObject(key).getJSONObject("evening")
        return VerseData(
            youVersion = evening.getString("youVersion"),
            mySword = evening.getString("mySword"),
            info = evening.optString("info", "")
        )
    }

    data class VerseData(
        val youVersion: String,
        val mySword: String,
        val info: String
    )
}