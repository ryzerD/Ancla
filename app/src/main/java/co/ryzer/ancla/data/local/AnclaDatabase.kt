package co.ryzer.ancla.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import co.ryzer.ancla.data.local.task.TaskDao
import co.ryzer.ancla.data.local.task.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AnclaDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}

