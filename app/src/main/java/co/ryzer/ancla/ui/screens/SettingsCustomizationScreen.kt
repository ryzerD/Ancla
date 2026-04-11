package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FrontHand
import androidx.compose.material.icons.outlined.PanToolAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.data.DefaultToolOrder
import co.ryzer.ancla.data.Script
import co.ryzer.ancla.data.ToolIds
import co.ryzer.ancla.data.ToolOrderEntry
import co.ryzer.ancla.ui.components.AnclaTopBar
import co.ryzer.ancla.ui.components.SensoryPalettePicker
import co.ryzer.ancla.ui.components.SensoryPalettePickerStyle
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.CardPeach
import co.ryzer.ancla.ui.theme.CardRose
import co.ryzer.ancla.ui.theme.CommonDimens
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens

private data class SettingsToolOrderItem(
    val toolId: String,
    val titleResId: Int,
    val icon: ImageVector,
    val accentColor: Color
)

private data class SettingsScriptOrderItem(
    val scriptId: String,
    val title: String
)

private fun moveItem(order: List<String>, fromIndex: Int, toIndex: Int): List<String> {
    if (fromIndex !in order.indices || toIndex !in order.indices || fromIndex == toIndex) {
        return order
    }
    val mutable = order.toMutableList()
    val moved = mutable.removeAt(fromIndex)
    mutable.add(toIndex, moved)
    return mutable
}

@Composable
private fun SettingsOrderHeader(
    isExpanded: Boolean,
    titleResId: Int,
    descriptionResId: Int
) {
    Column {
        Text(
            text = stringResource(titleResId),
            style = if (isExpanded) {
                AnclaTextStyles.toolsTitleExpanded.copy(fontWeight = FontWeight.Bold)
            } else {
                AnclaTextStyles.toolsTitle.copy(fontWeight = FontWeight.Bold)
            },
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
        Text(
            text = stringResource(descriptionResId),
            style = AnclaTextStyles.toolCardSubtitle,
            color = TextSecondary
        )
    }
}

@Composable
private fun MoveCircleButton(
    enabled: Boolean,
    contentDescription: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(ToolsScreenDimens.iconPlaceholderSize)
            .background(
                color = if (enabled) {
                    CardGreen.copy(alpha = 0.25f)
                } else {
                    CardLavender.copy(alpha = 0.14f)
                },
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) TextPrimary else TextSecondary
        )
    }
}

@Composable
private fun OrderCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
        border = BorderStroke(width = CommonDimens.spacingSmall / 2, color = accentColor.copy(alpha = 0.65f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ToolsScreenDimens.cardContentPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ToolsScreenDimens.iconToTextSpacer)
            ) {
                Box(
                    modifier = Modifier
                        .size(ToolsScreenDimens.iconPlaceholderSize)
                        .background(
                            color = accentColor.copy(alpha = 0.25f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TextPrimary
                    )
                }
                Text(
                    text = title,
                    style = AnclaTextStyles.toolCardTitle,
                    color = TextPrimary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(ToolsScreenDimens.orderControlsSpacing)) {
                MoveCircleButton(
                    enabled = canMoveUp,
                    contentDescription = stringResource(R.string.tool_move_up),
                    icon = Icons.Default.KeyboardArrowUp,
                    onClick = onMoveUp
                )
                MoveCircleButton(
                    enabled = canMoveDown,
                    contentDescription = stringResource(R.string.tool_move_down),
                    icon = Icons.Default.KeyboardArrowDown,
                    onClick = onMoveDown
                )
            }
        }
    }
}

