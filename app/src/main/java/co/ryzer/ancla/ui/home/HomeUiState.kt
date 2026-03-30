package co.ryzer.ancla.ui.home
import co.ryzer.ancla.data.Task
data class HomeUiState(
    val currentTask: Task? = null,
    val activityState: ActivityState = ActivityState.SCHEDULED,
    val hasOverlap: Boolean = false,
    val isRecoveryMode: Boolean = false
)

enum class HomeContent {
    RECOVERY,
    TASK,
    REST
}

data class HomeDisplayState(
    val isRecoveryMode: Boolean = false,
    val currentTask: Task? = null,
    val content: HomeContent = HomeContent.REST
)

