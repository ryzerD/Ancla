package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import co.ryzer.ancla.data.DefaultToolOrder
import co.ryzer.ancla.navigation.feature.FeatureRegistry
import co.ryzer.ancla.navigation.EMERGENCY_CONTACT_DEFAULT
import co.ryzer.ancla.navigation.NavigationRoutes
import co.ryzer.ancla.ui.components.AnclaNavigationBar
import co.ryzer.ancla.ui.components.NavigationItem
import co.ryzer.ancla.ui.home.HomeViewModel
import co.ryzer.ancla.ui.home.navigation.HomeFeatureEntry
import co.ryzer.ancla.ui.profile.ProfileUiState
import co.ryzer.ancla.ui.profile.ProfileViewModel
import co.ryzer.ancla.ui.screening.ScreeningPagerScreen
import co.ryzer.ancla.ui.scripts.ScriptsViewModel
import co.ryzer.ancla.ui.scripts.navigation.ScriptsFeatureEntry
import co.ryzer.ancla.ui.settings.navigation.SettingsFeatureEntry
import co.ryzer.ancla.ui.tasks.TasksViewModel
import co.ryzer.ancla.ui.tasks.navigation.TasksFeatureEntry
import co.ryzer.ancla.ui.tools.navigation.ToolsFeatureEntry

private fun shouldHideNavigationChrome(currentRoute: String?): Boolean {
    return currentRoute == NavigationRoutes.SCRIPT_READER ||
            currentRoute == NavigationRoutes.NEW_SCRIPT ||
            currentRoute == NavigationRoutes.BREATHING ||
            currentRoute == NavigationRoutes.CALMA_TOTAL ||
            currentRoute == NavigationRoutes.CALM_MAP ||
            currentRoute == NavigationRoutes.ONBOARDING
}

private fun mainNavigationItems(): List<NavigationItem> = listOf(
    NavigationItem(
        route = NavigationRoutes.HOME,
        label = "Inicio",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    ),
    NavigationItem(
        route = NavigationRoutes.TOOLS,
        label = "Herramientas",
        icon = Icons.Outlined.Build,
        selectedIcon = Icons.Filled.Build
    ),
    NavigationItem(
        route = NavigationRoutes.SETTINGS,
        label = "Ajustes",
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings
    )
)

internal fun resolveStartDestination(profileUiState: ProfileUiState): String? {
    if (!profileUiState.isLoaded) return null
    return if (profileUiState.requiresOnboarding) NavigationRoutes.ONBOARDING else NavigationRoutes.HOME
}

