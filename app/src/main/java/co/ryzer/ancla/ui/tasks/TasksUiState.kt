package co.ryzer.ancla.ui.tasks

import co.ryzer.ancla.data.Task

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val newTitle: String = "",
    val newDescription: String = "",
    val newStartTime: String = "08:00",
    val newEndTime: String = "09:00",
    val newCategory: String = "Rutina",
    val editingTaskId: String? = null
) {
    val pendingTasks: List<Task>
        get() = tasks.filterNot { it.isCompleted }
}
