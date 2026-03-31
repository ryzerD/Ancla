package co.ryzer.ancla.data.repository

import co.ryzer.ancla.data.UserAssessmentResult
import co.ryzer.ancla.data.local.assessment.UserAssessmentDao
import co.ryzer.ancla.data.local.assessment.toDomain
import co.ryzer.ancla.data.local.assessment.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomUserAssessmentRepository @Inject constructor(
    private val userAssessmentDao: UserAssessmentDao
) : UserAssessmentRepository {

    override fun observeAssessment(): Flow<UserAssessmentResult?> {
        return userAssessmentDao.observeById().map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun getAssessment(): UserAssessmentResult? {
        return userAssessmentDao.getById()?.toDomain()
    }

    override suspend fun saveAssessment(assessment: UserAssessmentResult) {
        userAssessmentDao.upsert(assessment.toEntity())
    }

    override suspend fun deleteAssessment() {
        userAssessmentDao.delete()
    }
}