private fun navigateFromNavItem(navController: NavHostController, route: String) {
    if (route == NavigationRoutes.HOME) {
        // Keep this path simple so Home is always reachable with one tap.
        navController.navigate(NavigationRoutes.HOME) {
            launchSingleTop = true
            restoreState = false
        }
        return
    }

    if (route == NavigationRoutes.TOOLS) {
        // Always open toolbox root, never restore a previous tool sub-screen.
        navController.navigate(NavigationRoutes.TOOLS) {
            launchSingleTop = true
            restoreState = false
            popUpTo(NavigationRoutes.HOME) { saveState = false }
        }
        return
    }

    navController.navigate(route) {
        popUpTo(NavigationRoutes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass,
    tasksViewModel: TasksViewModel,
    homeViewModel: HomeViewModel,
    scriptsViewModel: ScriptsViewModel,
    profileViewModel: ProfileViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val hidesNavigationChrome = shouldHideNavigationChrome(currentRoute)
    var toolOrder by remember {
        mutableStateOf(DefaultToolOrder)
    }
    val scriptsUiState by scriptsViewModel.uiState.collectAsState()
    val profileUiState by profileViewModel.uiState.collectAsState()
    val homeUiState by homeViewModel.uiState.collectAsState()
    val homeDisplayState by homeViewModel.homeDisplayState.collectAsState()

    val startDestination = resolveStartDestination(profileUiState) ?: return
    val featureRegistry = FeatureRegistry(
        entries = listOf(
            HomeFeatureEntry(
                windowSizeClass = windowSizeClass,
                userName = profileUiState.name,
                currentActivity = homeDisplayState.currentTask,
                activityState = homeUiState.activityState,
                hasOverlap = homeUiState.hasOverlap,
                isRecoveryMode = homeDisplayState.isRecoveryMode,
                currentPostponementMinutes = homeDisplayState.currentPostponementMinutes,
                onTaskComplete = { taskId -> tasksViewModel.onHomeTaskPrimaryAction(taskId) },
                onToggleRecoveryMode = { homeViewModel.toggleRecoveryMode() },
                onPostponeRemaining = { minutes -> homeViewModel.postponeAllRemaining(minutes) },
                onReducePostponement = { minutes -> homeViewModel.reducePostponement(minutes) },
                onClearPostponement = { homeViewModel.clearPostponement() },
                onStartMeditation = { navController.navigate(NavigationRoutes.BREATHING) }
            ),
            ToolsFeatureEntry(
                windowSizeClass = windowSizeClass,
                toolOrder = toolOrder
            ),
            SettingsFeatureEntry(
                windowSizeClass = windowSizeClass,
                toolOrder = toolOrder,
                scripts = scriptsUiState.scripts,
                selectedColorId = profileUiState.effectiveSelectedColorId,
                hasPendingPaletteChanges = profileUiState.hasPendingPaletteChanges,
                onToolsOrderChanged = { updatedOrder ->
                    toolOrder = updatedOrder
                },
                onScriptsOrderChanged = { orderedScriptIds ->
                    scriptsViewModel.reorderScripts(orderedScriptIds)
                },
                onPalettePreviewChanged = { colorId ->
                    profileViewModel.onPalettePreviewSelected(colorId)
                },
                onSavePalette = {
                    profileViewModel.savePaletteSelection()
                },
                onDiscardPalettePreview = {
                    profileViewModel.discardPalettePreview()
                }
            ),
            ScriptsFeatureEntry(
                windowSizeClass = windowSizeClass,
                scripts = scriptsUiState.scripts,
                emergencyContact = profileUiState.emergencyContact.ifBlank {
                    EMERGENCY_CONTACT_DEFAULT
                },
                onAddScript = { phrase, categoryId, styleId ->
                    scriptsViewModel.addScript(
                        phrase = phrase,
                        categoryId = categoryId,
                        styleId = styleId
                    )
                },
                getScriptById = { scriptId -> scriptsViewModel.getScriptById(scriptId) }
            ),
            TasksFeatureEntry(tasksViewModel = tasksViewModel)
        )
    )

    val navigationItems = remember { mainNavigationItems() }

    val showBottomBar = (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact ||
            windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) && !hidesNavigationChrome

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AnclaNavigationBar(
                    items = navigationItems,
                    currentRoute = currentRoute,
                    onItemClick = { item ->
                        navigateFromNavItem(navController = navController, route = item.route)
                    }
                )
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!showBottomBar) {
                if (!hidesNavigationChrome) {
                    NavigationRail {
                        navigationItems.forEach { item ->
                            NavigationRailItem(
                                selected = currentRoute == item.route,
                                onClick = {
                                    navigateFromNavItem(
                                        navController = navController,
                                        route = item.route
                                    )
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (currentRoute == item.route) item.selectedIcon else item.icon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }
            }

            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.weight(1f)
            ) {
                composable(NavigationRoutes.ONBOARDING) {
                    OnboardingSensorialScreen(
                        onContinue = {
                            navController.navigate(NavigationRoutes.HOME) {
                                popUpTo(NavigationRoutes.ONBOARDING) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                featureRegistry.registerAll(
                    navGraphBuilder = this,
                    navController = navController
                )
                composable(NavigationRoutes.BREATHING) {
                    BreathingScreen(onExit = { navController.popBackStack() })
                }
                composable(NavigationRoutes.CALMA_TOTAL) {
                    CalmaTotalScreen(onExit = { navController.popBackStack() })
                }
                composable(NavigationRoutes.CALM_MAP) {
                    ScreeningPagerScreen(
                        onClose = { navController.popBackStack() },
                        onComplete = {}
                    )
                }
            }
        }
    }
}
