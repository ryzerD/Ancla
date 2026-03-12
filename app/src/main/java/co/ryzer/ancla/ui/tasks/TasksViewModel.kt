package co.ryzer.ancla.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ryzer.ancla.data.Task
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

    companion object {
        const val TITLE_MAX_LENGTH = 60
        const val DESCRIPTION_MAX_LENGTH = 200
        const val TITLE_COUNTER_THRESHOLD = 10
        const val DESCRIPTION_COUNTER_THRESHOLD = 20
    }

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
        formState.update { it.copy(newTitle = value.take(TITLE_MAX_LENGTH)) }
    }

    fun onDescriptionChange(value: String) {
        formState.update { it.copy(newDescription = value.take(DESCRIPTION_MAX_LENGTH)) }
    }

    fun onTimeChange(value: String) {
        formState.update { it.copy(newTime = value) }
    }

    fun startEditing(task: Task) {
        formState.update {
            it.copy(
                newTitle = task.title,
                newDescription = task.description,
                newTime = task.time,
                editingTaskId = task.id
            )
        }
    }

    fun cancelEditing() {
        formState.update {
            it.copy(
                newTitle = "",
                newDescription = "",
                newTime = "",
                editingTaskId = null
            )
        }
    }

    fun addTask() {
        val currentState = uiState.value
        val title = currentState.newTitle.trim()
        val description = currentState.newDescription.trim()
        val time = currentState.newTime.trim()
        if (title.isBlank() || time.isBlank()) return

        viewModelScope.launch {
            val editingTaskId = currentState.editingTaskId
            if (editingTaskId == null) {
                repository.addTask(
                    title = title,
                    description = description,
                    time = time
                )
            } else {
                val previousTask = currentState.tasks.firstOrNull { it.id == editingTaskId }
                repository.updateTask(
                    Task(
                        id = editingTaskId,
                        title = title,
                        description = description,
                        time = time,
                        isCompleted = previousTask?.isCompleted ?: false
                    )
                )
            }
            formState.update {
                it.copy(
                    newTitle = "",
                    newDescription = "",
                    newTime = "",
                    editingTaskId = null
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

    fun deleteCompletedTasks() {
        val completedTaskIds = uiState.value.tasks
            .filter { it.isCompleted }
            .map { it.id }
        if (completedTaskIds.isEmpty()) return

        viewModelScope.launch {
            completedTaskIds.forEach { taskId ->
                repository.deleteTask(taskId)
            }
        }
    }
}