@Composable
fun SettingsToolsOrderScreen(
    windowSizeClass: WindowSizeClass? = null,
    toolOrder: List<ToolOrderEntry> = DefaultToolOrder,
    scripts: List<Script> = emptyList(),
    onToolsOrderChanged: (List<ToolOrderEntry>) -> Unit = {},
    onScriptsOrderChanged: (List<String>) -> Unit = {},
    onNavigationClick: () -> Unit = {}
) {
    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded
    val horizontalPadding = if (isExpanded) {
        ToolsScreenDimens.horizontalPaddingExpanded
    } else {
        ToolsScreenDimens.horizontalPaddingCompact
    }

    val toolCatalog = listOf(
        SettingsToolOrderItem(
            toolId = ToolIds.TASKS,
            titleResId = R.string.tool_tasks_title,
            icon = Icons.Outlined.PanToolAlt,
            accentColor = CardGreen
        ),
        SettingsToolOrderItem(
            toolId = ToolIds.SCRIPTS,
            titleResId = R.string.tool_scripts_title,
            icon = Icons.Outlined.FrontHand,
            accentColor = CardLavender
        ),
        SettingsToolOrderItem(
            toolId = ToolIds.BREATHING,
            titleResId = R.string.tool_breathing_title,
            icon = Icons.Outlined.Air,
            accentColor = CardPeach
        ),
        SettingsToolOrderItem(
            toolId = ToolIds.SOS,
            titleResId = R.string.tool_sos_title,
            icon = Icons.Outlined.Build,
            accentColor = CardRose
        ),
        SettingsToolOrderItem(
            toolId = ToolIds.CALM_MAP,
            titleResId = R.string.tool_calm_map_title,
            icon = Icons.Outlined.Favorite,
            accentColor = CardLavender
        )
    )
    val orderedToolIds = toolOrder.sortedBy { it.position }.map { it.toolId }
    val toolCatalogById = toolCatalog.associateBy { it.toolId }
    val orderedTools = orderedToolIds.mapNotNull { toolCatalogById[it] }

    val orderedScripts = scripts
        .sortedBy { it.position }
        .map { script -> SettingsScriptOrderItem(scriptId = script.id, title = script.title) }
    val orderedScriptIds = orderedScripts.map { it.scriptId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
    ) {
        AnclaTopBar(
            title = stringResource(R.string.settings_tools_org_title),
            onNavigationClick = onNavigationClick,
            navigationContentDescription = stringResource(R.string.tasks_back_content_description)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = ToolsScreenDimens.verticalPadding),
            verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.gridSpacing)
        ) {
        item {
            SettingsOrderHeader(
                isExpanded = isExpanded,
                titleResId = R.string.settings_tools_order_title,
                descriptionResId = R.string.settings_tools_order_description
            )
        }

        items(items = orderedTools, key = { it.toolId }) { tool ->
            val currentIndex = orderedToolIds.indexOf(tool.toolId)
            val canMoveUp = currentIndex > 0
            val canMoveDown = currentIndex < orderedToolIds.lastIndex

            OrderCard(
                title = stringResource(tool.titleResId),
                icon = tool.icon,
                accentColor = tool.accentColor,
                canMoveUp = canMoveUp,
                canMoveDown = canMoveDown,
                onMoveUp = {
                    val newOrder = moveItem(orderedToolIds, currentIndex, currentIndex - 1)
                    onToolsOrderChanged(
                        newOrder.mapIndexed { index, toolId ->
                            ToolOrderEntry(toolId = toolId, position = index)
                        }
                    )
                },
                onMoveDown = {
                    val newOrder = moveItem(orderedToolIds, currentIndex, currentIndex + 1)
                    onToolsOrderChanged(
                        newOrder.mapIndexed { index, toolId ->
                            ToolOrderEntry(toolId = toolId, position = index)
                        }
                    )
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))
            SettingsOrderHeader(
                isExpanded = isExpanded,
                titleResId = R.string.settings_scripts_order_title,
                descriptionResId = R.string.settings_scripts_order_description
            )
        }

        items(items = orderedScripts, key = { it.scriptId }) { script ->
            val currentIndex = orderedScriptIds.indexOf(script.scriptId)
            val canMoveUp = currentIndex > 0
            val canMoveDown = currentIndex < orderedScriptIds.lastIndex

            OrderCard(
                title = script.title,
                icon = Icons.Outlined.FrontHand,
                accentColor = CardLavender,
                canMoveUp = canMoveUp,
                canMoveDown = canMoveDown,
                onMoveUp = {
                    val newOrder = moveItem(orderedScriptIds, currentIndex, currentIndex - 1)
                    onScriptsOrderChanged(newOrder)
                },
                onMoveDown = {
                    val newOrder = moveItem(orderedScriptIds, currentIndex, currentIndex + 1)
                    onScriptsOrderChanged(newOrder)
                }
            )
        }
        }
    }
}

@Composable
fun SettingsVisualPreferencesScreen(
    windowSizeClass: WindowSizeClass? = null,
    selectedColorId: String = "lavender",
    hasPendingPaletteChanges: Boolean = false,
    onPalettePreviewChanged: (String) -> Unit = {},
    onSavePalette: () -> Unit = {},
    onDiscardPalettePreview: () -> Unit = {},
    onNavigationClick: () -> Unit = {}
) {
    DisposableEffect(Unit) {
        onDispose { onDiscardPalettePreview() }
    }

    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded
    val horizontalPadding = if (isExpanded) {
        ToolsScreenDimens.horizontalPaddingExpanded
    } else {
        ToolsScreenDimens.horizontalPaddingCompact
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
    ) {
        AnclaTopBar(
            title = stringResource(R.string.settings_palette_preview_title),
            onNavigationClick = onNavigationClick,
            navigationContentDescription = stringResource(R.string.tool_move_up)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = ToolsScreenDimens.verticalPadding),
            verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.gridSpacing)
        ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.settings_palette_preview_title),
                    style = if (isExpanded) AnclaTextStyles.toolsTitleExpanded else AnclaTextStyles.toolsTitle,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
                Text(
                    text = stringResource(R.string.settings_palette_preview_description),
                    style = AnclaTextStyles.toolCardSubtitle,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(ToolsScreenDimens.gridSpacing))

                SensoryPalettePicker(
                    selectedColorId = selectedColorId,
                    onColorSelected = onPalettePreviewChanged,
                    style = SensoryPalettePickerStyle.LargeCircle
                )

                Spacer(modifier = Modifier.height(ToolsScreenDimens.gridSpacing))
                Text(
                    text = if (hasPendingPaletteChanges) {
                        stringResource(R.string.settings_palette_unsaved_changes)
                    } else {
                        stringResource(R.string.settings_palette_saved_state)
                    },
                    style = AnclaTextStyles.toolCardSubtitle,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(ToolsScreenDimens.gridSpacing))
                Button(
                    onClick = onSavePalette,
                    enabled = hasPendingPaletteChanges,
                    shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CardGreen,
                        contentColor = TextPrimary
                    )
                ) {
                    Text(text = stringResource(R.string.settings_palette_save_button))
                }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
private fun SettingsToolsOrderScreenPreview() {
    AnclaTheme {
        SettingsToolsOrderScreen(
            scripts = listOf(
                Script(
                    id = "1",
                    title = "Pedir ayuda",
                    subtitle = "",
                    message = "",
                    categoryId = "1",
                    styleId = "1",
                    position = 0
                ),
                Script(
                    id = "2",
                    title = "No puedo hablar",
                    subtitle = "",
                    message = "",
                    categoryId = "1",
                    styleId = "1",
                    position = 1
                )
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
private fun SettingsVisualPreferencesScreenPreview() {
    AnclaTheme {
        SettingsVisualPreferencesScreen(hasPendingPaletteChanges = true)
    }
}
