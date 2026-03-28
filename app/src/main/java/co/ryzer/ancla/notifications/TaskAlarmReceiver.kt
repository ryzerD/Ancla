package co.ryzer.ancla.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

private const val LOG_TAG = "AnclaTaskAlarmReceiver"

class TaskAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            // Extract task data from broadcast intent
            val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Actividad próxima"
            val taskCategory = intent.getStringExtra("TASK_CATEGORY") ?: "Rutina"
            val taskId = intent.getStringExtra("TASK_ID")

            if (taskId.isNullOrBlank()) {
                Log.w(LOG_TAG, "Received alarm with missing TASK_ID")
                return
            }

            // Use taskId hash as notification ID (ensure it's positive)
            val notificationId = kotlin.math.abs(taskId.hashCode())

            Log.d(LOG_TAG, "Alarm received for taskId=$taskId, showing notification")

            NotificationHelper.showTaskNotification(
                context = context,
                title = "Próxima actividad: $taskTitle",
                message = "Tu actividad de $taskCategory comenzará en 10 minutos.",
                notificationId = notificationId
            )
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error handling task alarm broadcast", e)
        }
    }
}
