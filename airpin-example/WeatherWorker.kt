package example.workmanager.airpin

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.airpin.R
import com.example.airpin.api.WeatherApiClient

import com.example.airpin.MainActivity
import com.example.airpin.MyApplication


class WeatherWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("WeatherWorker", "Worker started")

        val app = applicationContext as MyApplication
        val pins = app.pins

        if (app.pins.isEmpty()) {
            Log.d("WeatherWortker", "No pins saved - skipping weather check.")
            return Result.success()
        }

        return try {
            for (pin in pins) {

                // Fetch current weather
                val response = WeatherApiClient.service.getWeather(pin.latitude, pin.longitude)
                val newTemp = response.main.temp
                val newWeather = response.weather.firstOrNull()?.description ?: "Unknown"

                Log.d("WeatherWorker", "Pin ${pin.title}: $newTemp°C, $newWeather")

                // Detect change
                val tempChanged = pin.lastKnownTemp?.let {
                    kotlin.math.abs(newTemp - it) > 2
                } ?: true  // first time - always true

                val weatherChanged = pin.lastKnownWeather?.let {
                    it != newWeather
                } ?: true

                if (tempChanged || weatherChanged) {
                    Log.d("WeatherWorker", "CHANGE DETECTED for ${pin.title} → sending NOTIFICATION")

                    sendWeatherNotification(
                        pinTitle = pin.title,
                        pinId = pin.id,
                        temp = newTemp,
                        desc = newWeather
                    )
                }

                //  Update stored values
                pin.lastKnownTemp = newTemp
                pin.lastKnownWeather = newWeather
            }

            // Save all pins back to JSON
            app.savePins()
            Result.success()

        } catch (e: Exception) {
            Log.e("WeatherWorker", "Error checking weather: ${e.message}")
            Result.failure()
        }
    }

    private fun sendWeatherNotification(pinTitle: String, pinId: String, temp: Double, desc: String) {

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // When user taps the notification
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("pinId", pinId)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            MainActivity.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_pin)
            .setContentTitle("Weather update: $pinTitle")
            .setContentText("$temp°C • $desc")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // UniqueID - hash
        notificationManager.notify(pinTitle.hashCode(), notification)
    }
}

