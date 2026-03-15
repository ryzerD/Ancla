package co.ryzer.ancla.ui.scripts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ryzer.ancla.data.Script
import co.ryzer.ancla.data.repository.ScriptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ScriptsViewModel @Inject constructor(
    private val repository: ScriptRepository
) : ViewModel() {

    val uiState: StateFlow<ScriptsUiState> = repository.observeScripts()
        .map { scripts -> ScriptsUiState(scripts = scripts) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ScriptsUiState()
        )

    fun addScript(phrase: String, categoryId: String, styleId: String) {
        viewModelScope.launch {
            repository.addScript(
                phrase = phrase,
                categoryId = categoryId,
                styleId = styleId
            )
        }
    }

    fun reorderScripts(orderedScriptIds: List<String>) {
        viewModelScope.launch {
            repository.updateOrder(orderedScriptIds)
        }
    }

    fun getScriptById(scriptId: String): Script? {
        return uiState.value.scripts.firstOrNull { it.id == scriptId }
    }
}

