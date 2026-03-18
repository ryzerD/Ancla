package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.data.DefaultToolOrder
import co.ryzer.ancla.data.Script
import co.ryzer.ancla.data.ToolIds
import co.ryzer.ancla.data.ToolOrderEntry
import co.ryzer.ancla.ui.components.SensoryPalettePicker
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens

private data class SettingsToolOrderItem(
    val toolId: String,
    val titleResId: Int
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
fun SettingsToolsOrderScreen(
    windowSizeClass: WindowSizeClass? = null,
    toolOrder: List<ToolOrderEntry> = DefaultToolOrder,
    scripts: List<Script> = emptyList(),
    onToolsOrderChanged: (List<ToolOrderEntry>) -> Unit = {},
    onScriptsOrderChanged: (List<String>) -> Unit = {}
) {
    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded
    val horizontalPadding = if (isExpanded) {
        ToolsScreenDimens.horizontalPaddingExpanded
    } else {
        ToolsScreenDimens.horizontalPaddingCompact
    }

    val toolCatalog = listOf(
        SettingsToolOrderItem(toolId = ToolIds.DECODER, titleResId = R.string.tool_decoder_title),
        SettingsToolOrderItem(toolId = ToolIds.TASKS, titleResId = R.string.tool_tasks_title),
        SettingsToolOrderItem(toolId = ToolIds.SCRIPTS, titleResId = R.string.tool_scripts_title),
        SettingsToolOrderItem(toolId = ToolIds.BREATHING, titleResId = R.string.tool_breathing_title),
        SettingsToolOrderItem(toolId = ToolIds.SOS, titleResId = R.string.tool_sos_title)
    )
    val orderedToolIds = toolOrder.sortedBy { it.position }.map { it.toolId }
    val toolCatalogById = toolCatalog.associateBy { it.toolId }
    val orderedTools = orderedToolIds.mapNotNull { toolCatalogById[it] }

    val orderedScripts = scripts
        .sortedBy { it.position }
        .map { script -> SettingsScriptOrderItem(scriptId = script.id, title = script.title) }
    val orderedScriptIds = orderedScripts.map { it.scriptId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(horizontal = horizontalPadding, vertical = ToolsScreenDimens.verticalPadding),
        verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.gridSpacing)
    ) {
        item {
            Text(
                text = stringResource(R.string.settings_tools_order_label),
                style = AnclaTextStyles.toolsSupportLabel,
                color = TextSecondary
            )
            Text(
                text = stringResource(R.string.settings_tools_order_title),
                style = if (isExpanded) AnclaTextStyles.toolsTitleExpanded else AnclaTextStyles.toolsTitle,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
            Text(
                text = stringResource(R.string.settings_tools_order_description),
                style = AnclaTextStyles.toolCardSubtitle,
                color = TextSecondary
            )
        }

        items(items = orderedTools, key = { it.toolId }) { tool ->
            val currentIndex = orderedToolIds.indexOf(tool.toolId)
            val canMoveUp = currentIndex > 0
            val canMoveDown = currentIndex < orderedToolIds.lastIndex

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(ToolsScreenDimens.cardContentPadding),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(tool.titleResId),
                        style = AnclaTextStyles.toolCardTitle,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(ToolsScreenDimens.orderControlsSpacing)) {
                        TextButton(
                            onClick = {
                                val newOrder = moveItem(orderedToolIds, currentIndex, currentIndex - 1)
                                onToolsOrderChanged(
                                    newOrder.mapIndexed { index, toolId ->
                                        ToolOrderEntry(toolId = toolId, position = index)
                                    }
                                )
                            },
                            enabled = canMoveUp
                        ) {
                            Text(text = stringResource(R.string.tool_move_up))
                        }
                        TextButton(
                            onClick = {
                                val newOrder = moveItem(orderedToolIds, currentIndex, currentIndex + 1)
                                onToolsOrderChanged(
                                    newOrder.mapIndexed { index, toolId ->
                                        ToolOrderEntry(toolId = toolId, position = index)
                                    }
                                )
                            },
                            enabled = canMoveDown
                        ) {
                            Text(text = stringResource(R.string.tool_move_down))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))
            Text(
                text = stringResource(R.string.settings_scripts_order_title),
                style = AnclaTextStyles.toolCardTitle,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
            Text(
                text = stringResource(R.string.settings_scripts_order_description),
                style = AnclaTextStyles.toolCardSubtitle,
                color = TextSecondary
            )
        }

        items(items = orderedScripts, key = { it.scriptId }) { script ->
            val currentIndex = orderedScriptIds.indexOf(script.scriptId)
            val canMoveUp = currentIndex > 0
            val canMoveDown = currentIndex < orderedScriptIds.lastIndex

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(ToolsScreenDimens.cardContentPadding),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = script.title,
                        style = AnclaTextStyles.toolCardTitle,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(ToolsScreenDimens.orderControlsSpacing)) {
                        TextButton(
                            onClick = {
                                val newOrder = moveItem(orderedScriptIds, currentIndex, currentIndex - 1)
                                onScriptsOrderChanged(newOrder)
                            },
                            enabled = canMoveUp
                        ) {
                            Text(text = stringResource(R.string.tool_move_up))
                        }
                        TextButton(
                            onClick = {
                                val newOrder = moveItem(orderedScriptIds, currentIndex, currentIndex + 1)
                                onScriptsOrderChanged(newOrder)
                            },
                            enabled = canMoveDown
                        ) {
                            Text(text = stringResource(R.string.tool_move_down))
                        }
                    }
                }
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
    onDiscardPalettePreview: () -> Unit = {}
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(horizontal = horizontalPadding, vertical = ToolsScreenDimens.verticalPadding),
        verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.gridSpacing)
    ) {
        item {
            Text(
                text = stringResource(R.string.settings_palette_preview_title),
                style = if (isExpanded) AnclaTextStyles.toolsTitleExpanded else AnclaTextStyles.toolsTitle,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
            Text(
                text = stringResource(R.string.settings_palette_preview_description),
                style = AnclaTextStyles.toolCardSubtitle,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(ToolsScreenDimens.gridSpacing))

            SensoryPalettePicker(
                selectedColorId = selectedColorId,
                onColorSelected = onPalettePreviewChanged
            )

            Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
            Text(
                text = if (hasPendingPaletteChanges) {
                    stringResource(R.string.settings_palette_unsaved_changes)
                } else {
                    stringResource(R.string.settings_palette_saved_state)
                },
                style = AnclaTextStyles.toolCardSubtitle,
                color = TextSecondary
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



