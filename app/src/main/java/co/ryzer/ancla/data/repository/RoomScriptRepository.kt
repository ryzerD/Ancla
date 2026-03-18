package co.ryzer.ancla.data.repository

import co.ryzer.ancla.data.Script
import co.ryzer.ancla.data.local.script.ScriptDao
import co.ryzer.ancla.data.local.script.toDomain
import co.ryzer.ancla.data.local.script.toEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomScriptRepository @Inject constructor(
    private val scriptDao: ScriptDao
) : ScriptRepository {

    override fun observeScripts(): Flow<List<Script>> {
        return scriptDao.observeScripts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeScriptById(scriptId: String): Flow<Script?> {
        return scriptDao.observeScriptById(scriptId).map { entity -> entity?.toDomain() }
    }

    override suspend fun addScript(phrase: String, categoryId: String, styleId: String) {
        val message = phrase.trim()
        if (message.isBlank()) return

        val nextPosition = scriptDao.maxPosition() + 1
        scriptDao.insert(
            Script(
                title = message,
                subtitle = defaultSubtitleForCategory(categoryId),
                message = message,
                categoryId = categoryId,
                styleId = styleId,
                position = nextPosition,
                showEmergencyContact = false
            ).toEntity()
        )
    }

    override suspend fun updateOrder(orderedScriptIds: List<String>) {
        orderedScriptIds.forEachIndexed { index, scriptId ->
            scriptDao.updatePosition(scriptId = scriptId, position = index)
        }
    }

    private fun defaultSubtitleForCategory(categoryId: String): String {
        return when (categoryId) {
            "social" -> "Guion para interaccion social."
            "needs" -> "Guion para expresar necesidades."
            "limits" -> "Guion para comunicar limites."
            "errands" -> "Guion para tramites o compras."
            else -> "Guion personalizado."
        }
    }
}

