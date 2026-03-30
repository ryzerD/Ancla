package co.ryzer.ancla.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ryzer.ancla.data.Task
import co.ryzer.ancla.data.repository.TaskRepository
import co.ryzer.ancla.notifications.TaskAlarmManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val alarmManager: TaskAlarmManager
) : ViewModel() {

    companion object {
        const val TITLE_MAX_LENGTH = 60
        const val DESCRIPTION_MAX_LENGTH = 200
    }

    private val formState = MutableStateFlow(TasksUiState())

    private val _currentActivity = MutableStateFlow<Task?>(null)

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

    init {
        startActivityTicker()
    }

    private fun startActivityTicker() {
        viewModelScope.launch {
            while (isActive) {
                refreshCurrentActivity()
                val now = LocalTime.now()
                val secondsUntilNextMinute = 60 - now.second
                delay(secondsUntilNextMinute * 1000L)
            }
        }
    }

    private fun refreshCurrentActivity() {
        val now = LocalTime.now()
        val tasks = uiState.value.tasks

        // Filtramos para encontrar la actividad actual que NO esté completada
        _currentActivity.value = tasks.find { task ->
            if (task.isCompleted) return@find false // Ignorar si ya está terminada

            try {
                val start = LocalTime.parse(task.startTime)
                val end = LocalTime.parse(task.endTime)
                if (start.isBefore(end)) {
                    !now.isBefore(start) && now.isBefore(end)
                } else {
                    // Over midnight
                    !now.isBefore(start) || now.isBefore(end)
                }
            } catch (_: Exception) {
                false
            }
        }
    }

    fun onTitleChange(value: String) {
        formState.update { it.copy(newTitle = value.take(TITLE_MAX_LENGTH)) }
    }

    fun onDescriptionChange(value: String) {
        formState.update { it.copy(newDescription = value.take(DESCRIPTION_MAX_LENGTH)) }
    }

    fun onStartTimeChange(value: String) {
        formState.update { it.copy(newStartTime = value) }
    }

    fun onEndTimeChange(value: String) {
        formState.update { it.copy(newEndTime = value) }
    }

    fun onCategoryChange(value: String) {
        formState.update { it.copy(newCategory = value) }
    }

    fun startEditing(task: Task) {
        formState.update {
            it.copy(
                newTitle = task.title,
                newDescription = task.description,
                newStartTime = task.startTime,
                newEndTime = task.endTime,
                newCategory = task.category,
                editingTaskId = task.id
            )
        }
    }

    fun cancelEditing() {
        formState.update {
            it.copy(
                newTitle = "",
                newDescription = "",
                newStartTime = "08:00",
                newEndTime = "09:00",
                newCategory = "Rutina",
                editingTaskId = null
            )
        }
    }

    fun addTask() {
        val currentState = uiState.value
        val title = currentState.newTitle.trim()
        val description = currentState.newDescription.trim()
        val startTime = currentState.newStartTime
        val endTime = currentState.newEndTime
        val category = currentState.newCategory

        if (title.isBlank()) return

        viewModelScope.launch {
            val editingTaskId = currentState.editingTaskId
            if (editingTaskId == null) {
                val newTask = Task(
                    title = title,
                    description = description,
                    startTime = startTime,
                    endTime = endTime,
                    category = category
                )
                repository.addTask(newTask)
                alarmManager.scheduleAlarm(newTask)
            } else {
                val previousTask = currentState.tasks.firstOrNull { it.id == editingTaskId }
                val updatedTask = Task(
                    id = editingTaskId,
                    title = title,
                    description = description,
                    startTime = startTime,
                    endTime = endTime,
                    category = category,
                    startedAt = previousTask?.startedAt,
                    completedAt = previousTask?.completedAt
                )
                repository.updateTask(updatedTask)
                alarmManager.scheduleAlarm(updatedTask)
            }
            cancelEditing()
        }
    }

    fun setTaskCompleted(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.setTaskCompleted(taskId = taskId, isCompleted = isCompleted)

            val updatedTasks = uiState.value.tasks.map {
                if (it.id == taskId) {
                    it.copy(
                        completedAt = if (isCompleted) System.currentTimeMillis() else null
                    )
                } else {
                    it
                }
            }
            refreshCurrentActivityWithList(updatedTasks)

            val task = updatedTasks.firstOrNull { it.id == taskId }
            if (task != null) {
                if (isCompleted) alarmManager.cancelAlarm(task)
                else alarmManager.scheduleAlarm(task)
            }
        }
    }

    fun onHomeTaskPrimaryAction(taskId: String) {
        viewModelScope.launch {
            // Read from repository first to avoid acting on stale in-memory task state.
            val task = repository.getTaskById(taskId)
                ?: uiState.value.tasks.firstOrNull { it.id == taskId }
                ?: return@launch

            if (task.isCompleted) return@launch

            if (task.isInProgress) {
                // Complete the task when it is already in progress.
                setTaskCompleted(taskId = taskId, isCompleted = true)
            } else {
                // Start the task.
                repository.setTaskInProgress(taskId = taskId, isInProgress = true)

                // Optimistic refresh so Home updates immediately even before Flow emission.
                val startedAtNow = System.currentTimeMillis()
                val updatedTasks = uiState.value.tasks.map {
                    if (it.id == taskId) {
                        it.copy(startedAt = it.startedAt ?: startedAtNow, completedAt = null)
                    } else {
                        it
                    }
                }
                refreshCurrentActivityWithList(updatedTasks)
            }
        }
    }

    private fun refreshCurrentActivityWithList(tasks: List<Task>) {
        val now = LocalTime.now()
        _currentActivity.value = tasks.find { task ->
            if (task.isCompleted) return@find false
            try {
                val start = LocalTime.parse(task.startTime)
                val end = LocalTime.parse(task.endTime)
                if (start.isBefore(end)) {
                    !now.isBefore(start) && now.isBefore(end)
                } else {
                    !now.isBefore(start) || now.isBefore(end)
                }
            } catch (_: Exception) {
                false
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            val task = uiState.value.tasks.firstOrNull { it.id == taskId }
            if (task != null) alarmManager.cancelAlarm(task)
            repository.deleteTask(taskId)
        }
    }
}
