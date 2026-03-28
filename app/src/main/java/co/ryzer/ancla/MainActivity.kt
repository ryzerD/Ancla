package co.ryzer.ancla

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import co.ryzer.ancla.ui.profile.ProfileViewModel
import co.ryzer.ancla.ui.screens.MainScreen
import co.ryzer.ancla.ui.scripts.ScriptsViewModel
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.applySensoryPalette
import co.ryzer.ancla.ui.tasks.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val tasksViewModel: TasksViewModel = viewModel()
            val scriptsViewModel: ScriptsViewModel = viewModel()
            val profileViewModel: ProfileViewModel = viewModel()
            val profileUiState by profileViewModel.uiState.collectAsState()

            // Solicitar permiso de notificaciones en Android 13+
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
                // Permiso otorgado o denegado
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            LaunchedEffect(profileUiState.effectiveSelectedColorId) {
                applySensoryPalette(profileUiState.effectiveSelectedColorId)
            }

            AnclaTheme {
                val navController = rememberNavController()
                MainScreen(
                    navController = navController,
                    windowSizeClass = windowSizeClass,
                    tasksViewModel = tasksViewModel,
                    scriptsViewModel = scriptsViewModel,
                    profileViewModel = profileViewModel
                )
            }
        }
    }
}
