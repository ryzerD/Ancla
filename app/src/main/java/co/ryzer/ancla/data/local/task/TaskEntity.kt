package co.ryzer.ancla.data.local.task

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ryzer.ancla.data.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val category: String,
    val isCompleted: Boolean
)

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    startTime = startTime,
    endTime = endTime,
    category = category,
    isCompleted = isCompleted
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    startTime = startTime,
    endTime = endTime,
    category = category,
    isCompleted = isCompleted
)
