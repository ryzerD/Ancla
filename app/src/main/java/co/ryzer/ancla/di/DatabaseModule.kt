package co.ryzer.ancla.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import co.ryzer.ancla.data.local.AnclaDatabase
import co.ryzer.ancla.data.local.MIGRATION_1_2
import co.ryzer.ancla.data.local.MIGRATION_2_3
import co.ryzer.ancla.data.local.MIGRATION_3_4
import co.ryzer.ancla.data.local.MIGRATION_4_5
import co.ryzer.ancla.data.local.assessment.UserAssessmentDao
import co.ryzer.ancla.data.local.seedDefaultScripts
import co.ryzer.ancla.data.local.seedDefaultSensoryProfile
import co.ryzer.ancla.data.local.profile.SensoryProfileDao
import co.ryzer.ancla.data.local.script.ScriptDao
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
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    seedDefaultScripts(db)
                    seedDefaultSensoryProfile(db)
                }
            })
            .build()
    }

    @Provides
    fun provideTaskDao(database: AnclaDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideScriptDao(database: AnclaDatabase): ScriptDao = database.scriptDao()

    @Provides
    fun provideSensoryProfileDao(database: AnclaDatabase): SensoryProfileDao =
        database.sensoryProfileDao()

    @Provides
    fun provideUserAssessmentDao(database: AnclaDatabase): UserAssessmentDao =
        database.userAssessmentDao()
}

