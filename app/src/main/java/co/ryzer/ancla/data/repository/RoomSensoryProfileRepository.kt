package co.ryzer.ancla.data.repository

import co.ryzer.ancla.data.SensoryProfile
import co.ryzer.ancla.data.local.profile.SensoryProfileDao
import co.ryzer.ancla.data.local.profile.toDomain
import co.ryzer.ancla.data.local.profile.toEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomSensoryProfileRepository @Inject constructor(
    private val sensoryProfileDao: SensoryProfileDao
) : SensoryProfileRepository {

    override fun observeProfile(): Flow<SensoryProfile> {
        return sensoryProfileDao.observeById().map { entity ->
            entity?.toDomain() ?: SensoryProfile()
        }
    }

    override suspend fun getProfile(): SensoryProfile {
        return sensoryProfileDao.getById()?.toDomain() ?: SensoryProfile()
    }

    override suspend fun saveProfile(profile: SensoryProfile) {
        sensoryProfileDao.upsert(profile.toEntity())
    }
}

