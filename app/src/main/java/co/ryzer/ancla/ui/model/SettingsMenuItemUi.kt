package co.ryzer.ancla.ui.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SettingsMenuItemUi(
    val number: Int,
    @param:StringRes val titleResId: Int,
    val icon: ImageVector,
    val badgeColor: Color,
    val onClick: () -> Unit
)


