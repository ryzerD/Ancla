package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.data.Task
import co.ryzer.ancla.ui.home.ActivityState
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.HomeScreenDimens

@Composable
fun HomeScreen(
    userName: String = "",
    currentActivity: Task? = null,
    activityState: ActivityState = ActivityState.SCHEDULED,
    hasOverlap: Boolean = false,
    isRecoveryMode: Boolean = false,
    onTaskComplete: (String) -> Unit = {},
    onToggleRecoveryMode: () -> Unit = {},
    onPostponeRemaining: (Long) -> Unit = {},
    onStartMeditation: () -> Unit = {},
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

        HomeGreetingSection(userName = userName, isExpanded = isExpanded)

        Spacer(modifier = Modifier.height(HomeScreenDimens.periodGreetingBottomPadding))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            HomeMainContentSection(
                currentActivity = currentActivity,
                activityState = activityState,
                hasOverlap = hasOverlap,
                isRecoveryMode = isRecoveryMode,
                isExpanded = isExpanded,
                onTaskComplete = onTaskComplete,
                onStartMeditation = onStartMeditation
            )
        }

        Spacer(modifier = Modifier.height(HomeScreenDimens.bottomSpacer))

        HomeQuickControlsSection(
            isRecoveryMode = isRecoveryMode,
            onToggleRecoveryMode = onToggleRecoveryMode,
            onPostponeRemaining = onPostponeRemaining
        )
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
