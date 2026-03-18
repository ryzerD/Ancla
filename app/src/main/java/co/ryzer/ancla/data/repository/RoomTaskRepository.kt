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

    override suspend fun addTask(title: String, description: String, time: String) {
        taskDao.insert(
            Task(
                title = title,
                description = description,
                time = time
            ).toEntity()
        )
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
    }

    override suspend fun setTaskCompleted(taskId: String, isCompleted: Boolean) {
        taskDao.updateCompleted(taskId = taskId, isCompleted = isCompleted)
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteById(taskId)
    }
}

