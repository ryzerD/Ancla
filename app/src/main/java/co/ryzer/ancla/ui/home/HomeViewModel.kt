package co.ryzer.ancla.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ryzer.ancla.data.Task
import co.ryzer.ancla.data.repository.TaskRepository
import co.ryzer.ancla.data.preferences.PostponementPreferencesManager
import co.ryzer.ancla.notifications.NotificationHelper
import co.ryzer.ancla.notifications.TaskAlarmManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import co.ryzer.ancla.data.withPostponementOffset

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val taskAlarmManager: TaskAlarmManager,
    @param:ApplicationContext private val appContext: Context
) : ViewModel() {

    companion object {
        private const val TICKER_INTERVAL_MILLIS = 60_000L
        private const val PREPARING_BUFFER_MINUTES = 15L
        private const val MINUTES_PER_DAY = 1_440L
    }

    private val recoveryMode = MutableStateFlow(
        NotificationHelper.areTaskNotificationsSilenced(appContext)
    )
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Flow persistente del postponement desde DataStore
    private val persistentPostponementFlow: Flow<Long> = 
        PostponementPreferencesManager.getPostponementMinutesFlow(appContext)

    // Detecta cambio de día y limpia offset automáticamente a medianoche
    private val midnightDetector: Flow<Unit> = flow {
        var lastDate = LocalDate.now()
        while (true) {
            delay(60_000L)  // Chequear cada minuto
            val today = LocalDate.now()
            if (today.isAfter(lastDate)) {
                lastDate = today
                // Guardar limpieza en DataStore (persistencia)
                PostponementPreferencesManager.clearPostponement(appContext)
                emit(Unit)
            }
        }
    }

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
    }.combine(persistentPostponementFlow) { candidate, offset ->
        candidate?.withPostponementOffset(offset)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        recoveryMode,
        nowTicker,
        candidateTaskFlow,
        taskRepository.observeTasks(),
        persistentPostponementFlow
    ) { recovery, now, candidateTask, allTasks, offset ->
        // Aplicar offset visual a todas las tareas
        val tasksWithOffset = if (offset > 0L) {
            allTasks.map { it.withPostponementOffset(offset) }
        } else {
            allTasks
        }

        val overlapNow = hasOverlapNow(allTasks, now)
        
        // If no candidate in the immediate window, get the next pending task
        val taskToShow = candidateTask ?: tasksWithOffset.filter { !it.isCompleted }
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
            hasOverlap = overlapNow && offset == 0L,
            isRecoveryMode = recovery
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    val homeDisplayState: StateFlow<HomeDisplayState> = combine(
        recoveryMode,
        uiState,
        persistentPostponementFlow
    ) { recovery, homeState, postponementOffset ->
        val taskToShow = if (recovery) null else homeState.currentTask
        val content = when {
            recovery -> HomeContent.RECOVERY
            taskToShow != null -> HomeContent.TASK
            else -> HomeContent.REST
        }

        HomeDisplayState(
            isRecoveryMode = recovery,
            currentTask = taskToShow,
            content = content,
            currentPostponementMinutes = postponementOffset
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeDisplayState()
    )

    fun toggleRecoveryMode() {
        recoveryMode.update { previous ->
            val enabled = !previous
            NotificationHelper.setTaskNotificationsSilenced(appContext, enabled)
            enabled
        }
    }

    fun postponeAllRemaining(minutes: Long) {
        if (minutes <= 0L) return
        // Actualiza el offset de postponement
        val safeMinutes = normalizeMinutes(minutes)
        if (safeMinutes == 0L) return

        viewModelScope.launch {
            try {
                // Obtener el valor actual desde DataStore (solo el primer valor)
                val currentPostponement = persistentPostponementFlow.first()
                val newPostponementValue = normalizeMinutes(currentPostponement + safeMinutes)
                
                // Guardar el nuevo valor en DataStore (persistencia)
                PostponementPreferencesManager.savePostponementMinutes(appContext, newPostponementValue)
                
                // Reprogramar alarmas de tareas pendientes con el nuevo offset
                val pendingTasks = taskRepository.observeTasks().stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = emptyList()
                ).value.filter { !it.isCompleted }

                for (task in pendingTasks) {
                    taskAlarmManager.scheduleAlarmWithPostponement(task, newPostponementValue)
                }
            } catch (_: Exception) {
                // Si falla reprogramación de alarmas, continuamos
            }
        }
    }

    fun reducePostponement(minutes: Long) {
        if (minutes <= 0L) return
        val safeMinutes = normalizeMinutes(minutes)
        if (safeMinutes == 0L) return

        viewModelScope.launch {
            try {
                // Obtener el valor actual desde DataStore (solo el primer valor)
                val currentPostponement = persistentPostponementFlow.first()
                val newPostponementValue = (currentPostponement - safeMinutes).coerceAtLeast(0L)

                // Guardar el nuevo valor en DataStore (persistencia)
                PostponementPreferencesManager.savePostponementMinutes(appContext, newPostponementValue)

                // Reprogramar alarmas con el nuevo offset reducido
                val pendingTasks = taskRepository.observeTasks().stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = emptyList()
                ).value.filter { !it.isCompleted }

                for (task in pendingTasks) {
                    if (newPostponementValue > 0L) {
                        taskAlarmManager.scheduleAlarmWithPostponement(task, newPostponementValue)
                    } else {
                        taskAlarmManager.scheduleAlarm(task)
                    }
                }
            } catch (_: Exception) {
                // Si falla reprogramación, continuamos
            }
        }
    }

    fun clearPostponement() {
        // Guardar limpieza en DataStore (persistencia)
        viewModelScope.launch {
            PostponementPreferencesManager.clearPostponement(appContext)
        }

        // Reprogramar alarmas a hora original
        viewModelScope.launch {
            try {
                val pendingTasks = taskRepository.observeTasks().stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = emptyList()
                ).value.filter { !it.isCompleted }

                for (task in pendingTasks) {
                    taskAlarmManager.scheduleAlarm(task)
                }
            } catch (_: Exception) {
                // Si falla reprogramación, continuamos
            }
        }
    }

    private fun normalizeMinutes(minutes: Long): Long {
        val modulo = minutes % MINUTES_PER_DAY
        return if (modulo >= 0L) modulo else modulo + MINUTES_PER_DAY
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
            // Rango normal: 08:00-09:00
            // Activo si: now >= start AND now < end (NO incluye el end exacto)
            !now.isBefore(start) && now.isBefore(end)
        } else {
            // Rango overnight: 22:00-02:00
            // Activo si: now >= start OR now < end (NO incluye el end exacto)
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

    init {
        // Suscribirse a detector de medianoche para limpiar offset automáticamente
        viewModelScope.launch {
            @Suppress("UNREACHABLE_CODE")
            midnightDetector.collect { }
        }
    }
}


