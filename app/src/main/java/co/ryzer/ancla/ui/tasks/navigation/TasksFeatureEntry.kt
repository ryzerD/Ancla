package co.ryzer.ancla.ui.tasks.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import co.ryzer.ancla.core.navigation.FeatureEntry
import co.ryzer.ancla.navigation.NavigationRoutes
import co.ryzer.ancla.ui.screens.TaskManagementScreen
import co.ryzer.ancla.ui.tasks.TasksViewModel

class TasksFeatureEntry(
    private val tasksViewModel: TasksViewModel
) : FeatureEntry {
    override val route: String = NavigationRoutes.TASKS

    override fun register(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.composable(route = route) {
            TaskManagementScreen(
                onBack = { navController.popBackStack() },
                viewModel = tasksViewModel
            )
        }
    }
}

