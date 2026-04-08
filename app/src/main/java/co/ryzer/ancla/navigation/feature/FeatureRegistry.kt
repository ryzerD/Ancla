package co.ryzer.ancla.navigation.feature

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import co.ryzer.ancla.core.navigation.FeatureEntry

class FeatureRegistry(
    private val entries: List<FeatureEntry>
) {
    fun registerAll(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        entries.forEach { entry ->
            entry.register(navGraphBuilder = navGraphBuilder, navController = navController)
        }
    }
}

