package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import co.ryzer.ancla.R
import co.ryzer.ancla.data.DefaultToolOrder
import co.ryzer.ancla.data.ToolIds
import co.ryzer.ancla.data.ToolOrderEntry
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens

private data class SettingsToolItem(
    val toolId: String,
    val titleResId: Int
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
    onToolsOrderChanged: (List<ToolOrderEntry>) -> Unit = {}
) {
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
        }
    }
}