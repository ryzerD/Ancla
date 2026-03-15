package co.ryzer.ancla.data.repository

import co.ryzer.ancla.data.SensoryProfile
import kotlinx.coroutines.flow.Flow

interface SensoryProfileRepository {
    fun observeProfile(): Flow<SensoryProfile>
    suspend fun getProfile(): SensoryProfile
    suspend fun saveProfile(profile: SensoryProfile)
}

