package co.ryzer.ancla.data.repository

import co.ryzer.ancla.data.Script
import kotlinx.coroutines.flow.Flow

interface ScriptRepository {
    fun observeScripts(): Flow<List<Script>>
    fun observeScriptById(scriptId: String): Flow<Script?>
    suspend fun addScript(phrase: String, categoryId: String, styleId: String)
    suspend fun updateOrder(orderedScriptIds: List<String>)
}

