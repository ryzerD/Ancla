package co.ryzer.ancla.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private fun createSensoryProfileTable(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS sensory_profile (
            id INTEGER NOT NULL,
            name TEXT NOT NULL,
            emergencyContactName TEXT NOT NULL,
            emergencyContact TEXT NOT NULL,
            selectedColorId TEXT NOT NULL,
            PRIMARY KEY(id)
        )
        """.trimIndent()
    )
}

private fun createScriptsTable(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS scripts (
            id TEXT NOT NULL,
            title TEXT NOT NULL,
            subtitle TEXT NOT NULL,
            message TEXT NOT NULL,
            categoryId TEXT NOT NULL,
            styleId TEXT NOT NULL,
            position INTEGER NOT NULL,
            showEmergencyContact INTEGER NOT NULL,
            PRIMARY KEY(id)
        )
        """.trimIndent()
    )
}

fun seedDefaultSensoryProfile(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        INSERT OR REPLACE INTO sensory_profile (id, name, emergencyContactName, emergencyContact, selectedColorId)
        VALUES (1, '', '', '', 'lavender')
        """.trimIndent()
    )
}

fun seedDefaultScripts(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        INSERT OR REPLACE INTO scripts
        (id, title, subtitle, message, categoryId, styleId, position, showEmergencyContact)
        VALUES
        ('ask_help', 'Pedir ayuda', 'Toca para pedir ayuda rapidamente.', 'NECESITO AYUDA', 'social', 'lavender', 0, 1),
        ('noise', 'Hay mucho ruido', 'Muestra esto si hay sobrecarga sonora.', 'HAY MUCHO RUIDO\nNECESITO UN LUGAR TRANQUILO', 'limits', 'rose', 1, 0),
        ('shopping', 'Comprar algo', 'Muestra para interacciones en tiendas.', 'QUIERO COMPRAR ESTO\nPOR FAVOR', 'errands', 'mixed', 2, 0),
        ('cannot_talk', 'No puedo hablar ahora', 'Usa en momentos de bloqueo comunicativo.', 'NO PUEDO HABLAR\nAHORA', 'needs', 'sand', 3, 1)
        """.trimIndent()
    )
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        createScriptsTable(db)
        seedDefaultScripts(db)
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        createSensoryProfileTable(db)
        seedDefaultSensoryProfile(db)
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            ALTER TABLE sensory_profile
            ADD COLUMN emergencyContactName TEXT NOT NULL DEFAULT ''
            """.trimIndent()
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS user_assessment (
                id INTEGER NOT NULL,
                totalScore INTEGER NOT NULL,
                primaryTrait TEXT NOT NULL,
                completedAt INTEGER NOT NULL,
                assessmentData TEXT NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
    }
}


