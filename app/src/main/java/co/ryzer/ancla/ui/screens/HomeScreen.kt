package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.data.Task
import co.ryzer.ancla.ui.theme.*
import java.time.LocalTime

@Composable
fun HomeScreen(
    userName: String = "",
    currentActivity: Task? = null,
    onTaskComplete: (String) -> Unit = {},
    onStartMeditation: () -> Unit = {},
    windowSizeClass: WindowSizeClass? = null
) {
    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded
    val horizontalPadding =
        if (isExpanded) HomeScreenDimens.horizontalPaddingExpanded
        else HomeScreenDimens.horizontalPaddingCompact

    val greetingPeriodRes = when (LocalTime.now().hour) {
        in 5..11 -> R.string.greeting_morning
        in 12..18 -> R.string.greeting_afternoon
        else -> R.string.greeting_evening
    }

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
            text = stringResource(greetingPeriodRes),
            style = AnclaTextStyles.sectionLabel,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = HomeScreenDimens.periodGreetingBottomPadding)
        )

        Text(
            text = if (userName.isBlank()) stringResource(R.string.greeting) else stringResource(R.string.greeting_user, userName),
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
                RestCard(
                    isExpanded = isExpanded,
                    onStartMeditation = onStartMeditation
                )
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
                tint = ScriptReaderButton,
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
fun RestCard(
    isExpanded: Boolean = false,
    onStartMeditation: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = if (isExpanded) HomeScreenDimens.cardMaxWidthExpanded else HomeScreenDimens.cardMaxWidthCompact)
            .aspectRatio(
                if (isExpanded) {
                    HomeScreenDimens.restCardAspectRatioExpanded
                } else {
                    HomeScreenDimens.restCardAspectRatioCompact
                }
            )
            .clip(RoundedCornerShape(HomeScreenDimens.restCardCornerRadius)),
        shape = RoundedCornerShape(HomeScreenDimens.restCardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = CardPeach.copy(alpha = 0.22f)),
        elevation = CardDefaults.cardElevation(defaultElevation = HomeScreenDimens.restCardElevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(HomeScreenDimens.restCardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(HomeScreenDimens.restIconContainerSize)
                    .background(CardGreen.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = ScriptReaderButton,
                    modifier = Modifier.size(HomeScreenDimens.restIconSize)
                )
            }
            Spacer(modifier = Modifier.height(HomeScreenDimens.restIconBottomSpacing))
            Text(
                text = stringResource(R.string.home_rest_title),
                style = if (isExpanded) AnclaTextStyles.taskTitleExpanded else AnclaTextStyles.taskTitle,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(HomeScreenDimens.restTitleBottomSpacing))
            Text(
                text = stringResource(R.string.home_rest_subtitle),
                style = AnclaTextStyles.taskDescription,
                color = TextPrimary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = HomeScreenDimens.restSubtitleHorizontalPadding)
            )

            Spacer(modifier = Modifier.height(HomeScreenDimens.restContentToButtonSpacing))

            Button(
                onClick = onStartMeditation,
                modifier = Modifier
                    .fillMaxWidth(HomeScreenDimens.restButtonWidthFraction)
                    .heightIn(min = HomeScreenDimens.buttonMinHeight),
                colors = ButtonDefaults.buttonColors(containerColor = ScriptReaderButton),
                shape = RoundedCornerShape(HomeScreenDimens.buttonCornerRadius)
            ) {
                Text(
                    text = stringResource(R.string.home_rest_cta),
                    color = SurfaceWhite,
                    style = AnclaTextStyles.primaryButton
                )
            }
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
