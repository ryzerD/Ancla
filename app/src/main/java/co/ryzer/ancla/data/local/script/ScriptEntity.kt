package co.ryzer.ancla.data.local.script

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ryzer.ancla.data.Script

@Entity(tableName = "scripts")
data class ScriptEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subtitle: String,
    val message: String,
    val categoryId: String,
    val styleId: String,
    val position: Int,
    val showEmergencyContact: Boolean
)

fun ScriptEntity.toDomain(): Script = Script(
    id = id,
    title = title,
    subtitle = subtitle,
    message = message,
    categoryId = categoryId,
    styleId = styleId,
    position = position,
    showEmergencyContact = showEmergencyContact
)

fun Script.toEntity(): ScriptEntity = ScriptEntity(
    id = id,
    title = title,
    subtitle = subtitle,
    message = message,
    categoryId = categoryId,
    styleId = styleId,
    position = position,
    showEmergencyContact = showEmergencyContact
)

