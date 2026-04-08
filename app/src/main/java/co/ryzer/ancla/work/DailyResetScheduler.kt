package co.ryzer.ancla.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import androidx.core.content.edit

object DailyResetScheduler {
    private const val UNIQUE_WORK_NAME = "daily_reset_routine_tasks"
    private const val IMMEDIATE_WORK_NAME = "daily_reset_immediate"
    private const val PREFS_NAME = "ancla_daily_reset"
    private const val LAST_RESET_DATE_KEY = "last_reset_date"

    fun schedule(context: Context) {
        // Chequeo inmediato: si no se ha hecho reset hoy, ejecutar ahora
        checkAndExecuteImmediateReset(context)

        // Programar trabajo periódico para futuras ejecuciones a medianoche
        val initialDelay = computeDelayUntilNextMidnightMillis()
        val request = PeriodicWorkRequestBuilder<DailyResetWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun checkAndExecuteImmediateReset(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastResetDate = prefs.getString(LAST_RESET_DATE_KEY, null)
        val todayDate = LocalDate.now().toString()

        if (lastResetDate != todayDate) {
            // Primer inicio del día - marcar que se ejecutó hoy
            prefs.edit { putString(LAST_RESET_DATE_KEY, todayDate) }
            
            // Ejecutar reset inmediatamente (OneTimeWork)
            val immediateRequest = OneTimeWorkRequestBuilder<DailyResetWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                IMMEDIATE_WORK_NAME,
                androidx.work.ExistingWorkPolicy.REPLACE,
                immediateRequest
            )
        }
    }

    private fun computeDelayUntilNextMidnightMillis(): Long {
        val now = LocalDateTime.now()
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
        return Duration.between(now, nextMidnight).toMillis().coerceAtLeast(1_000L)
    }
}
