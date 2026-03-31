package co.ryzer.ancla.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.ryzer.ancla.data.repository.TaskRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate

class DailyResetWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                DailyResetWorkerEntryPoint::class.java
            )
            entryPoint.taskRepository().runDailyRoutineReset(
                snapshotDate = LocalDate.now().toString(),
                recordedAt = System.currentTimeMillis()
            )
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DailyResetWorkerEntryPoint {
    fun taskRepository(): TaskRepository
}

