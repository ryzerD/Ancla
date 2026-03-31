package co.ryzer.ancla.data.local.assessment

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ryzer.ancla.data.UserAssessmentResult

@Entity(tableName = "user_assessment")
data class UserAssessmentEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalScore: Int,
    val primaryTrait: String,
    val completedAt: Long,
    val assessmentData: String // JSON string with detailed answers
)

fun UserAssessmentEntity.toDomain(): UserAssessmentResult = UserAssessmentResult(
    id = id,
    totalScore = totalScore,
    primaryTrait = primaryTrait,
    completedAt = completedAt,
    assessmentData = assessmentData
)

fun UserAssessmentResult.toEntity(): UserAssessmentEntity = UserAssessmentEntity(
    id = id,
    totalScore = totalScore,
    primaryTrait = primaryTrait,
    completedAt = completedAt,
    assessmentData = assessmentData
)

