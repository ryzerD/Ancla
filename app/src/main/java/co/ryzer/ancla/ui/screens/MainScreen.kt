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
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import co.ryzer.ancla.data.DefaultToolOrder
import co.ryzer.ancla.data.ToolOrderEntry
import co.ryzer.ancla.ui.profile.ProfileUiState
import co.ryzer.ancla.ui.components.AnclaNavigationBar
import co.ryzer.ancla.ui.components.NavigationItem
import co.ryzer.ancla.ui.profile.ProfileViewModel
import co.ryzer.ancla.ui.scripts.ScriptsViewModel
import co.ryzer.ancla.ui.tasks.TasksViewModel

private const val ROUTE_HOME = "home"
private const val ROUTE_ONBOARDING = "onboarding"
private const val ROUTE_TOOLS = "tools"
private const val ROUTE_SETTINGS = "settings"
private const val ROUTE_DECODER = "decoder"
private const val ROUTE_TASKS = "tasks"
private const val ROUTE_SCRIPTS = "scripts"
private const val ROUTE_NEW_SCRIPT = "new_script"
private const val ROUTE_SCRIPT_READER = "script_reader/{scriptId}"
private const val ARG_SCRIPT_ID = "scriptId"
private const val EMERGENCY_CONTACT_DEFAULT = "123-456-789"

internal fun resolveStartDestination(profileUiState: ProfileUiState): String? {
    if (!profileUiState.isLoaded) return null
    return if (profileUiState.requiresOnboarding) ROUTE_ONBOARDING else ROUTE_HOME
}

private fun navigateFromNavItem(navController: NavHostController, route: String) {
    if (route == ROUTE_HOME) {
        // Always go straight to Home root without restoring previous tool stack.
        navController.navigate(ROUTE_HOME) {
            launchSingleTop = true
            restoreState = false
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
                saveState = false
            }
        }
        return
    }

    if (route == ROUTE_TOOLS) {
        // Always open toolbox root, never restore a previous tool sub-screen.
        navController.navigate(ROUTE_TOOLS) {
            launchSingleTop = true
            restoreState = false
            popUpTo(ROUTE_HOME) { saveState = false }
        }
        return
    }

    navController.navigate(route) {
        popUpTo(ROUTE_HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass,
    tasksViewModel: TasksViewModel,
    scriptsViewModel: ScriptsViewModel,
    profileViewModel: ProfileViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val hidesNavigationChrome = currentRoute == ROUTE_SCRIPT_READER ||
            currentRoute == ROUTE_NEW_SCRIPT ||
            currentRoute == ROUTE_ONBOARDING
    var toolOrder by remember {
        mutableStateOf(DefaultToolOrder)
    }
    val tasksUiState by tasksViewModel.uiState.collectAsState()
    val scriptsUiState by scriptsViewModel.uiState.collectAsState()
    val profileUiState by profileViewModel.uiState.collectAsState()

    val startDestination = resolveStartDestination(profileUiState) ?: return

    val navigationItems = listOf(
        NavigationItem(
            route = ROUTE_HOME,
            label = "Inicio",
            icon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home
        ),
        NavigationItem(
            route = ROUTE_TOOLS,
            label = "Herramientas",
            icon = Icons.Outlined.Build,
            selectedIcon = Icons.Filled.Build
        ),
        NavigationItem(
            route = ROUTE_SETTINGS,
            label = "Ajustes",
            icon = Icons.Outlined.Settings,
            selectedIcon = Icons.Filled.Settings
        )
    )

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
                                navigateFromNavItem(navController = navController, route = item.route)
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
                composable(ROUTE_ONBOARDING) {
                    OnboardingSensorialScreen(
                        onContinue = {
                            navController.navigate(ROUTE_HOME) {
                                popUpTo(ROUTE_ONBOARDING) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(ROUTE_HOME) {
                    HomeScreen(
                        userName = profileUiState.name,
                        currentTasks = tasksUiState.pendingTasks,
                        onTaskComplete = { taskId ->
                            tasksViewModel.setTaskCompleted(taskId = taskId, isCompleted = true)
                        },
                        windowSizeClass = windowSizeClass
                    )
                }
                composable(ROUTE_TOOLS) {
                    ToolsScreen(
                        onNavigateToDecoder = { navController.navigate(ROUTE_DECODER) },
                        onNavigateToTasks = { navController.navigate(ROUTE_TASKS) },
                        onNavigateToScripts = { navController.navigate(ROUTE_SCRIPTS) },
                        windowSizeClass = windowSizeClass,
                        toolOrder = toolOrder
                    )
                }
                composable(ROUTE_SETTINGS) {
                    SettingsScreen(
                        windowSizeClass = windowSizeClass,
                        toolOrder = toolOrder,
                        scripts = scriptsUiState.scripts,
                        selectedColorId = profileUiState.effectiveSelectedColorId,
                        hasPendingPaletteChanges = profileUiState.hasPendingPaletteChanges,
                        onToolsOrderChanged = { updatedOrder: List<ToolOrderEntry> ->
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
                    )
                }
                composable(ROUTE_DECODER) { DecoderScreen() }
                composable(ROUTE_TASKS) {
                    TaskManagementScreen(
                        title = tasksUiState.newTitle,
                        description = tasksUiState.newDescription,
                        time = tasksUiState.newTime,
                        tasks = tasksUiState.tasks,
                        onTitleChange = tasksViewModel::onTitleChange,
                        onDescriptionChange = tasksViewModel::onDescriptionChange,
                        onTimeChange = tasksViewModel::onTimeChange,
                        onAddTask = tasksViewModel::addTask,
                        isEditing = tasksUiState.isEditing,
                        onToggleCompleted = tasksViewModel::setTaskCompleted,
                        onDeleteTask = tasksViewModel::deleteTask,
                        onStartEditTask = tasksViewModel::startEditing,
                        onCancelEditing = tasksViewModel::cancelEditing,
                    )
                }
                composable(ROUTE_SCRIPTS) {
                    ScriptsScreen(
                        windowSizeClass = windowSizeClass,
                        scripts = scriptsUiState.scripts,
                        onScriptClick = { scriptId ->
                            navController.navigate("script_reader/$scriptId")
                        },
                        onNewScriptClick = { navController.navigate(ROUTE_NEW_SCRIPT) }
                    )
                }
                composable(ROUTE_NEW_SCRIPT) {
                    NewScriptScreen(
                        windowSizeClass = windowSizeClass,
                        onSaveScript = { phrase, categoryId, styleId ->
                            scriptsViewModel.addScript(
                                phrase = phrase,
                                categoryId = categoryId,
                                styleId = styleId
                            )
                            navController.popBackStack()
                        },
                        onCloseWithoutSaving = { navController.popBackStack() }
                    )
                }
                composable(
                    route = ROUTE_SCRIPT_READER,
                    arguments = listOf(navArgument(ARG_SCRIPT_ID) { type = NavType.StringType })
                ) { backStackEntry ->
                    val scriptId = backStackEntry.arguments?.getString(ARG_SCRIPT_ID).orEmpty()
                    val selectedScript = scriptsViewModel.getScriptById(scriptId)
                    ScriptReaderScreen(
                        mainText = selectedScript?.message ?: "NECESITO APOYO",
                        showEmergencyInfo = selectedScript?.showEmergencyContact ?: false,
                        emergencyContact = profileUiState.emergencyContact.ifBlank {
                            EMERGENCY_CONTACT_DEFAULT
                        },
                        onClose = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
