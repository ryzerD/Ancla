package co.ryzer.ancla.data.repository

import co.ryzer.ancla.data.Task
import co.ryzer.ancla.data.local.task.TaskDao
import co.ryzer.ancla.data.local.task.toDomain
import co.ryzer.ancla.data.local.task.toEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTaskRepository @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun observeTasks(): Flow<List<Task>> {
        return taskDao.observeTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeHomeTaskCandidate(currentTime: String, preparingUntil: String): Flow<Task?> {
        return taskDao.observeHomeTaskCandidate(
            currentTime = currentTime,
            preparingUntil = preparingUntil
        ).map { it?.toDomain() }
    }

    override suspend fun getTaskById(taskId: String): Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }

    override suspend fun addTask(task: Task) {
        taskDao.insert(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
    }

    override suspend fun setTaskInProgress(taskId: String, isInProgress: Boolean) {
        if (isInProgress) {
            taskDao.markStarted(taskId)
        }
    }

    override suspend fun setTaskCompleted(taskId: String, isCompleted: Boolean) {
        if (isCompleted) {
            taskDao.markCompleted(taskId)
        }
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteById(taskId)
    }

    override suspend fun getPendingTasksStartingFrom(fromTime: String): List<Task> {
        return taskDao.getPendingTasksStartingFrom(fromTime).map { it.toDomain() }
    }
}
