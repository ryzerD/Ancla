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
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(task: Task) {
        if (task.isCompleted) {
            cancelAlarm(task)
            return
        }

        try {
            val startTime = LocalTime.parse(task.startTime)
            val now = LocalDateTime.now()

            // Calculamos el momento de la notificación (10 min antes)
            var notificationTime = LocalDateTime.of(LocalDate.now(), startTime).minusMinutes(10)

            // Si el momento de avisar YA PASÓ pero la tarea aún no termina,
            // avisamos INMEDIATAMENTE (dentro de 5 segundos) en lugar de esperar a mañana.
            if (notificationTime.isBefore(now)) {
                val taskEndTime = LocalTime.parse(task.endTime)
                val taskEndDateTime = LocalDateTime.of(LocalDate.now(), taskEndTime)

                if (now.isBefore(taskEndDateTime)) {
                    notificationTime = now.plusSeconds(5) // Aviso casi inmediato
                } else {
                    notificationTime = notificationTime.plusDays(1)
                }
            }

            val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
                putExtra("TASK_ID", task.id)
                putExtra("TASK_TITLE", task.title)
                putExtra("TASK_CATEGORY", task.category)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                task.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerAtMillis =
                notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            // Log para que verifiques en Android Studio
            Log.d(
                "AnclaNotifications",
                "Programando alarma para ${task.title} a las $notificationTime"
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
            } else {
                // Fallback when exact alarms are not allowed on Android 12+
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                Log.w(
                    "AnclaNotifications",
                    "No se pueden programar alarmas exactas; usando alarma inexacta para ${task.title}"
                )
            }
        } catch (e: SecurityException) {
            Log.e(
                "AnclaNotifications",
                "Sin permiso para programar alarma exacta/inexacta de ${task.title}",
                e
            )
        } catch (e: Exception) {
            Log.e("AnclaNotifications", "Error al programar alarma", e)
        }
    }

    fun cancelAlarm(task: Task) {
        val intent = Intent(context, TaskAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
