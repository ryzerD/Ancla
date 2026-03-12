package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.data.Task
import co.ryzer.ancla.ui.tasks.TasksViewModel
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens

@Composable
fun TaskManagementScreen(
    title: String,
    description: String,
    time: String,
    tasks: List<Task>,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onAddTask: () -> Unit,
    isEditing: Boolean,
    onToggleCompleted: (String, Boolean) -> Unit,
    onDeleteTask: (String) -> Unit,
    onStartEditTask: (Task) -> Unit,
    onCancelEditing: () -> Unit,
) {
    val titleRemaining = TasksViewModel.TITLE_MAX_LENGTH - title.length
    val descriptionRemaining = TasksViewModel.DESCRIPTION_MAX_LENGTH - description.length
    var taskPendingDelete by remember { mutableStateOf<Task?>(null) }
    val taskFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedLabelColor = TextPrimary,
        unfocusedLabelColor = TextSecondary,
        focusedBorderColor = TextPrimary,
        unfocusedBorderColor = TextSecondary,
        cursorColor = TextPrimary,
        focusedSupportingTextColor = TextSecondary,
        unfocusedSupportingTextColor = TextSecondary
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(
                horizontal = ToolsScreenDimens.horizontalPaddingCompact,
                vertical = ToolsScreenDimens.verticalPadding
            )
    ) {
        Text(
            text = stringResource(R.string.tasks_screen_title),
            style = AnclaTextStyles.toolsTitle,
            color = TextPrimary
        )
        Text(
            text = stringResource(R.string.tasks_screen_subtitle),
            style = AnclaTextStyles.toolCardSubtitle,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))

        OutlinedTextField(
            value = title,
            onValueChange = { value ->
                if (value.length <= TasksViewModel.TITLE_MAX_LENGTH) {
                    onTitleChange(value)
                }
            },
            label = { Text(stringResource(R.string.tasks_field_title)) },
            supportingText = if (titleRemaining <= TasksViewModel.TITLE_COUNTER_THRESHOLD) {
                {
                    Text(stringResource(R.string.tasks_remaining_characters, titleRemaining))
                }
            } else {
                null
            },
            colors = taskFieldColors,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(ToolsScreenDimens.orderControlsSpacing))
        OutlinedTextField(
            value = description,
            onValueChange = { value ->
                if (value.length <= TasksViewModel.DESCRIPTION_MAX_LENGTH) {
                    onDescriptionChange(value)
                }
            },
            label = { Text(stringResource(R.string.tasks_field_description)) },
            supportingText = if (descriptionRemaining <= TasksViewModel.DESCRIPTION_COUNTER_THRESHOLD) {
                {
                    Text(stringResource(R.string.tasks_remaining_characters, descriptionRemaining))
                }
            } else {
                null
            },
            colors = taskFieldColors,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(ToolsScreenDimens.orderControlsSpacing))

        // Time is selected via a TimePicker dialog – no free text allowed.
        TimePickerField(
            time = time,
            onTimeSelected = onTimeChange,
            colors = taskFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
        Button(onClick = onAddTask, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isEditing) {
                    stringResource(R.string.tasks_save_button)
                } else {
                    stringResource(R.string.tasks_add_button)
                }
            )
        }

        if (isEditing) {
            Spacer(modifier = Modifier.height(ToolsScreenDimens.orderControlsSpacing))
            TextButton(
                onClick = onCancelEditing,
                colors = ButtonDefaults.textButtonColors(contentColor = TextPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.tasks_cancel_edit_button))
            }
        }

        Spacer(modifier = Modifier.height(ToolsScreenDimens.orderControlsSpacing))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.orderControlsSpacing),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items = tasks, key = { it.id }) { task ->
                TaskRow(
                    task = task,
                    onToggleCompleted = onToggleCompleted,
                    onRequestDeleteTask = { taskPendingDelete = task },
                    onEditTask = onStartEditTask
                )
            }
        }

        taskPendingDelete?.let { task ->
            ConfirmDeleteTaskDialog(
                taskTitle = task.title,
                onDismiss = { taskPendingDelete = null },
                onConfirm = {
                    onDeleteTask(task.id)
                    taskPendingDelete = null
                }
            )
        }
    }
}

/**
 * Read-only text field that opens a Material3 [TimePicker] dialog on tap.
 * Stores the selected value as "HH:mm" (24-hour format).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerField(
    time: String,
    onTimeSelected: (String) -> Unit,
    colors: androidx.compose.material3.TextFieldColors,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    // Parse the stored value so the picker pre-selects the right hour/minute.
    val initialHour = remember(time) {
        time.substringBefore(":", missingDelimiterValue = "08").toIntOrNull() ?: 8
    }
    val initialMinute = remember(time) {
        time.substringAfter(":", missingDelimiterValue = "00").toIntOrNull() ?: 0
    }
    val pickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    OutlinedTextField(
        value = time,
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(R.string.tasks_field_time)) },
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = stringResource(R.string.tasks_time_picker_description),
                    tint = TextPrimary
                )
            }
        },
        colors = colors,
        modifier = modifier
    )

    if (showPicker) {
        AnclaTimePickerDialog(
            state = pickerState,
            onDismiss = { showPicker = false },
            onConfirm = {
                onTimeSelected("%02d:%02d".format(pickerState.hour, pickerState.minute))
                showPicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnclaTimePickerDialog(
    state: TimePickerState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.dialog_confirm))
            }
        },
        text = {
            TimePicker(state = state)
        }
    )
}

@Composable
private fun TaskRow(
    task: Task,
    onToggleCompleted: (String, Boolean) -> Unit,
    onRequestDeleteTask: (Task) -> Unit,
    onEditTask: (Task) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ToolsScreenDimens.cardContentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ToolsScreenDimens.orderControlsSpacing)
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { checked -> onToggleCompleted(task.id, checked) }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, style = AnclaTextStyles.toolCardTitle, color = TextPrimary)
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = AnclaTextStyles.toolCardSubtitle,
                        color = TextSecondary
                    )
                }
                Text(
                    text = stringResource(R.string.task_time_format, task.time),
                    style = AnclaTextStyles.toolCardSubtitle,
                    color = TextSecondary
                )
            }
            IconButton(onClick = { onEditTask(task) }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.tasks_edit_content_description),
                    tint = TextPrimary
                )
            }
            IconButton(onClick = { onRequestDeleteTask(task) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.tasks_delete_content_description),
                    tint = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun ConfirmDeleteTaskDialog(
    taskTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.tasks_delete_dialog_title)) },
        text = { Text(stringResource(R.string.tasks_delete_dialog_message, taskTitle)) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.dialog_confirm))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TaskManagementScreenPreview() {
    AnclaTheme {
        TaskManagementScreen(
            title = "Morning Routine",
            description = "Start the day right",
            time = "07:30",
            tasks = listOf(
                Task(
                    title = "Meditation",
                    description = "10 minutes mindfulness",
                    time = "07:30",
                    isCompleted = true
                ),
                Task(
                    title = "Exercise",
                    description = "Light stretching",
                    time = "08:00",
                    isCompleted = false
                ),
                Task(
                    title = "Breakfast",
                    description = "Healthy meal",
                    time = "08:30",
                    isCompleted = false
                )
            ),
            onTitleChange = {},
            onDescriptionChange = {},
            onTimeChange = {},
            onAddTask = {},
            isEditing = false,
            onToggleCompleted = { _, _ -> },
            onDeleteTask = {},
            onStartEditTask = {},
            onCancelEditing = {},
        )
    }
}
