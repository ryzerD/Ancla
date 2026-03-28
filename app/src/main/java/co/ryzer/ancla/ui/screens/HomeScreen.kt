package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.ui.unit.dp
import co.ryzer.ancla.R
import co.ryzer.ancla.data.Task
import co.ryzer.ancla.ui.theme.*

@Composable
fun HomeScreen(
    userName: String = "",
    currentActivity: Task? = null,
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
            if (currentActivity != null) {
                ActivityCard(currentActivity, onTaskComplete, isExpanded = isExpanded)
            } else {
                RestCard(isExpanded = isExpanded)
            }
        }

        Spacer(modifier = Modifier.height(HomeScreenDimens.bottomSpacer))
    }
}

@Composable
fun ActivityCard(
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
                text = task.category.uppercase(),
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
                text = "${task.startTime} - ${task.endTime}",
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
fun RestCard(isExpanded: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = if (isExpanded) HomeScreenDimens.cardMaxWidthExpanded else HomeScreenDimens.cardMaxWidthCompact),
        shape = RoundedCornerShape(HomeScreenDimens.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = CardLavender),
        elevation = CardDefaults.cardElevation(defaultElevation = HomeScreenDimens.cardElevation)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(
                    if (isExpanded) HomeScreenDimens.taskIconSizeExpanded else HomeScreenDimens.taskIconSizeCompact
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tiempo de Calma",
                style = if (isExpanded) AnclaTextStyles.taskTitleExpanded else AnclaTextStyles.taskTitle,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No tienes actividades programadas. Aprovecha para descansar y respirar profundo.",
                style = AnclaTextStyles.taskDescription,
                color = TextPrimary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun HomeScreenCompactPreview() {
    AnclaTheme {
        HomeScreen(
            userName = "David",
            currentActivity = Task(
                title = "Meditar 10 minutos",
                description = "Busca un lugar tranquilo y concéntrate en tu respiración.",
                startTime = "08:00",
                endTime = "08:10",
                category = "Rutina"
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun HomeScreenRestPreview() {
    AnclaTheme {
        HomeScreen(
            userName = "David",
            currentActivity = null
        )
    }
}
