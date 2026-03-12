package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.data.Task
import co.ryzer.ancla.ui.theme.*

@Composable
fun HomeScreen(
    userName: String = "",
    currentTasks: List<Task> = emptyList(),
    onTaskComplete: (String) -> Unit = {},
    windowSizeClass: WindowSizeClass? = null
) {
    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded
    val horizontalPadding =
        if (isExpanded) HomeScreenDimens.horizontalPaddingExpanded
        else HomeScreenDimens.horizontalPaddingCompact

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(horizontal = horizontalPadding)
    ) {
        Spacer(
            modifier = Modifier.height(
                if (isExpanded) HomeScreenDimens.topSpacerExpanded else HomeScreenDimens.topSpacerCompact
            )
        )

        Text(
            text = if (userName.isBlank()) stringResource(R.string.greeting)
                   else stringResource(R.string.greeting_user, userName),
            style = if (isExpanded) AnclaTextStyles.greetingExpanded else AnclaTextStyles.greeting,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = HomeScreenDimens.greetingBottomPadding)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (currentTasks.isEmpty()) {
                EmptyTasksState(isExpanded = isExpanded)
            } else {
                TaskCard(currentTasks.first(), onTaskComplete, isExpanded = isExpanded)
            }
        }

        Spacer(modifier = Modifier.height(HomeScreenDimens.bottomSpacer))
    }
}

@Composable
fun TaskCard(
    task: Task,
    onComplete: (String) -> Unit,
    isExpanded: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = if (isExpanded) HomeScreenDimens.cardMaxWidthExpanded else HomeScreenDimens.cardMaxWidthCompact),
        shape = RoundedCornerShape(HomeScreenDimens.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = HomeScreenDimens.cardElevation)
    ) {
        Column(
            modifier = Modifier.padding(
                if (isExpanded) HomeScreenDimens.cardPaddingExpanded else HomeScreenDimens.cardPaddingCompact
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF81A18B),
                modifier = Modifier.size(
                    if (isExpanded) HomeScreenDimens.taskIconSizeExpanded else HomeScreenDimens.taskIconSizeCompact
                )
            )
            Spacer(
                modifier = Modifier.height(
                    if (isExpanded) HomeScreenDimens.iconToContentSpacerExpanded else HomeScreenDimens.iconToContentSpacerCompact
                )
            )
            Text(
                text = stringResource(R.string.current_task_label),
                style = AnclaTextStyles.sectionLabel,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Text(
                text = task.title,
                style = if (isExpanded) AnclaTextStyles.taskTitleExpanded else AnclaTextStyles.taskTitle,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = HomeScreenDimens.taskTitleVerticalPadding)
            )
            Text(
                text = stringResource(R.string.task_time_format, task.time),
                style = AnclaTextStyles.taskTime,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(HomeScreenDimens.descriptionTopSpacer))
                Text(
                    text = task.description,
                    style = AnclaTextStyles.taskDescription,
                    color = TextSecondary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(
                modifier = Modifier.height(
                    if (isExpanded) HomeScreenDimens.contentToButtonSpacerExpanded else HomeScreenDimens.contentToButtonSpacerCompact
                )
            )

            Button(
                onClick = { onComplete(task.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = HomeScreenDimens.buttonMinHeight),
                colors = ButtonDefaults.buttonColors(containerColor = CardGreen),
                shape = RoundedCornerShape(HomeScreenDimens.buttonCornerRadius)
            ) {
                Text(
                    text = stringResource(R.string.btn_complete),
                    color = TextPrimary,
                    style = AnclaTextStyles.primaryButton
                )
            }
        }
    }
}

@Composable
fun EmptyTasksState(isExpanded: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HomeScreenDimens.emptyStatePadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = TextSecondary.copy(alpha = 0.3f),
            modifier = Modifier.size(
                if (isExpanded) HomeScreenDimens.emptyIconSizeExpanded else HomeScreenDimens.emptyIconSizeCompact
            )
        )
        Spacer(modifier = Modifier.height(HomeScreenDimens.emptyIconToTitleSpacer))
        Text(
            text = stringResource(R.string.empty_tasks_title),
            style = if (isExpanded) AnclaTextStyles.emptyStateTitleExpanded else AnclaTextStyles.emptyStateTitle,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(HomeScreenDimens.emptyTitleToSubtitleSpacer))
        Text(
            text = stringResource(R.string.empty_tasks_subtitle),
            style = AnclaTextStyles.emptyStateSubtitle,
            color = TextSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun HomeScreenCompactPreview() {
    AnclaTheme {
        HomeScreen(
            userName = "David",
            currentTasks = listOf(
                Task(
                    title = "Meditar 10 minutos",
                    description = "Busca un lugar tranquilo y concéntrate en tu respiración.",
                    time = "08:00 AM"
                )
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 900, heightDp = 600)
@Composable
fun HomeScreenExpandedPreview() {
    AnclaTheme {
        HomeScreen(
            userName = "David",
            currentTasks = listOf(
                Task(
                    title = "Meditar 10 minutos",
                    description = "Busca un lugar tranquilo y concéntrate en tu respiración.",
                    time = "08:00 AM"
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenEmptyPreview() {
    AnclaTheme {
        HomeScreen(
            userName = "David",
            currentTasks = emptyList()
        )
    }
}
