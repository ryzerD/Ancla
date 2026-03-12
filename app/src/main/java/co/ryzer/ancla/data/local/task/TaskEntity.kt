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
    val time: String,
    val isCompleted: Boolean
)

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    time = time,
    isCompleted = isCompleted
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    time = time,
    isCompleted = isCompleted
)

