package co.ryzer.ancla.data.local.assessment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

const val USER_ASSESSMENT_SINGLETON_ID = 1

@Dao
interface UserAssessmentDao {
    @Query("SELECT * FROM user_assessment WHERE id = :assessmentId LIMIT 1")
    fun observeById(assessmentId: Int = USER_ASSESSMENT_SINGLETON_ID): Flow<UserAssessmentEntity?>

    @Query("SELECT * FROM user_assessment WHERE id = :assessmentId LIMIT 1")
    suspend fun getById(assessmentId: Int = USER_ASSESSMENT_SINGLETON_ID): UserAssessmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(assessment: UserAssessmentEntity)

    @Query("DELETE FROM user_assessment WHERE id = :assessmentId")
    suspend fun delete(assessmentId: Int = USER_ASSESSMENT_SINGLETON_ID)
}

