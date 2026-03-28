package co.ryzer.ancla.data.local.task

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_history",
    indices = [Index("taskId"), Index("snapshotDate")]
)
data class TaskHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: String,
    val taskTitle: String,
    val taskCategory: String,
    val wasCompleted: Boolean,
    val recordedAt: Long,
    val snapshotDate: String
)
