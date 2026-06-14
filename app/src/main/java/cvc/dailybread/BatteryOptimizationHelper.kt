package cvc.dailybread

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

object BatteryOptimizationHelper {

    /**
     * Check if the app is excluded from battery optimization
     * @return true if NOT optimized (good!), false if optimized (bad!)
     */
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return powerManager.isIgnoringBatteryOptimizations(context.packageName)
        }
        // Android 5 and below don't have battery optimization
        return true
    }

    /**
     * Open the battery optimization settings for this app
     */
    fun openBatteryOptimizationSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback to general battery optimization list
                try {
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    context.startActivity(intent)
                } catch (e2: Exception) {
                    e2.printStackTrace()
                }
            }
        }
    }

    /**
     * Get manufacturer-specific advice URL
     */
    fun getManufacturerAdviceUrl(context: Context): String {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return when {
            manufacturer.contains("huawei") -> "https://dontkillmyapp.com/huawei"
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") -> "https://dontkillmyapp.com/xiaomi"
            manufacturer.contains("samsung") -> "https://dontkillmyapp.com/samsung"
            manufacturer.contains("oneplus") -> "https://dontkillmyapp.com/oneplus"
            manufacturer.contains("oppo") -> "https://dontkillmyapp.com/oppo"
            manufacturer.contains("realme") -> "https://dontkillmyapp.com/realme"
            manufacturer.contains("vivo") -> "https://dontkillmyapp.com/vivo"
            manufacturer.contains("asus") -> "https://dontkillmyapp.com/asus"
            manufacturer.contains("nokia") -> "https://dontkillmyapp.com/nokia"
            manufacturer.contains("meizu") -> "https://dontkillmyapp.com/meizu"
            else -> "https://dontkillmyapp.com"
        }
    }

    /**
     * Check if this device is known to be aggressive with battery optimization
     */
    fun isAggressiveManufacturer(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return manufacturer.contains("huawei") ||
                manufacturer.contains("xiaomi") ||
                manufacturer.contains("redmi") ||
                manufacturer.contains("oneplus") ||
                manufacturer.contains("oppo") ||
                manufacturer.contains("realme") ||
                manufacturer.contains("samsung")
    }
}