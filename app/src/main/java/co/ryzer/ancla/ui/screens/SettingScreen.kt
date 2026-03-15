package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import co.ryzer.ancla.R
import co.ryzer.ancla.data.DefaultToolOrder
import co.ryzer.ancla.data.Script
import co.ryzer.ancla.data.ToolIds
import co.ryzer.ancla.data.ToolOrderEntry
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.CardPeach
import co.ryzer.ancla.ui.theme.CardRose
import co.ryzer.ancla.ui.theme.OnboardingSensorialDimens
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens
import co.ryzer.ancla.ui.theme.CardGreen

private data class SettingsToolItem(
    val toolId: String,
    val titleResId: Int
)

private data class SettingsScriptItem(
    val scriptId: String,
    val title: String
)

private data class PaletteOption(
    val id: String,
    val labelResId: Int,
    val color: Color
)

private fun moveTool(order: List<String>, fromIndex: Int, toIndex: Int): List<String> {
    if (fromIndex !in order.indices || toIndex !in order.indices || fromIndex == toIndex) {
        return order
    }
    val mutable = order.toMutableList()
    val item = mutable.removeAt(fromIndex)
    mutable.add(toIndex, item)
    return mutable
}

@Composable
fun SettingsScreen(
    windowSizeClass: WindowSizeClass? = null,
    toolOrder: List<ToolOrderEntry> = DefaultToolOrder,
    scripts: List<Script> = emptyList(),
    selectedColorId: String = "lavender",
    hasPendingPaletteChanges: Boolean = false,
    onToolsOrderChanged: (List<ToolOrderEntry>) -> Unit = {},
    onScriptsOrderChanged: (List<String>) -> Unit = {},
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

    val catalog = listOf(
        SettingsToolItem(toolId = ToolIds.DECODER, titleResId = R.string.tool_decoder_title),
        SettingsToolItem(toolId = ToolIds.TASKS, titleResId = R.string.tool_tasks_title),
        SettingsToolItem(toolId = ToolIds.SCRIPTS, titleResId = R.string.tool_scripts_title),
        SettingsToolItem(toolId = ToolIds.BREATHING, titleResId = R.string.tool_breathing_title),
        SettingsToolItem(toolId = ToolIds.SOS, titleResId = R.string.tool_sos_title)
    )
    val orderedToolIds = toolOrder.sortedBy { it.position }.map { it.toolId }
    val catalogById = catalog.associateBy { it.toolId }
    val orderedTools = orderedToolIds.mapNotNull { catalogById[it] }
    val orderedScripts = scripts
        .sortedBy { it.position }
        .map { script -> SettingsScriptItem(scriptId = script.id, title = script.title) }
    val orderedScriptIds = orderedScripts.map { it.scriptId }
    val paletteOptions = listOf(
        PaletteOption(id = "lavender", labelResId = R.string.palette_lavender, color = CardLavender),
        PaletteOption(id = "rose", labelResId = R.string.palette_rose, color = CardRose),
        PaletteOption(id = "sage", labelResId = R.string.palette_sage, color = CardGreen),
        PaletteOption(id = "peach", labelResId = R.string.palette_peach, color = CardPeach)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(horizontal = horizontalPadding, vertical = ToolsScreenDimens.verticalPadding)
    ) {
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
        Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.gridSpacing),
            modifier = Modifier.fillMaxWidth()
        ) {
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
                            TextButton(onClick = {
                                val newOrder = moveTool(orderedToolIds, currentIndex, currentIndex - 1)
                                onToolsOrderChanged(newOrder.mapIndexed { index, toolId ->
                                    ToolOrderEntry(toolId = toolId, position = index)
                                })
                            }, enabled = canMoveUp) {
                                Text(text = stringResource(R.string.tool_move_up))
                            }
                            TextButton(onClick = {
                                val newOrder = moveTool(orderedToolIds, currentIndex, currentIndex + 1)
                                onToolsOrderChanged(newOrder.mapIndexed { index, toolId ->
                                    ToolOrderEntry(toolId = toolId, position = index)
                                })
                            }, enabled = canMoveDown) {
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
                            TextButton(onClick = {
                                val newOrder = moveTool(orderedScriptIds, currentIndex, currentIndex - 1)
                                onScriptsOrderChanged(newOrder)
                            }, enabled = canMoveUp) {
                                Text(text = stringResource(R.string.tool_move_up))
                            }
                            TextButton(onClick = {
                                val newOrder = moveTool(orderedScriptIds, currentIndex, currentIndex + 1)
                                onScriptsOrderChanged(newOrder)
                            }, enabled = canMoveDown) {
                                Text(text = stringResource(R.string.tool_move_down))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))
                Text(
                    text = stringResource(R.string.settings_palette_preview_title),
                    style = AnclaTextStyles.toolCardTitle,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
                Text(
                    text = stringResource(R.string.settings_palette_preview_description),
                    style = AnclaTextStyles.toolCardSubtitle,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(ToolsScreenDimens.gridSpacing))

                Column(
                    verticalArrangement = Arrangement.spacedBy(OnboardingSensorialDimens.pickerGridSpacing)
                ) {
                    paletteOptions.chunked(2).forEach { rowOptions ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(OnboardingSensorialDimens.pickerGridSpacing)
                        ) {
                            rowOptions.forEach { option ->
                                val isSelected = option.id == selectedColorId
                                Box(
                                    modifier = Modifier
                                        .size(OnboardingSensorialDimens.pickerItemSize)
                                        .clip(RoundedCornerShape(OnboardingSensorialDimens.pickerItemCornerRadius))
                                        .background(option.color)
                                        .border(
                                            width = if (isSelected) {
                                                OnboardingSensorialDimens.pickerSelectedBorderWidth
                                            } else {
                                                OnboardingSensorialDimens.pickerUnselectedBorderWidth
                                            },
                                            color = if (isSelected) TextPrimary else Color.Transparent,
                                            shape = RoundedCornerShape(OnboardingSensorialDimens.pickerItemCornerRadius)
                                        )
                                        .clickable { onPalettePreviewChanged(option.id) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ChatBubble,
                                        contentDescription = stringResource(option.labelResId),
                                        tint = TextPrimary,
                                        modifier = Modifier
                                            .size(OnboardingSensorialDimens.pickerIconSize)
                                    )
                                }
                            }
                        }
                    }
                }

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
}