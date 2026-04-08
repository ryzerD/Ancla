package co.ryzer.ancla.ui.tools.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import co.ryzer.ancla.core.navigation.FeatureEntry
import co.ryzer.ancla.data.ToolOrderEntry
import co.ryzer.ancla.navigation.NavigationRoutes
import co.ryzer.ancla.ui.screens.ToolsScreen

class ToolsFeatureEntry(
    private val windowSizeClass: WindowSizeClass,
    private val toolOrder: List<ToolOrderEntry>
) : FeatureEntry {
    override val route: String = NavigationRoutes.TOOLS

    override fun register(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.composable(route = route) {
            ToolsScreen(
                onNavigateToTasks = { navController.navigate(NavigationRoutes.TASKS) },
                onNavigateToScripts = { navController.navigate(NavigationRoutes.SCRIPTS) },
                onNavigateToBreathing = { navController.navigate(NavigationRoutes.BREATHING) },
                onNavigateToCalmaTotal = { navController.navigate(NavigationRoutes.CALMA_TOTAL) },
                onNavigateToCalmMap = { navController.navigate(NavigationRoutes.CALM_MAP) },
                windowSizeClass = windowSizeClass,
                toolOrder = toolOrder
            )
        }
    }
}

