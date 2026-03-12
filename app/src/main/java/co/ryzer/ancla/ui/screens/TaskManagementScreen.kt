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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import co.ryzer.ancla.R
import co.ryzer.ancla.data.Task
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
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
    onToggleCompleted: (String, Boolean) -> Unit,
    onDeleteTask: (String) -> Unit
) {
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
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.tasks_field_title)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(ToolsScreenDimens.orderControlsSpacing))
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.tasks_field_description)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(ToolsScreenDimens.orderControlsSpacing))
        OutlinedTextField(
            value = time,
            onValueChange = onTimeChange,
            label = { Text(stringResource(R.string.tasks_field_time)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
        Button(onClick = onAddTask, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.tasks_add_button))
        }

        Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.orderControlsSpacing),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items = tasks, key = { it.id }) { task ->
                TaskRow(
                    task = task,
                    onToggleCompleted = onToggleCompleted,
                    onDeleteTask = onDeleteTask
                )
            }
        }
    }
}

@Composable
private fun TaskRow(
    task: Task,
    onToggleCompleted: (String, Boolean) -> Unit,
    onDeleteTask: (String) -> Unit
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
            IconButton(onClick = { onDeleteTask(task.id) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.tasks_delete_content_description)
                )
            }
        }
    }
}

