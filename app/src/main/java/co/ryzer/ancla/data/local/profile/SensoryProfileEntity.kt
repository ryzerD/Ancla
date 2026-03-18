package co.ryzer.ancla.data.local.profile

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ryzer.ancla.data.SensoryProfile

@Entity(tableName = "sensory_profile")
data class SensoryProfileEntity(
    @PrimaryKey
    val id: Int = SENSORY_PROFILE_SINGLETON_ID,
    val name: String,
    val emergencyContact: String,
    val selectedColorId: String
)

const val SENSORY_PROFILE_SINGLETON_ID = 1

fun SensoryProfileEntity.toDomain(): SensoryProfile = SensoryProfile(
    id = id,
    name = name,
    emergencyContact = emergencyContact,
    selectedColorId = selectedColorId
)

fun SensoryProfile.toEntity(): SensoryProfileEntity = SensoryProfileEntity(
    id = id,
    name = name,
    emergencyContact = emergencyContact,
    selectedColorId = selectedColorId
)

