package co.ryzer.ancla.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import co.ryzer.ancla.data.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskAlarmManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        private const val LOG_TAG = "AnclaTaskAlarmManager"
        private const val EXTRA_TASK_ID = "TASK_ID"
        private const val EXTRA_TASK_TITLE = "TASK_TITLE"
        private const val EXTRA_TASK_CATEGORY = "TASK_CATEGORY"
        private const val NOTIFICATION_LEAD_MINUTES = 10L
        private const val IMMEDIATE_NOTIFICATION_DELAY_SECONDS = 5L
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(task: Task) {
        // Validate task
        if (task.id.isBlank() || task.title.isBlank()) {
            Log.w(LOG_TAG, "Skipping alarm for invalid task: ${task.id}")
            return
        }

        if (task.isCompleted) {
            cancelAlarm(task)
            return
        }

        try {
            val startTime = try {
                LocalTime.parse(task.startTime)
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Invalid startTime format: ${task.startTime}, using 08:00", e)
                LocalTime.of(8, 0)
            }

            val endTime = try {
                LocalTime.parse(task.endTime)
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Invalid endTime format: ${task.endTime}, using 09:00", e)
                LocalTime.of(9, 0)
            }

            val now = LocalDateTime.now()

            // Calculate notification time: 10 minutes before task start
            val todayStart = LocalDateTime.of(LocalDate.now(), startTime)
            val todayEnd = adjustEndForOvernight(todayStart, endTime)
            var notificationTime = todayStart.minusMinutes(NOTIFICATION_LEAD_MINUTES)

            // If notification time has already passed but task is still ongoing,
            // notify immediately instead of waiting until tomorrow.
            if (notificationTime.isBefore(now)) {
                if (now.isBefore(todayEnd)) {
                    notificationTime = now.plusSeconds(IMMEDIATE_NOTIFICATION_DELAY_SECONDS)
                    Log.d(LOG_TAG, "Task ${task.id} is currently ongoing, notifying immediately")
                } else {
                    notificationTime = notificationTime.plusDays(1)
                    Log.d(LOG_TAG, "Task ${task.id} already passed, scheduling for tomorrow")
                }
            }

            val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
                putExtra(EXTRA_TASK_ID, task.id)
                putExtra(EXTRA_TASK_TITLE, task.title)
                putExtra(EXTRA_TASK_CATEGORY, task.category)
            }

            // Use absolute hash to ensure positive ID
            val alarmRequestCode = kotlin.math.abs(task.id.hashCode())

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmRequestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerAtMillis =
                notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            Log.d(
                LOG_TAG,
                "Scheduling alarm for taskId=${task.id}, title=${task.title}, triggerTime=$notificationTime"
            )

            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }

            if (canScheduleExact) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                Log.i(LOG_TAG, "Exact alarm scheduled for taskId=${task.id}")
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                Log.w(
                    LOG_TAG,
                    "Exact alarms not permitted on this device; using inexact alarm for taskId=${task.id}"
                )
            }
        } catch (e: SecurityException) {
            Log.e(
                LOG_TAG,
                "Permission denied while scheduling alarm for taskId=${task.id}",
                e
            )
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Unexpected error while scheduling alarm for taskId=${task.id}", e)
        }
    }

    fun scheduleAlarmWithPostponement(task: Task, postponementMinutes: Long) {
        // Reprograma la alarma con el offset temporal de postponement
        if (postponementMinutes <= 0L) {
            scheduleAlarm(task)
            return
        }

        // Validate task
        if (task.id.isBlank() || task.title.isBlank()) {
            Log.w(LOG_TAG, "Skipping alarm for invalid task: ${task.id}")
            return
        }

        if (task.isCompleted) {
            cancelAlarm(task)
            return
        }

        try {
            // Solo sumamos postponement a startTime (es lo que determina cuándo dispara la alarma)
            val startTime = try {
                LocalTime.parse(task.startTime).plusMinutes(postponementMinutes)
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Invalid startTime format: ${task.startTime}, using 08:00", e)
                LocalTime.of(8, 0).plusMinutes(postponementMinutes)
            }

            // endTime se mantiene igual - solo se usa para saber si la tarea ya pasó
            val endTime = try {
                LocalTime.parse(task.endTime)
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Invalid endTime format: ${task.endTime}, using 09:00", e)
                LocalTime.of(9, 0)
            }

            val now = LocalDateTime.now()

            // Calculate notification time: 10 minutes before task start (con postponement)
            val todayStart = LocalDateTime.of(LocalDate.now(), startTime)
            val todayEnd = adjustEndForOvernight(todayStart, endTime)
            var notificationTime = todayStart.minusMinutes(NOTIFICATION_LEAD_MINUTES)

            // If notification time has already passed but task is still ongoing,
            // notify immediately instead of waiting until tomorrow.
            if (notificationTime.isBefore(now)) {
                if (now.isBefore(todayEnd)) {
                    notificationTime = now.plusSeconds(IMMEDIATE_NOTIFICATION_DELAY_SECONDS)
                    Log.d(LOG_TAG, "Task ${task.id} is currently ongoing (postponed), notifying immediately")
                } else {
                    notificationTime = notificationTime.plusDays(1)
                    Log.d(LOG_TAG, "Task ${task.id} already passed (postponed), scheduling for tomorrow")
                }
            }

            val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
                putExtra(EXTRA_TASK_ID, task.id)
                putExtra(EXTRA_TASK_TITLE, task.title)
                putExtra(EXTRA_TASK_CATEGORY, task.category)
            }

            // Use absolute hash to ensure positive ID
            val alarmRequestCode = kotlin.math.abs(task.id.hashCode())

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmRequestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerAtMillis =
                notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            Log.d(
                LOG_TAG,
                "Scheduling alarm (POSTPONED +$postponementMinutes min) for taskId=${task.id}, title=${task.title}, triggerTime=$notificationTime"
            )

            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }

            if (canScheduleExact) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                Log.i(LOG_TAG, "Exact alarm scheduled (postponed) for taskId=${task.id}")
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                Log.w(
                    LOG_TAG,
                    "Exact alarms not permitted; using inexact alarm (postponed) for taskId=${task.id}"
                )
            }
        } catch (e: SecurityException) {
            Log.e(
                LOG_TAG,
                "Permission denied while scheduling alarm (postponed) for taskId=${task.id}",
                e
            )
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Unexpected error while scheduling alarm (postponed) for taskId=${task.id}", e)
        }
    }

    fun cancelAlarm(task: Task) {
        if (task.id.isBlank()) {
            Log.w(LOG_TAG, "Cannot cancel alarm for task with blank ID")
            return
        }

        try {
            val intent = Intent(context, TaskAlarmReceiver::class.java)
            val alarmRequestCode = kotlin.math.abs(task.id.hashCode())

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmRequestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
                Log.d(LOG_TAG, "Alarm cancelled for taskId=${task.id}")
            } else {
                Log.d(LOG_TAG, "No pending alarm found to cancel for taskId=${task.id}")
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error cancelling alarm for taskId=${task.id}", e)
        }
    }

    private fun adjustEndForOvernight(start: LocalDateTime, endTime: LocalTime): LocalDateTime {
        val sameDayEnd = LocalDateTime.of(start.toLocalDate(), endTime)
        return if (sameDayEnd.isAfter(start)) sameDayEnd else sameDayEnd.plusDays(1)
    }
}
