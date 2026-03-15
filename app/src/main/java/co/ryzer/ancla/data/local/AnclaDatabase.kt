package co.ryzer.ancla.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import co.ryzer.ancla.data.local.script.ScriptDao
import co.ryzer.ancla.data.local.script.ScriptEntity
import co.ryzer.ancla.data.local.task.TaskDao
import co.ryzer.ancla.data.local.task.TaskEntity

@Database(
    entities = [TaskEntity::class, ScriptEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AnclaDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun scriptDao(): ScriptDao
}

