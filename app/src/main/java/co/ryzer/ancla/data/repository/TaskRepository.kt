package co.ryzer.ancla.data.repository

import co.ryzer.ancla.data.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    fun observeHomeTaskCandidate(currentTime: String, preparingUntil: String): Flow<Task?>
    suspend fun getTaskById(taskId: String): Task?
    suspend fun addTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun getOverlappingTask(newStart: String, newEnd: String, excludeTaskId: String? = null): Task?
    suspend fun setTaskInProgress(taskId: String, isInProgress: Boolean)
    suspend fun setTaskCompleted(taskId: String, isCompleted: Boolean)
    suspend fun deleteTask(taskId: String)
    suspend fun getPendingTasksStartingFrom(fromTime: String): List<Task>
    suspend fun postponePendingTasksStartingFrom(fromTime: String, minutes: Long): Int
    suspend fun savePostponementBackup(taskId: String, offsetMinutes: Long)
    suspend fun clearAllPostponements(): Int
    suspend fun runDailyRoutineReset(snapshotDate: String, recordedAt: Long): Int
}
