package co.ryzer.ancla.data.local.task

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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
        SET startedAt = :timestamp
        WHERE id = :taskId
        """
    )
    suspend fun markStarted(taskId: String, timestamp: Long = System.currentTimeMillis())

    @Query(
        """
        UPDATE tasks
        SET completedAt = NULL
        WHERE id = :taskId
        """
    )
    suspend fun markPending(taskId: String)

    @Query(
        """
        UPDATE tasks
        SET postponementOffsetMinutes = :offsetMinutes
        WHERE id = :taskId
        """
    )
    suspend fun savePostponementBackup(taskId: String, offsetMinutes: Long)

    @Query(
        """
        UPDATE tasks
        SET postponementOffsetMinutes = NULL
        WHERE postponementOffsetMinutes IS NOT NULL
        """
    )
    suspend fun clearAllPostponements(): Int

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Query(
        """
        INSERT INTO task_history (taskId, taskTitle, taskCategory, wasCompleted, recordedAt, snapshotDate)
        SELECT id, title, category, CASE WHEN completedAt IS NOT NULL THEN 1 ELSE 0 END, :recordedAt, :snapshotDate
        FROM tasks
        WHERE category = :routineCategory
        """
    )
    suspend fun saveRoutineTaskSnapshot(
        routineCategory: String,
        snapshotDate: String,
        recordedAt: Long
    )

    @Query(
        """
        UPDATE tasks
        SET startedAt = NULL,
            completedAt = NULL
        WHERE category = :routineCategory
          AND completedAt IS NOT NULL
        """
    )
    suspend fun resetRoutineTasks(routineCategory: String): Int

    @Transaction
    suspend fun archiveAndResetRoutineTasks(
        routineCategory: String,
        snapshotDate: String,
        recordedAt: Long
    ): Int {
        saveRoutineTaskSnapshot(
            routineCategory = routineCategory,
            snapshotDate = snapshotDate,
            recordedAt = recordedAt
        )
        return resetRoutineTasks(routineCategory)
    }

    @Query(
        """
        SELECT * FROM tasks
        WHERE (:newStart < endTime) AND (:newEnd > startTime)
        LIMIT 1
        """
    )
    suspend fun getOverlappingTask(newStart: String, newEnd: String): TaskEntity?

    @Query(
        """
        SELECT * FROM tasks
        WHERE id != :excludeTaskId
          AND (:newStart < endTime) AND (:newEnd > startTime)
        LIMIT 1
        """
    )
    suspend fun getOverlappingTaskExcludingId(
        newStart: String,
        newEnd: String,
        excludeTaskId: String
    ): TaskEntity?

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

    @Query(
        """
        UPDATE tasks
        SET
            startTime = strftime('%H:%M', time(startTime, '+' || :minutes || ' minutes')),
            endTime = strftime('%H:%M', time(endTime, '+' || :minutes || ' minutes'))
        WHERE completedAt IS NULL
          AND startTime >= :fromTime
          AND time(startTime) IS NOT NULL
          AND time(endTime) IS NOT NULL
        """
    )
    suspend fun postponePendingTasksStartingFrom(fromTime: String, minutes: Long): Int
}
