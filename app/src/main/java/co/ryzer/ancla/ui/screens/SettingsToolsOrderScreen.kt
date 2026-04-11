package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FrontHand
import androidx.compose.material.icons.outlined.PanToolAlt
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.data.DefaultToolOrder
import co.ryzer.ancla.data.Script
import co.ryzer.ancla.data.ToolIds
import co.ryzer.ancla.data.ToolOrderEntry
import co.ryzer.ancla.ui.components.AnclaTopBar
import co.ryzer.ancla.ui.settings.components.SettingsOrderCard
import co.ryzer.ancla.ui.settings.components.SettingsOrderHeader
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.CardPeach
import co.ryzer.ancla.ui.theme.CardRose
import co.ryzer.ancla.ui.theme.ToolsScreenDimens

private data class SettingsOrderUiItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val accentColor: Color
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
        SettingsOrderUiItem(
            id = ToolIds.TASKS,
            title = stringResource(R.string.tool_tasks_title),
            icon = Icons.Outlined.PanToolAlt,
            accentColor = CardGreen
        ),
        SettingsOrderUiItem(
            id = ToolIds.SCRIPTS,
            title = stringResource(R.string.tool_scripts_title),
            icon = Icons.Outlined.FrontHand,
            accentColor = CardLavender
        ),
        SettingsOrderUiItem(
            id = ToolIds.BREATHING,
            title = stringResource(R.string.tool_breathing_title),
            icon = Icons.Outlined.Air,
            accentColor = CardPeach
        ),
        SettingsOrderUiItem(
            id = ToolIds.SOS,
            title = stringResource(R.string.tool_sos_title),
            icon = Icons.Outlined.Build,
            accentColor = CardRose
        ),
        SettingsOrderUiItem(
            id = ToolIds.CALM_MAP,
            title = stringResource(R.string.tool_calm_map_title),
            icon = Icons.Outlined.Favorite,
            accentColor = CardLavender
        )
    )
    val toolCatalogById = toolCatalog.associateBy { it.id }

    val orderedToolIds = toolOrder.sortedBy { it.position }.map { it.toolId }
    val orderedTools = orderedToolIds.mapNotNull { id -> toolCatalogById[id] }

    val orderedScripts = scripts
        .sortedBy { it.position }
        .map { script ->
            SettingsOrderUiItem(
                id = script.id,
                title = script.title,
                icon = Icons.Outlined.FrontHand,
                accentColor = CardLavender
            )
        }
    val orderedScriptIds = orderedScripts.map { it.id }

    val toolsOrderTitle = stringResource(R.string.settings_tools_order_title)
    val toolsOrderDescription = stringResource(R.string.settings_tools_order_description)
    val scriptsOrderTitle = stringResource(R.string.settings_scripts_order_title)
    val scriptsOrderDescription = stringResource(R.string.settings_scripts_order_description)

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
            orderSection(
                isExpanded = isExpanded,
                title = toolsOrderTitle,
                description = toolsOrderDescription,
                items = orderedTools,
                onMoveUp = { index ->
                    val newOrder = moveItem(orderedToolIds, index, index - 1)
                    onToolsOrderChanged(
                        newOrder.mapIndexed { position, toolId ->
                            ToolOrderEntry(toolId = toolId, position = position)
                        }
                    )
                },
                onMoveDown = { index ->
                    val newOrder = moveItem(orderedToolIds, index, index + 1)
                    onToolsOrderChanged(
                        newOrder.mapIndexed { position, toolId ->
                            ToolOrderEntry(toolId = toolId, position = position)
                        }
                    )
                }
            )

            item {
                Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))
            }

            orderSection(
                isExpanded = isExpanded,
                title = scriptsOrderTitle,
                description = scriptsOrderDescription,
                items = orderedScripts,
                onMoveUp = { index ->
                    val newOrder = moveItem(orderedScriptIds, index, index - 1)
                    onScriptsOrderChanged(newOrder)
                },
                onMoveDown = { index ->
                    val newOrder = moveItem(orderedScriptIds, index, index + 1)
                    onScriptsOrderChanged(newOrder)
                }
            )
        }
    }
}

private fun LazyListScope.orderSection(
    isExpanded: Boolean,
    title: String,
    description: String,
    items: List<SettingsOrderUiItem>,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit
) {
    item {
        SettingsOrderHeader(
            isExpanded = isExpanded,
            title = title,
            description = description
        )
    }

    itemsIndexed(items = items, key = { _, item -> item.id }) { index, item ->
        SettingsOrderCard(
            title = item.title,
            icon = item.icon,
            accentColor = item.accentColor,
            canMoveUp = index > 0,
            canMoveDown = index < items.lastIndex,
            moveUpContentDescription = stringResource(R.string.tool_move_up),
            moveDownContentDescription = stringResource(R.string.tool_move_down),
            onMoveUp = { onMoveUp(index) },
            onMoveDown = { onMoveDown(index) },
            modifier = Modifier.fillMaxWidth()
        )
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


