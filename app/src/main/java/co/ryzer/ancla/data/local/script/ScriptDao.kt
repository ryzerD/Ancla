package co.ryzer.ancla.data.local.script

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptDao {
    @Query("SELECT * FROM scripts ORDER BY position ASC")
    fun observeScripts(): Flow<List<ScriptEntity>>

    @Query("SELECT * FROM scripts WHERE id = :scriptId LIMIT 1")
    fun observeScriptById(scriptId: String): Flow<ScriptEntity?>

    @Query("SELECT COUNT(id) FROM scripts")
    suspend fun countScripts(): Int

    @Query("SELECT COALESCE(MAX(position), -1) FROM scripts")
    suspend fun maxPosition(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(script: ScriptEntity)

    @Query("UPDATE scripts SET position = :position WHERE id = :scriptId")
    suspend fun updatePosition(scriptId: String, position: Int)
}

