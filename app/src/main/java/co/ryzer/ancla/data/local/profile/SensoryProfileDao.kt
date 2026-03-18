package co.ryzer.ancla.data.local.profile

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SensoryProfileDao {
    @Query("SELECT * FROM sensory_profile WHERE id = :profileId LIMIT 1")
    fun observeById(profileId: Int = SENSORY_PROFILE_SINGLETON_ID): Flow<SensoryProfileEntity?>

    @Query("SELECT * FROM sensory_profile WHERE id = :profileId LIMIT 1")
    suspend fun getById(profileId: Int = SENSORY_PROFILE_SINGLETON_ID): SensoryProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: SensoryProfileEntity)
}

