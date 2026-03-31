package co.ryzer.ancla.data.repository

import co.ryzer.ancla.data.UserAssessmentResult
import kotlinx.coroutines.flow.Flow

interface UserAssessmentRepository {
    fun observeAssessment(): Flow<UserAssessmentResult?>
    suspend fun getAssessment(): UserAssessmentResult?
    suspend fun saveAssessment(assessment: UserAssessmentResult)
    suspend fun deleteAssessment()
}

