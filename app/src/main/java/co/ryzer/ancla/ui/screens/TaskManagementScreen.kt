package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.ryzer.ancla.R
import co.ryzer.ancla.data.Task
import co.ryzer.ancla.ui.components.AnclaOutlinedButton
import co.ryzer.ancla.ui.components.AnclaPrimaryButton
import co.ryzer.ancla.ui.components.AnclaTextField
import co.ryzer.ancla.ui.components.AnclaTimePickerField
import co.ryzer.ancla.ui.tasks.TasksViewModel
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.CardPeach
import co.ryzer.ancla.ui.theme.ScriptReaderButton
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagementScreen(
    onBack: () -> Unit,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = AnclaBackground,
        contentWindowInsets = WindowInsets(0),
        topBar = {
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
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            TaskForm(
                uiState = uiState,
                onTitleChange = viewModel::onTitleChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onStartTimeChange = viewModel::onStartTimeChange,
                onEndTimeChange = viewModel::onEndTimeChange,
                onCategoryChange = viewModel::onCategoryChange,
                onAddTask = viewModel::addTask,
                onCancelEdit = viewModel::cancelEditing
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.tasks_today_label),
                    style = AnclaTextStyles.sectionLabel,
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(CardPeach)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(
                            R.string.tasks_remaining_count,
                            uiState.pendingTasks.size
                        ),
                        style = AnclaTextStyles.toolCardSubtitle,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(uiState.tasks.sortedBy { it.isCompleted }) { task ->
                    TaskListItem(
                        task = task,
                        onToggleCompleted = {
                            viewModel.setTaskCompleted(task.id, !task.isCompleted)
                        },
                        onEdit = { viewModel.startEditing(task) },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskForm(
    uiState: co.ryzer.ancla.ui.tasks.TasksUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onAddTask: () -> Unit,
    onCancelEdit: () -> Unit
) {
    val categories = listOf(
        stringResource(R.string.tasks_category_routine),
        stringResource(R.string.tasks_category_health),
        stringResource(R.string.tasks_category_work)
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.tasks_form_title),
                style = AnclaTextStyles.sectionLabel,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AnclaTextField(
                value = uiState.newTitle,
                onValueChange = onTitleChange,
                singleLine = true,
                placeholder = { Text(stringResource(R.string.tasks_field_title_placeholder)) },
                containerAlpha = 0.9f
            )

            Spacer(modifier = Modifier.height(12.dp))

            AnclaTextField(
                value = uiState.newDescription,
                onValueChange = onDescriptionChange,
                placeholder = { Text(stringResource(R.string.tasks_field_description_placeholder)) },
                containerAlpha = 0.9f
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.tasks_field_category_label),
                style = AnclaTextStyles.sectionLabel,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { category ->
                    val selected = category == uiState.newCategory
                    FilterChip(
                        selected = selected,
                        onClick = { onCategoryChange(category) },
                        label = {
                            Text(
                                text = category,
                                style = AnclaTextStyles.toolCardSubtitle
                            )
                        },
                        shape = RoundedCornerShape(999.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CardGreen,
                            selectedLabelColor = TextPrimary,
                            containerColor = CardPeach,
                            labelColor = TextPrimary
                        ),
                        border = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.tasks_schedule_label),
                style = AnclaTextStyles.sectionLabel,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnclaTimePickerField(
                    value = uiState.newStartTime,
                    timeLabel = stringResource(R.string.tasks_schedule_start),
                    onValueChange = onStartTimeChange,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.tasks_schedule_between),
                    style = AnclaTextStyles.sectionLabel,
                    color = TextSecondary
                )
                AnclaTimePickerField(
                    value = uiState.newEndTime,
                    timeLabel = stringResource(R.string.tasks_schedule_end),
                    onValueChange = onEndTimeChange,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                if (uiState.editingTaskId != null) {
                    AnclaOutlinedButton(
                        text = stringResource(R.string.dialog_cancel),
                        onClick = onCancelEdit,
                        modifier = Modifier.weight(1f),
                        contentColor = TextSecondary,
                        borderColor = CardLavender
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                AnclaPrimaryButton(
                    text = if (uiState.editingTaskId == null) {
                        stringResource(R.string.tasks_add_short)
                    } else {
                        stringResource(R.string.tasks_update_short)
                    },
                    onClick = onAddTask,
                    modifier = Modifier.weight(1f),
                    leadingIcon = Icons.Default.Add,
                    containerColor = ScriptReaderButton,
                    contentColor = SurfaceWhite
                )
            }
        }
    }
}


@Composable
private fun TaskListItem(
    task: Task,
    onToggleCompleted: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                CardPeach.copy(alpha = 0.35f)
            } else {
                SurfaceWhite
            }
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !task.isCompleted) { onEdit() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleCompleted) {
                Icon(
                    imageVector = if (task.isCompleted) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Outlined.RadioButtonUnchecked
                    },
                    contentDescription = if (task.isCompleted) {
                        stringResource(R.string.tasks_reopen_content_description)
                    } else {
                        stringResource(R.string.tasks_complete_content_description)
                    },
                    tint = ScriptReaderButton
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = AnclaTextStyles.toolCardTitle,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = AnclaTextStyles.toolCardSubtitle,
                        color = TextSecondary,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CardLavender.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.category,
                            style = AnclaTextStyles.toolCardSubtitle,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${task.startTime} - ${task.endTime}",
                        style = AnclaTextStyles.taskTime,
                        color = TextSecondary
                    )
                }
            }

            if (task.isCompleted) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.tasks_delete_content_description),
                        tint = TextSecondary.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
