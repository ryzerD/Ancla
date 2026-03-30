package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
        onDeleteTask = viewModel::deleteTask
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
    onDeleteTask: (String) -> Unit
) {
    var showFormSheet by remember { mutableStateOf(false) }
    val formSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(uiState.editingTaskId) {
        if (uiState.editingTaskId != null) {
            showFormSheet = true
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
                onToggleCompleted = onToggleCompleted,
                onEditTask = {
                    onEditTask(it)
                    showFormSheet = true
                },
                onDeleteTask = onDeleteTask,
                modifier = Modifier.weight(1f)
            )
        }

        if (showFormSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showFormSheet = false
                    onCancelEdit()
                },
                sheetState = formSheetState,
                containerColor = AnclaBackground,
                contentWindowInsets = { BottomSheetDefaults.modalWindowInsets }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp)
                ) {
                    TaskFormSection(
                        uiState = uiState,
                        onTitleChange = onTitleChange,
                        onDescriptionChange = onDescriptionChange,
                        onStartTimeChange = onStartTimeChange,
                        onEndTimeChange = onEndTimeChange,
                        onCategoryChange = onCategoryChange,
                        onAddTask = {
                            onAddTask()
                            if (uiState.newTitle.isNotBlank()) showFormSheet = false
                        },
                        onCancelEdit = {
                            onCancelEdit()
                            showFormSheet = false
                        }
                    )
                }
            }
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
            onDeleteTask = {}
        )
    }
}
