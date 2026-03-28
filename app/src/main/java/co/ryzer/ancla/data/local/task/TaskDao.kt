package co.ryzer.ancla.data.local.task

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY completedAt ASC, startTime ASC")
    fun observeTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteById(taskId: String)

    @Query(
        """
        UPDATE tasks
        SET completedAt = :timestamp
        WHERE id = :taskId
        """
    )
    suspend fun markCompleted(taskId: String, timestamp: Long = System.currentTimeMillis())

    @Query(
        """
        UPDATE tasks 
        SET startedAt = CASE WHEN startedAt IS NULL THEN :timestamp ELSE startedAt END
        WHERE id = :taskId
        """
    )
    suspend fun markStarted(taskId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Query(
        """
        SELECT * FROM tasks
        WHERE completedAt IS NULL
          AND (
            (:currentTime >= startTime AND :currentTime < endTime)
            OR (startTime > :currentTime AND startTime <= :preparingUntil)
          )
        ORDER BY
            CASE
                WHEN :currentTime >= startTime AND :currentTime < endTime THEN 0
                ELSE 1
            END,
            startTime ASC
        LIMIT 1
        """
    )
    fun observeHomeTaskCandidate(currentTime: String, preparingUntil: String): Flow<TaskEntity?>

    @Query(
        """
        SELECT * FROM tasks
        WHERE completedAt IS NULL
          AND startTime >= :fromTime
        ORDER BY startTime ASC
        """
    )
    suspend fun getPendingTasksStartingFrom(fromTime: String): List<TaskEntity>
}
