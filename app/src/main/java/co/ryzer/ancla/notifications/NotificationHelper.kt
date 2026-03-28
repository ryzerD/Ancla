package co.ryzer.ancla.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import co.ryzer.ancla.R
import co.ryzer.ancla.MainActivity
import android.Manifest

object NotificationHelper {
    const val CHANNEL_ID = "task_reminders"
    const val CHANNEL_NAME = "Recordatorios de Tareas"
    const val CHANNEL_DESC = "Notificaciones suaves para tus próximas actividades"
    const val EXTRA_OPEN_HOME_FROM_NOTIFICATION = "extra_open_home_from_notification"
    private const val LOG_TAG = "AnclaNotifications"
    private const val PREFS_NAME = "ancla_notification_prefs"
    private const val PREF_KEY_TASK_NOTIFICATIONS_SILENCED = "task_notifications_silenced"

    fun createNotificationChannel(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                    description = CHANNEL_DESC
                    enableLights(true)
                    enableVibration(false)
                    setShowBadge(true)
                }
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
                Log.d(LOG_TAG, "Notification channel created successfully")
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to create notification channel", e)
            }
    }

    fun showTaskNotification(context: Context, title: String, message: String, notificationId: Int) {
        // Validate inputs
        if (title.isBlank() || message.isBlank()) {
            Log.w(LOG_TAG, "Skipping notification: title or message is empty")
            return
        }

        if (notificationId <= 0) {
            Log.w(LOG_TAG, "Skipping notification: invalid notificationId=$notificationId")
            return
        }

        val largeIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)

        try {
            // Ensure channel exists before posting notification
            createNotificationChannel(context)

            // Create intent to open app at home
            val openHomeIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(EXTRA_OPEN_HOME_FROM_NOTIFICATION, true)
            }
            val openHomePendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                openHomeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Build notification with safe resource fallbacks
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(largeIcon)
                .setContentTitle(title.take(256))  // Limit title length for safety
                .setContentText(message.take(256))  // Limit message length for safety
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(openHomePendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setOnlyAlertOnce(true)

            // Safely apply color (fallback to default if resource unavailable)
            try {
                builder.setColor(context.getColor(R.color.purple_500))
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Color resource not found, using default", e)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Check permission before notifying (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    Log.w(
                        LOG_TAG,
                        "POST_NOTIFICATIONS permission not granted, skipping notification"
                    )
                    return
                }
            }

            notificationManager.notify(notificationId, builder.build())
            Log.d(LOG_TAG, "Notification posted successfully for taskId=$notificationId")

        } catch (e: SecurityException) {
            Log.e(LOG_TAG, "SecurityException while posting notification", e)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to show task notification for taskId=$notificationId", e)
        }
    }

    fun setTaskNotificationsSilenced(context: Context, silenced: Boolean) {
        runCatching {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit {
                    putBoolean(PREF_KEY_TASK_NOTIFICATIONS_SILENCED, silenced)
                }

            if (silenced) {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }
        }.onFailure { error ->
            Log.e(LOG_TAG, "Failed to set task notification silence=$silenced", error)
        }
    }

    fun areTaskNotificationsSilenced(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(PREF_KEY_TASK_NOTIFICATIONS_SILENCED, false)
    }
}
