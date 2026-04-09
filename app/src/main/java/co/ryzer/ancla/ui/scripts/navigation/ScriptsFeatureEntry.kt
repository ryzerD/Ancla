package co.ryzer.ancla.ui.scripts.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import co.ryzer.ancla.core.navigation.FeatureEntry
import co.ryzer.ancla.data.Script
import co.ryzer.ancla.navigation.NavigationRoutes
import co.ryzer.ancla.ui.screens.NewScriptScreen
import co.ryzer.ancla.ui.screens.ScriptReaderScreen
import co.ryzer.ancla.ui.screens.ScriptsScreen

class ScriptsFeatureEntry(
    private val windowSizeClass: WindowSizeClass,
    private val scripts: List<Script>,
    private val emergencyContact: String,
    private val onAddScript: (phrase: String, categoryId: String, styleId: String) -> Unit,
    private val getScriptById: (String) -> Script?
) : FeatureEntry {
    override val route: String = NavigationRoutes.SCRIPTS

    override fun register(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.composable(route = route) {
            ScriptsScreen(
                windowSizeClass = windowSizeClass,
                scripts = scripts,
                onScriptClick = { scriptId ->
                    navController.navigate(NavigationRoutes.scriptReaderRoute(scriptId))
                },
                onNewScriptClick = {
                    navController.navigate(NavigationRoutes.NEW_SCRIPT)
                },
                onBack = { navController.popBackStack() }
            )
        }

        navGraphBuilder.composable(route = NavigationRoutes.NEW_SCRIPT) {
            NewScriptScreen(
                windowSizeClass = windowSizeClass,
                onSaveScript = { phrase, categoryId, styleId ->
                    onAddScript(phrase, categoryId, styleId)
                    navController.popBackStack()
                },
                onCloseWithoutSaving = { navController.popBackStack() }
            )
        }

        navGraphBuilder.composable(
            route = NavigationRoutes.SCRIPT_READER,
            arguments = listOf(navArgument(NavigationRoutes.ARG_SCRIPT_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val scriptId = backStackEntry.arguments?.getString(NavigationRoutes.ARG_SCRIPT_ID).orEmpty()
            val selectedScript = getScriptById(scriptId)
            ScriptReaderScreen(
                mainText = selectedScript?.message ?: "NECESITO APOYO",
                showEmergencyInfo = selectedScript?.showEmergencyContact ?: false,
                emergencyContact = emergencyContact,
                onClose = { navController.popBackStack() }
            )
        }
    }
}

