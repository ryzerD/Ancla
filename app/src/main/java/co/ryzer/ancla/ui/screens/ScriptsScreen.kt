package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.PanToolAlt
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.data.Script
import co.ryzer.ancla.ui.model.ScriptCardUi
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

@Composable
fun ScriptsScreen(
    windowSizeClass: WindowSizeClass? = null,
    scripts: List<Script> = emptyList(),
    onScriptClick: (String) -> Unit = {},
    onNewScriptClick: () -> Unit = {}
) {
    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded
    val horizontalPadding = if (isExpanded) {
        ToolsScreenDimens.horizontalPaddingExpanded
    } else {
        ToolsScreenDimens.horizontalPaddingCompact
    }

    val scriptCards = scripts.map { script ->
        ScriptCardUi(
            id = script.id,
            title = script.title,
            subtitle = script.subtitle,
            icon = scriptIconForCategory(script.categoryId),
            backgroundColor = scriptColorForStyle(script.styleId),
            showsAddIndicator = script.id == "ask_help"
        )
    }

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
            text = stringResource(R.string.scripts_screen_title),
            style = AnclaTextStyles.toolsTitle,
            color = TextPrimary
        )
        Text(
            text = stringResource(R.string.scripts_screen_subtitle),
            style = AnclaTextStyles.toolCardSubtitle,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.gridSpacing),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(items = scriptCards, key = { it.id }) { script ->
                ScriptCard(
                    script = script,
                    onClick = { onScriptClick(script.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(ToolsScreenDimens.gridSpacing))

        Button(
            onClick = onNewScriptClick,
            shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
            colors = ButtonDefaults.buttonColors(containerColor = CardGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = TextPrimary
            )
            Spacer(modifier = Modifier.size(ToolsScreenDimens.orderControlsSpacing))
            Text(
                text = stringResource(R.string.scripts_new_button),
                style = AnclaTextStyles.toolCardTitle,
                color = TextPrimary
            )
        }
    }
}

private fun scriptIconForCategory(categoryId: String) = when (categoryId) {
    "social" -> Icons.Outlined.ChatBubbleOutline
    "needs" -> Icons.Outlined.PanToolAlt
    "limits" -> Icons.Outlined.NotificationsOff
    "errands" -> Icons.Outlined.ShoppingBag
    else -> Icons.Outlined.ChatBubbleOutline
}

private fun scriptColorForStyle(styleId: String): Color = when (styleId) {
    "lavender" -> CardLavender
    "rose" -> CardRose
    "sand" -> CardPeach
    "mixed" -> CardGreen
    else -> CardLavender
}

@Composable
private fun ScriptCard(
    script: ScriptCardUi,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = script.backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ToolsScreenDimens.cardContentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ToolsScreenDimens.gridSpacing)
        ) {
            Icon(
                imageVector = script.icon,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(ToolsScreenDimens.iconPlaceholderSize)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = script.title,
                    style = AnclaTextStyles.taskTitle,
                    color = TextPrimary
                )
                Text(
                    text = script.subtitle,
                    style = AnclaTextStyles.taskTime,
                    color = TextPrimary
                )
            }

            if (script.showsAddIndicator) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(ToolsScreenDimens.iconPlaceholderSize)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScriptsScreenPreview() {
    AnclaTheme {
        ScriptsScreen()
    }
}
