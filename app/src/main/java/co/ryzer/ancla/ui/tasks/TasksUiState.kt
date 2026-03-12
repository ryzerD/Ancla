package co.ryzer.ancla.ui.tasks

import co.ryzer.ancla.data.Task

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val newTitle: String = "",
    val newDescription: String = "",
    val newTime: String = ""
) {
    val pendingTasks: List<Task>
        get() = tasks.filterNot { it.isCompleted }
}

