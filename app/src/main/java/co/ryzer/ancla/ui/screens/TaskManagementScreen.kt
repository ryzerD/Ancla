package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.ryzer.ancla.R
import co.ryzer.ancla.data.Task
import co.ryzer.ancla.ui.tasks.TasksUiState
import co.ryzer.ancla.ui.tasks.TasksViewModel
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.ScriptReaderButton
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

private data class TaskFormSnapshot(
    val title: String = "",
    val description: String = "",
    val startTime: String = "08:00",
    val endTime: String = "09:00",
    val category: String = "Rutina"
) {
    companion object {
        fun fromTask(task: Task): TaskFormSnapshot = TaskFormSnapshot(
            title = task.title,
            description = task.description,
            startTime = task.startTime,
            endTime = task.endTime,
            category = task.category
        )

        fun fromUiState(uiState: TasksUiState): TaskFormSnapshot = TaskFormSnapshot(
            title = uiState.newTitle,
            description = uiState.newDescription,
            startTime = uiState.newStartTime,
            endTime = uiState.newEndTime,
            category = uiState.newCategory
        )
    }
}

private data class PendingToggleAction(
    val taskId: String,
    val targetCompleted: Boolean
)

@Composable
fun TaskManagementScreen(
    onBack: () -> Unit,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    TaskManagementContent(
        uiState = uiState,
        onBack = onBack,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onStartTimeChange = viewModel::onStartTimeChange,
        onEndTimeChange = viewModel::onEndTimeChange,
        onCategoryChange = viewModel::onCategoryChange,
        onAddTask = viewModel::addTask,
        onCancelEdit = viewModel::cancelEditing,
        onToggleCompleted = { taskId, isCompleted ->
            viewModel.setTaskCompleted(taskId, isCompleted)
        },
        onEditTask = viewModel::startEditing,
        onDeleteTask = viewModel::deleteTask,
        saveErrorEvents = viewModel.saveError,
        saveSuccessEvents = viewModel.saveSuccess
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskManagementContent(
    uiState: TasksUiState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onAddTask: () -> Unit,
    onCancelEdit: () -> Unit,
    onToggleCompleted: (taskId: String, isCompleted: Boolean) -> Unit,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (String) -> Unit,
    saveErrorEvents: Flow<String> = emptyFlow(),
    saveSuccessEvents: Flow<Unit> = emptyFlow()
) {
    var showFormSheet by remember { mutableStateOf(false) }
    var formErrorMessage by remember { mutableStateOf<String?>(null) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var pendingToggleAction by remember { mutableStateOf<PendingToggleAction?>(null) }
    var pendingDeleteTaskId by remember { mutableStateOf<String?>(null) }
    var initialFormSnapshot by remember { mutableStateOf<TaskFormSnapshot?>(null) }
    val currentSnapshot = TaskFormSnapshot.fromUiState(uiState)
    val hasUnsavedChanges by remember(currentSnapshot, initialFormSnapshot) {
        derivedStateOf {
            initialFormSnapshot != null && currentSnapshot != initialFormSnapshot
        }
    }
    val latestHasUnsavedChanges by rememberUpdatedState(hasUnsavedChanges)
    val formSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { targetValue ->
            if (targetValue == SheetValue.Hidden && latestHasUnsavedChanges) {
                showDiscardDialog = true
                false
            } else {
                true
            }
        }
    )
    val isImeVisible = WindowInsets.isImeVisible
    val sheetHeightFraction = if (isImeVisible) 0.92f else 0.74f

    fun closeSheet(forceDiscard: Boolean = false) {
        if (!forceDiscard && hasUnsavedChanges) {
            showDiscardDialog = true
            return
        }
        showDiscardDialog = false
        showFormSheet = false
        formErrorMessage = null
        initialFormSnapshot = null
        onCancelEdit()
    }

    LaunchedEffect(uiState.editingTaskId) {
        if (uiState.editingTaskId != null) {
            showFormSheet = true
        }
    }

    LaunchedEffect(saveErrorEvents) {
        saveErrorEvents.collect { message ->
            formErrorMessage = message
            showFormSheet = true
        }
    }

    LaunchedEffect(saveSuccessEvents) {
        saveSuccessEvents.collect {
            formErrorMessage = null
            showDiscardDialog = false
            initialFormSnapshot = null
            showFormSheet = false
        }
    }

    Scaffold(
        containerColor = AnclaBackground,
        contentWindowInsets = WindowInsets(0),
        topBar = { TaskManagementTopBar(onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onCancelEdit()
                    formErrorMessage = null
                    showDiscardDialog = false
                    initialFormSnapshot = TaskFormSnapshot()
                    showFormSheet = true
                },
                containerColor = ScriptReaderButton,
                contentColor = SurfaceWhite
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.tasks_add_button)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            TaskTodaySection(
                tasks = uiState.tasks,
                pendingCount = uiState.pendingTasks.size,
                onToggleCompleted = { taskId, isCompleted ->
                    pendingToggleAction = PendingToggleAction(
                        taskId = taskId,
                        targetCompleted = isCompleted
                    )
                },
                onEditTask = {
                    onEditTask(it)
                    initialFormSnapshot = TaskFormSnapshot.fromTask(it)
                    showDiscardDialog = false
                    showFormSheet = true
                },
                onDeleteTask = { taskId ->
                    pendingDeleteTaskId = taskId
                },
                modifier = Modifier.weight(1f)
            )
        }

        if (showFormSheet) {
            ModalBottomSheet(
                onDismissRequest = { closeSheet() },
                sheetState = formSheetState,
                containerColor = AnclaBackground,
                contentWindowInsets = { WindowInsets(0) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(sheetHeightFraction)
                        .imePadding()
                        .navigationBarsPadding()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        formErrorMessage?.let { message ->
                            item {
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = message,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = AnclaTextStyles.toolCardSubtitle,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }

                        item {
                            TaskFormSection(
                                uiState = uiState,
                                onTitleChange = {
                                    formErrorMessage = null
                                    onTitleChange(it)
                                },
                                onDescriptionChange = {
                                    formErrorMessage = null
                                    onDescriptionChange(it)
                                },
                                onStartTimeChange = {
                                    formErrorMessage = null
                                    onStartTimeChange(it)
                                },
                                onEndTimeChange = {
                                    formErrorMessage = null
                                    onEndTimeChange(it)
                                },
                                onCategoryChange = {
                                    formErrorMessage = null
                                    onCategoryChange(it)
                                },
                                onAddTask = onAddTask,
                                onCancelEdit = { closeSheet() }
                            )
                        }
                    }
                }
            }
        }

        if (showDiscardDialog) {
            AlertDialog(
                onDismissRequest = { showDiscardDialog = false },
                title = { Text(text = stringResource(R.string.tasks_discard_dialog_title)) },
                text = { Text(text = stringResource(R.string.tasks_discard_dialog_message)) },
                confirmButton = {
                    TextButton(onClick = { closeSheet(forceDiscard = true) }) {
                        Text(text = stringResource(R.string.tasks_discard_dialog_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDiscardDialog = false }) {
                        Text(text = stringResource(R.string.dialog_cancel))
                    }
                }
            )
        }

        pendingDeleteTaskId?.let { taskId ->
            val taskName = uiState.tasks.firstOrNull { it.id == taskId }?.title ?: "esta tarea"
            AlertDialog(
                onDismissRequest = { pendingDeleteTaskId = null },
                title = { Text(text = stringResource(R.string.tasks_delete_dialog_title)) },
                text = { Text(text = stringResource(R.string.tasks_delete_dialog_message, taskName)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteTask(taskId)
                            pendingDeleteTaskId = null
                        }
                    ) {
                        Text(text = stringResource(R.string.dialog_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingDeleteTaskId = null }) {
                        Text(text = stringResource(R.string.dialog_cancel))
                    }
                }
            )
        }

        pendingToggleAction?.let { action ->
            val taskName = uiState.tasks.firstOrNull { it.id == action.taskId }?.title ?: "esta tarea"
            AlertDialog(
                onDismissRequest = { pendingToggleAction = null },
                title = {
                    Text(
                        text = if (action.targetCompleted) {
                            stringResource(R.string.tasks_complete_confirm_title)
                        } else {
                            stringResource(R.string.tasks_reopen_confirm_title)
                        }
                    )
                },
                text = {
                    Text(
                        text = if (action.targetCompleted) {
                            stringResource(R.string.tasks_complete_confirm_message, taskName)
                        } else {
                            stringResource(R.string.tasks_reopen_confirm_message, taskName)
                        }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onToggleCompleted(action.taskId, action.targetCompleted)
                            pendingToggleAction = null
                        }
                    ) {
                        Text(text = stringResource(R.string.dialog_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingToggleAction = null }) {
                        Text(text = stringResource(R.string.dialog_cancel))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskManagementTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                stringResource(R.string.tasks_screen_title),
                style = AnclaTextStyles.sectionLabel,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.tasks_back_content_description),
                    tint = TextPrimary
                )
            }
        },
        windowInsets = WindowInsets(0),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AnclaBackground,
            titleContentColor = TextPrimary,
            navigationIconContentColor = TextPrimary
        )
    )
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun TaskManagementScreenPreview() {
    AnclaTheme {
        TaskManagementContent(
            uiState = TasksUiState(
                tasks = listOf(
                    Task(
                        title = "Completar reporte mensual",
                        description = "Revisar los datos financieros del mes pasado.",
                        startTime = "09:00",
                        endTime = "10:30",
                        category = "Trabajo"
                    ),
                    Task(
                        title = "Ir al gimnasio",
                        description = "Sesión de cardio y pesas.",
                        startTime = "18:00",
                        endTime = "19:30",
                        category = "Salud",
                        completedAt = System.currentTimeMillis()
                    ),
                    Task(
                        title = "Comprar víveres",
                        description = "Frutas, verduras y leche.",
                        startTime = "17:00",
                        endTime = "17:45",
                        category = "Rutina"
                    )
                )
            ),
            onBack = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onStartTimeChange = {},
            onEndTimeChange = {},
            onCategoryChange = {},
            onAddTask = {},
            onCancelEdit = {},
            onToggleCompleted = { _, _ -> },
            onEditTask = {},
            onDeleteTask = {},
            saveErrorEvents = emptyFlow(),
            saveSuccessEvents = emptyFlow()
        )
    }
}
