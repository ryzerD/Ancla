package co.ryzer.ancla.ui.components

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data model for Bottom Navigation entries.
 * Using String resources for accessibility and Localization.
 */
data class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector, // You can use ImageVector or @DrawableRes
    val selectedIcon: ImageVector
)