package co.ryzer.ancla.ui.home.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import co.ryzer.ancla.core.navigation.FeatureEntry
import co.ryzer.ancla.data.Task
import co.ryzer.ancla.navigation.NavigationRoutes
import co.ryzer.ancla.ui.home.ActivityState
import co.ryzer.ancla.ui.screens.HomeScreen

class HomeFeatureEntry(
    private val windowSizeClass: WindowSizeClass,
    private val userName: String,
    private val currentActivity: Task?,
    private val activityState: ActivityState,
    private val hasOverlap: Boolean,
    private val isRecoveryMode: Boolean,
    private val currentPostponementMinutes: Long,
    private val onTaskComplete: (String) -> Unit,
    private val onToggleRecoveryMode: () -> Unit,
    private val onPostponeRemaining: (Long) -> Unit,
    private val onReducePostponement: (Long) -> Unit,
    private val onClearPostponement: () -> Unit,
    private val onStartMeditation: () -> Unit
) : FeatureEntry {
    override val route: String = NavigationRoutes.HOME

    override fun register(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.composable(route = route) {
            HomeScreen(
                userName = userName,
                currentActivity = currentActivity,
                activityState = activityState,
                hasOverlap = hasOverlap,
                isRecoveryMode = isRecoveryMode,
                currentPostponementMinutes = currentPostponementMinutes,
                onTaskComplete = onTaskComplete,
                onToggleRecoveryMode = onToggleRecoveryMode,
                onPostponeRemaining = onPostponeRemaining,
                onReducePostponement = onReducePostponement,
                onClearPostponement = onClearPostponement,
                onStartMeditation = onStartMeditation,
                windowSizeClass = windowSizeClass
            )
        }
    }
}

