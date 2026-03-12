package co.ryzer.ancla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import co.ryzer.ancla.data.local.AnclaDatabase
import co.ryzer.ancla.data.repository.RoomTaskRepository
import co.ryzer.ancla.ui.screens.MainScreen
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.tasks.TasksViewModel
import co.ryzer.ancla.ui.tasks.TasksViewModelFactory

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val taskRepository = RoomTaskRepository(
            taskDao = AnclaDatabase.getInstance(this).taskDao()
        )
        val tasksViewModelFactory = TasksViewModelFactory(taskRepository)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val tasksViewModel: TasksViewModel = viewModel(factory = tasksViewModelFactory)
            AnclaTheme {
                val navController = rememberNavController()
                MainScreen(
                    navController = navController,
                    windowSizeClass = windowSizeClass,
                    tasksViewModel = tasksViewModel
                )
            }
        }
    }
}
