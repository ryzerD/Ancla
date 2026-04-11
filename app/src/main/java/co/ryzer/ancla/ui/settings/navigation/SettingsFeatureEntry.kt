package co.ryzer.ancla.ui.settings.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import co.ryzer.ancla.core.navigation.FeatureEntry
import co.ryzer.ancla.data.Script
import co.ryzer.ancla.data.ToolOrderEntry
import co.ryzer.ancla.navigation.NavigationRoutes
import co.ryzer.ancla.ui.screens.SettingsScreen
import co.ryzer.ancla.ui.screens.SettingsToolsOrderScreen
import co.ryzer.ancla.ui.screens.SettingsVisualPreferencesScreen

class SettingsFeatureEntry(
    private val windowSizeClass: WindowSizeClass,
    private val toolOrder: List<ToolOrderEntry>,
    private val scripts: List<Script>,
    private val selectedColorId: String,
    private val hasPendingPaletteChanges: Boolean,
    private val onToolsOrderChanged: (List<ToolOrderEntry>) -> Unit,
    private val onScriptsOrderChanged: (List<String>) -> Unit,
    private val onPalettePreviewChanged: (String) -> Unit,
    private val onSavePalette: () -> Unit,
    private val onDiscardPalettePreview: () -> Unit
) : FeatureEntry {
    override val route: String = NavigationRoutes.SETTINGS

    override fun register(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.composable(route = route) {
            SettingsScreen(
                windowSizeClass = windowSizeClass,
                toolOrder = toolOrder,
                scripts = scripts,
                selectedColorId = selectedColorId,
                hasPendingPaletteChanges = hasPendingPaletteChanges,
                onToolsOrderChanged = onToolsOrderChanged,
                onScriptsOrderChanged = onScriptsOrderChanged,
                onPalettePreviewChanged = onPalettePreviewChanged,
                onSavePalette = onSavePalette,
                onDiscardPalettePreview = onDiscardPalettePreview,
                onVisualPreferencesClick = {
                    navController.navigate(NavigationRoutes.SETTINGS_VISUAL)
                },
                onToolsOrganizationClick = {
                    navController.navigate(NavigationRoutes.SETTINGS_ORDER)
                }
            )
        }

        navGraphBuilder.composable(route = NavigationRoutes.SETTINGS_ORDER) {
            SettingsToolsOrderScreen(
                windowSizeClass = windowSizeClass,
                toolOrder = toolOrder,
                scripts = scripts,
                onToolsOrderChanged = onToolsOrderChanged,
                onScriptsOrderChanged = onScriptsOrderChanged,
                onNavigationClick = {
                    navController.navigate(NavigationRoutes.SETTINGS)
                }
            )
        }

        navGraphBuilder.composable(route = NavigationRoutes.SETTINGS_VISUAL) {
            SettingsVisualPreferencesScreen(
                windowSizeClass = windowSizeClass,
                selectedColorId = selectedColorId,
                hasPendingPaletteChanges = hasPendingPaletteChanges,
                onPalettePreviewChanged = onPalettePreviewChanged,
                onSavePalette = onSavePalette,
                onDiscardPalettePreview = onDiscardPalettePreview,
                onNavigationClick = {
                    navController.navigate(NavigationRoutes.SETTINGS)
                }
            )
        }
    }
}

