package co.ryzer.ancla.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

/**
 * Contrato base para registrar destinos de una feature sin acoplar el NavHost principal.
 */
interface FeatureEntry {
    val route: String

    fun register(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    )
}

