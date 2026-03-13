package co.ryzer.ancla.ui.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ScriptCardUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val showsAddIndicator: Boolean = false
)
