package co.ryzer.ancla.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {
        @Volatile
        private var INSTANCE: AnclaDatabase? = null

        fun getInstance(context: Context): AnclaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnclaDatabase::class.java,
                    "ancla.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

