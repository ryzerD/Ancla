package co.ryzer.ancla.di

import co.ryzer.ancla.data.repository.RoomTaskRepository
import co.ryzer.ancla.data.repository.RoomScriptRepository
import co.ryzer.ancla.data.repository.RoomSensoryProfileRepository
import co.ryzer.ancla.data.repository.SensoryProfileRepository
import co.ryzer.ancla.data.repository.ScriptRepository
import co.ryzer.ancla.data.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        repository: RoomTaskRepository
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindScriptRepository(
        repository: RoomScriptRepository
    ): ScriptRepository

    @Binds
    @Singleton
    abstract fun bindSensoryProfileRepository(
        repository: RoomSensoryProfileRepository
    ): SensoryProfileRepository
}

