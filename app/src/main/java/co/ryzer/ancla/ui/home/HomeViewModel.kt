package co.ryzer.ancla.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ryzer.ancla.data.Task
import co.ryzer.ancla.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    companion object {
        private const val TICKER_INTERVAL_MILLIS = 60_000L
        private const val PREPARING_BUFFER_MINUTES = 15L
    }

    private val recoveryMode = MutableStateFlow(false)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    private val nowTicker: Flow<LocalTime> = flow {
        while (true) {
            emit(LocalTime.now())
            delay(TICKER_INTERVAL_MILLIS)
        }
    }

    private val candidateTaskFlow = nowTicker.flatMapLatest { now ->
        val nowText = now.format(timeFormatter)
        val preparingUntilText = now.plusMinutes(PREPARING_BUFFER_MINUTES).format(timeFormatter)
        taskRepository.observeHomeTaskCandidate(
            currentTime = nowText,
            preparingUntil = preparingUntilText
        )
    }

    val uiState: StateFlow<HomeUiState> = combine(
        recoveryMode,
        nowTicker,
        candidateTaskFlow,
        taskRepository.observeTasks()
    ) { recovery, now, candidateTask, allTasks ->
        val overlapNow = hasOverlapNow(allTasks, now)
        
        // If no candidate in the immediate window, get the next pending task
        val taskToShow = candidateTask ?: allTasks.filter { !it.isCompleted }
            .minByOrNull { task ->
                try {
                    val start = LocalTime.parse(task.startTime)
                    if (start.isAfter(now)) {
                        java.time.Duration.between(now, start).toMinutes()
                    } else {
                        Long.MAX_VALUE
                    }
                } catch (_: Exception) {
                    Long.MAX_VALUE
                }
            }

        val state = when {
            recovery -> ActivityState.RECOVERY_MODE
            taskToShow == null -> ActivityState.SCHEDULED
            isActive(taskToShow, now) && overlapNow -> ActivityState.OVERLAPPING
            isActive(taskToShow, now) -> ActivityState.ACTIVE
            isPreparing(taskToShow, now) -> ActivityState.PREPARING
            else -> ActivityState.SCHEDULED
        }

        HomeUiState(
            currentTask = if (recovery) null else taskToShow,
            activityState = state,
            hasOverlap = overlapNow,
            isRecoveryMode = recovery
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun toggleRecoveryMode() {
        recoveryMode.update { !it }
    }

    fun postponeAllRemaining(minutes: Long) {
        if (minutes <= 0L) return

        viewModelScope.launch {
            val nowText = LocalTime.now().format(timeFormatter)
            val pendingTasks = taskRepository.getPendingTasksStartingFrom(nowText)

            pendingTasks.forEach { task ->
                val shifted = shiftTaskByMinutes(task, minutes)
                taskRepository.updateTask(shifted)
            }
        }
    }

    private fun shiftTaskByMinutes(task: Task, minutes: Long): Task {
        return try {
            val shiftedStart = LocalTime.parse(task.startTime).plusMinutes(minutes)
            val shiftedEnd = LocalTime.parse(task.endTime).plusMinutes(minutes)
            task.copy(
                startTime = shiftedStart.format(timeFormatter),
                endTime = shiftedEnd.format(timeFormatter)
            )
        } catch (_: Exception) {
            task
        }
    }

    private fun isPreparing(task: Task, now: LocalTime): Boolean {
        val start = parseTime(task.startTime) ?: return false
        val minutesUntil = java.time.Duration.between(now, start).toMinutes()
        return minutesUntil in 0..PREPARING_BUFFER_MINUTES
    }

    private fun isActive(task: Task, now: LocalTime): Boolean {
        val start = parseTime(task.startTime) ?: return false
        val end = parseTime(task.endTime) ?: return false

        return if (start < end) {
            !now.isBefore(start) && now.isBefore(end)
        } else {
            !now.isBefore(start) || now.isBefore(end)
        }
    }

    private fun hasOverlapNow(tasks: List<Task>, now: LocalTime): Boolean {
        val activeTasks = tasks.filter { !it.isCompleted && isActive(it, now) }
        return activeTasks.size > 1
    }

    private fun parseTime(value: String): LocalTime? {
        return try {
            LocalTime.parse(value)
        } catch (_: Exception) {
            null
        }
    }
}


