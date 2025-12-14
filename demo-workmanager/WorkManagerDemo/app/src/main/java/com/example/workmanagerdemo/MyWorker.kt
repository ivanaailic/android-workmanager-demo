package com.example.workmanagerdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class MyWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Simulate some background work
            delay(3000)

            // Confirm that the background work was executed
            Log.d("MyWorker", "Background work executed")

            // Show a notification when the work is finished
            showNotification()

            // Indicate that the work finished successfully
            Result.success()
        } catch (e: Exception) {
            // In case of an error, indicate failure
            Result.failure()
        }
    }

    private fun showNotification() {
        val channelId = "workmanager_demo"

        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "WorkManager Demo",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("WorkManager")
            .setContentText("Background task finished")
            .build()

        // Display the notification
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }
}
