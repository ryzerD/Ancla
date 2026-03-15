package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FrontHand
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.PanToolAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.data.DefaultToolOrder
import co.ryzer.ancla.data.ToolIds
import co.ryzer.ancla.data.ToolOrderEntry
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.CardPeach
import co.ryzer.ancla.ui.theme.CardRose
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens

private data class ToolItemUi(
    val toolId: String,
    val titleResId: Int,
    val subtitleResId: Int,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun ToolsScreen(
    onNavigateToDecoder: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToScripts: () -> Unit,
    windowSizeClass: WindowSizeClass? = null,
    toolOrder: List<ToolOrderEntry> = DefaultToolOrder
) {
    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded
    val horizontalPadding = if (isExpanded) {
        ToolsScreenDimens.horizontalPaddingExpanded
    } else {
        ToolsScreenDimens.horizontalPaddingCompact
    }
    val gridColumns = if (isExpanded) {
        ToolsScreenDimens.columnsExpanded
    } else {
        ToolsScreenDimens.columnsCompact
    }

    val catalog = listOf(
        ToolItemUi(
            toolId = ToolIds.DECODER,
            titleResId = R.string.tool_decoder_title,
            subtitleResId = R.string.tool_decoder_subtitle,
            icon = Icons.Outlined.ChatBubbleOutline,
            color = CardGreen,
            onClick = onNavigateToDecoder
        ),
        ToolItemUi(
            toolId = ToolIds.TASKS,
            titleResId = R.string.tool_tasks_title,
            subtitleResId = R.string.tool_tasks_subtitle,
            icon = Icons.Outlined.PanToolAlt,
            color = CardGreen,
            onClick = onNavigateToTasks
        ),
        ToolItemUi(
            toolId = ToolIds.SCRIPTS,
            titleResId = R.string.tool_scripts_title,
            subtitleResId = R.string.tool_scripts_subtitle,
            icon = Icons.Outlined.FrontHand,
            color = CardLavender,
            onClick = onNavigateToScripts
        ),
        ToolItemUi(
            toolId = ToolIds.BREATHING,
            titleResId = R.string.tool_breathing_title,
            subtitleResId = R.string.tool_breathing_subtitle,
            icon = Icons.Outlined.Air,
            color = CardPeach,
            onClick = {}
        ),
        ToolItemUi(
            toolId = ToolIds.SOS,
            titleResId = R.string.tool_sos_title,
            subtitleResId = R.string.tool_sos_subtitle,
            icon = Icons.Outlined.Build,
            color = CardRose,
            onClick = {}
        )
    )

    val orderedToolIds = toolOrder.sortedBy { it.position }.map { it.toolId }
    val catalogById = catalog.associateBy { it.toolId }
    val orderedTools = orderedToolIds.mapNotNull { catalogById[it] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(
                horizontal = horizontalPadding,
                vertical = ToolsScreenDimens.verticalPadding
            )
    ) {
        Text(
            text = stringResource(R.string.tools_support_label),
            color = TextSecondary,
            style = AnclaTextStyles.toolsSupportLabel
        )
        Text(
            text = stringResource(R.string.tools_title),
            style = if (isExpanded) {
                AnclaTextStyles.toolsTitleExpanded
            } else {
                AnclaTextStyles.toolsTitle
            },
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))

        LazyVerticalGrid(
            columns = GridCells.Fixed(gridColumns),
            horizontalArrangement = Arrangement.spacedBy(ToolsScreenDimens.gridSpacing),
            verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.gridSpacing),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items = orderedTools, key = { it.toolId }) { tool ->
                ToolCard(
                    title = stringResource(tool.titleResId),
                    subtitle = stringResource(tool.subtitleResId),
                    icon = tool.icon,
                    color = tool.color,
                    onClick = tool.onClick
                )
            }
        }
    }
}

@Composable
fun ToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            // Slightly squarer ratio improves adaptation across phone and tablet grids.
            .aspectRatio(ToolsScreenDimens.cardAspectRatio)
            .clickable { onClick() },
        shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .padding(ToolsScreenDimens.cardContentPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(ToolsScreenDimens.iconPlaceholderSize)
                    .background(
                        Color.Black.copy(alpha = ToolsScreenDimens.iconPlaceholderAlpha),
                        RoundedCornerShape(ToolsScreenDimens.iconPlaceholderCornerRadius)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
            Text(
                text = title,
                style = AnclaTextStyles.toolCardTitle,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = AnclaTextStyles.toolCardSubtitle,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun ToolsScreenCompactPreview() {
    AnclaTheme {
        ToolsScreen(onNavigateToDecoder = {}, onNavigateToTasks = {}, onNavigateToScripts = {})
    }
}

@Preview(showBackground = true, widthDp = 900, heightDp = 600)
@Composable
fun ToolsScreenExpandedPreview() {
    AnclaTheme {
        ToolsScreen(onNavigateToDecoder = {}, onNavigateToTasks = {}, onNavigateToScripts = {})
    }
}

@Preview(showBackground = true, widthDp = 200)
@Composable
fun ToolCardPreview() {
    AnclaTheme {
        ToolCard(
            title = "Decoder",
            subtitle = "Translate your feelings",
            icon = Icons.Outlined.ChatBubbleOutline,
            color = CardGreen,
            onClick = {}
        )
    }
}
