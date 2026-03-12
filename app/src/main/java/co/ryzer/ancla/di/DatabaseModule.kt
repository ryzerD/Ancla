package co.ryzer.ancla.di

import android.content.Context
import androidx.room.Room
import co.ryzer.ancla.data.local.AnclaDatabase
import co.ryzer.ancla.data.local.task.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAnclaDatabase(
        @ApplicationContext context: Context
    ): AnclaDatabase {
        return Room.databaseBuilder(
            context,
            AnclaDatabase::class.java,
            "ancla.db"
        ).build()
    }

    @Provides
    fun provideTaskDao(database: AnclaDatabase): TaskDao = database.taskDao()
}

