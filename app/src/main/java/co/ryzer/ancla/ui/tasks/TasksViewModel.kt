package co.ryzer.ancla.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ryzer.ancla.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val formState = MutableStateFlow(TasksUiState())

    val uiState: StateFlow<TasksUiState> = combine(
        repository.observeTasks(),
        formState
    ) { tasks, form ->
        form.copy(tasks = tasks)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TasksUiState()
    )

    fun onTitleChange(value: String) {
        formState.update { it.copy(newTitle = value) }
    }

    fun onDescriptionChange(value: String) {
        formState.update { it.copy(newDescription = value) }
    }

    fun onTimeChange(value: String) {
        formState.update { it.copy(newTime = value) }
    }

    fun addTask() {
        val title = uiState.value.newTitle.trim()
        val description = uiState.value.newDescription.trim()
        val time = uiState.value.newTime.trim()
        if (title.isBlank() || time.isBlank()) return

        viewModelScope.launch {
            repository.addTask(
                title = title,
                description = description,
                time = time
            )
            formState.update {
                it.copy(
                    newTitle = "",
                    newDescription = "",
                    newTime = ""
                )
            }
        }
    }

    fun setTaskCompleted(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.setTaskCompleted(taskId = taskId, isCompleted = isCompleted)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }
}


