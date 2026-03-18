package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.data.DefaultToolOrder
import co.ryzer.ancla.data.Script
import co.ryzer.ancla.data.ToolOrderEntry
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.CardPeach
import co.ryzer.ancla.ui.theme.CardRose
import co.ryzer.ancla.ui.theme.SettingsScreenDimens
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary

private data class SettingsToolItem(
    val number: Int,
    val titleResId: Int,
    val subtitleResId: Int,
    val icon: ImageVector,
    val badgeColor: Color,
    val onClick: () -> Unit
)

@Composable
@Suppress("UNUSED_PARAMETER")
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
    onDiscardPalettePreview: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onVisualPreferencesClick: () -> Unit = {},
    onToolsOrganizationClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    DisposableEffect(Unit) {
        onDispose { onDiscardPalettePreview() }
    }

    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded
    val horizontalPadding = if (isExpanded) {
        SettingsScreenDimens.horizontalPaddingExpanded
    } else {
        SettingsScreenDimens.horizontalPaddingCompact
    }

    val menuItems = listOf(
        SettingsToolItem(
            number = 1,
            titleResId = R.string.settings_profile_title,
            subtitleResId = R.string.settings_profile_subtitle,
            icon = Icons.Outlined.PersonOutline,
            badgeColor = CardLavender,
            onClick = onProfileClick
        ),
        SettingsToolItem(
            number = 2,
            titleResId = R.string.settings_visual_title,
            subtitleResId = R.string.settings_visual_subtitle,
            icon = Icons.Outlined.Palette,
            badgeColor = CardPeach,
            onClick = onVisualPreferencesClick
        ),
        SettingsToolItem(
            number = 3,
            titleResId = R.string.settings_tools_org_title,
            subtitleResId = R.string.settings_tools_org_subtitle,
            icon = Icons.Outlined.FormatListNumbered,
            badgeColor = CardRose,
            onClick = onToolsOrganizationClick
        ),
        SettingsToolItem(
            number = 4,
            titleResId = R.string.settings_notifications_title,
            subtitleResId = R.string.settings_notifications_subtitle,
            icon = Icons.Outlined.NotificationsNone,
            badgeColor = CardGreen,
            onClick = onNotificationsClick
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(
                horizontal = horizontalPadding,
                vertical = SettingsScreenDimens.verticalPadding
            )
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = if (isExpanded) {
                AnclaTextStyles.toolsTitleExpanded.copy(fontWeight = FontWeight.Bold)
            } else {
                AnclaTextStyles.toolsTitle.copy(fontWeight = FontWeight.Bold)
            },
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(SettingsScreenDimens.titleBottomSpacing))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(SettingsScreenDimens.menuItemSpacing)
        ) {
            menuItems.forEach { item ->
                SettingsMenuCard(item = item)
            }
        }
    }
}

@Composable
private fun SettingsMenuCard(item: SettingsToolItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SettingsScreenDimens.badgeToCardSpacing)
    ) {
        Box(
            modifier = Modifier
                .size(SettingsScreenDimens.badgeSize)
                .background(
                    color = item.badgeColor,
                    shape = RoundedCornerShape(SettingsScreenDimens.badgeCornerRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${item.number}.",
                style = AnclaTextStyles.toolCardTitle,
                color = TextPrimary
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(SettingsScreenDimens.menuCardCornerRadius),
            elevation = CardDefaults.cardElevation(defaultElevation = SettingsScreenDimens.menuCardElevation),
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = item.onClick)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = SettingsScreenDimens.menuCardHorizontalPadding,
                        vertical = SettingsScreenDimens.menuCardVerticalPadding
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(SettingsScreenDimens.menuIconSize)
                )
                Spacer(modifier = Modifier.size(SettingsScreenDimens.iconTextSpacing))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(item.titleResId),
                        style = AnclaTextStyles.toolsTitle.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(item.subtitleResId),
                        style = AnclaTextStyles.toolCardSubtitle,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.size(SettingsScreenDimens.iconTextSpacing))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(SettingsScreenDimens.chevronSize)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun SettingsScreenPreview() {
    AnclaTheme {
        SettingsScreen(
            scripts = listOf(
                Script(
                    id = "1",
                    title = "Perfil",
                    subtitle = "Persona",
                    message = "",
                    categoryId = "1",
                    styleId = "1",
                    position = 0
                )
            ),
            hasPendingPaletteChanges = true
        )
    }
}