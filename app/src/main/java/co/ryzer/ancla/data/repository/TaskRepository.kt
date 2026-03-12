package co.ryzer.ancla.data.repository

import co.ryzer.ancla.data.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    suspend fun addTask(title: String, description: String, time: String)
    suspend fun setTaskCompleted(taskId: String, isCompleted: Boolean)
    suspend fun deleteTask(taskId: String)
}

