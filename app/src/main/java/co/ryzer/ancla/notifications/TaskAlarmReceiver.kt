package co.ryzer.ancla.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Actividad próxima"
        val taskCategory = intent.getStringExtra("TASK_CATEGORY") ?: "Rutina"
        val taskId = intent.getStringExtra("TASK_ID")?.hashCode() ?: 0

        NotificationHelper.showTaskNotification(
            context = context,
            title = "Próxima actividad: $taskTitle",
            message = "Tu actividad de $taskCategory comenzará en 10 minutos.",
            notificationId = taskId
        )
    }
}
