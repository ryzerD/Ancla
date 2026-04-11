package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.data.DefaultToolOrder
import co.ryzer.ancla.data.Script
import co.ryzer.ancla.data.ToolOrderEntry
import co.ryzer.ancla.ui.model.SettingsMenuItemUi
import co.ryzer.ancla.ui.settings.components.SettingsMenuCard
import co.ryzer.ancla.ui.settings.components.SettingsScreenHeader
import co.ryzer.ancla.ui.settings.components.SettingsTipCard
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.CardPeach
import co.ryzer.ancla.ui.theme.CardRose
import co.ryzer.ancla.ui.theme.SettingsScreenDimens

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
    onNotificationsClick: () -> Unit = {},
    onBack: (() -> Unit)? = null
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
        SettingsMenuItemUi(
            number = 1,
            titleResId = R.string.settings_profile_title,
            icon = Icons.Outlined.PersonOutline,
            badgeColor = CardLavender,
            onClick = onProfileClick
        ),
        SettingsMenuItemUi(
            number = 2,
            titleResId = R.string.settings_visual_title,
            icon = Icons.Outlined.Palette,
            badgeColor = CardPeach,
            onClick = onVisualPreferencesClick
        ),
        SettingsMenuItemUi(
            number = 3,
            titleResId = R.string.settings_tools_org_title,
            icon = Icons.Outlined.FormatListNumbered,
            badgeColor = CardRose,
            onClick = onToolsOrganizationClick
        ),
        SettingsMenuItemUi(
            number = 4,
            titleResId = R.string.settings_notifications_title,
            icon = Icons.Outlined.NotificationsNone,
            badgeColor = CardGreen,
            onClick = onNotificationsClick
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(
                horizontal = horizontalPadding,
                vertical = SettingsScreenDimens.verticalPadding
            )
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SettingsScreenDimens.menuItemSpacing)
    ) {
        item {
            SettingsScreenHeader(
                isExpanded = isExpanded
            )
        }

        item {
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier
                    .height(SettingsScreenDimens.titleBottomSpacing)
            )
        }

        items(items = menuItems) { item ->
            Box(
            ) {
                SettingsMenuCard(item = item)
            }
        }

        item {
            SettingsTipCard(
                modifier = Modifier
                    .padding(top = SettingsScreenDimens.menuItemSpacing)
            )
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