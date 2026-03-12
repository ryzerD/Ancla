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
import co.ryzer.ancla.data.ToolOrderEntry
import co.ryzer.ancla.ui.components.AnclaNavigationBar
import co.ryzer.ancla.ui.components.NavigationItem
import co.ryzer.ancla.ui.tasks.TasksViewModel

@Composable
fun MainScreen(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass,
    tasksViewModel: TasksViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var toolOrder by remember {
        mutableStateOf(DefaultToolOrder)
    }
    val tasksUiState by tasksViewModel.uiState.collectAsState()

    val navigationItems = listOf(
        NavigationItem(
            route = "home",
            label = "Inicio",
            icon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home
        ),
        NavigationItem(
            route = "tools",
            label = "Herramientas",
            icon = Icons.Outlined.Build,
            selectedIcon = Icons.Filled.Build
        ),
        NavigationItem(
            route = "settings",
            label = "Ajustes",
            icon = Icons.Outlined.Settings,
            selectedIcon = Icons.Filled.Settings
        )
    )

    val showBottomBar = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact ||
            windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AnclaNavigationBar(
                    items = navigationItems,
                    currentRoute = currentRoute,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                NavigationRail {
                    navigationItems.forEach { item ->
                        NavigationRailItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
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

            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.weight(1f)
            ) {
                composable("home") {
                    HomeScreen(
                        currentTasks = tasksUiState.pendingTasks,
                        onTaskComplete = { taskId ->
                            tasksViewModel.setTaskCompleted(taskId = taskId, isCompleted = true)
                        },
                        windowSizeClass = windowSizeClass
                    )
                }
                composable("tools") {
                    ToolsScreen(
                        onNavigateToDecoder = { navController.navigate("decoder") },
                        onNavigateToTasks = { navController.navigate("tasks") },
                        windowSizeClass = windowSizeClass,
                        toolOrder = toolOrder
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        windowSizeClass = windowSizeClass,
                        toolOrder = toolOrder,
                        onToolsOrderChanged = { updatedOrder: List<ToolOrderEntry> ->
                            toolOrder = updatedOrder
                        }
                    )
                }
                composable("decoder") { DecoderScreen() }
                composable("tasks") {
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
            }
        }
    }
}
