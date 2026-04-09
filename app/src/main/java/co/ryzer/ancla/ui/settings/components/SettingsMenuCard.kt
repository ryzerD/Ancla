

package co.ryzer.ancla.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.model.SettingsMenuItemUi
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.SettingsScreenDimens
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary

@Composable
fun SettingsMenuCard(
    item: SettingsMenuItemUi,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
                text = item.number.toString(),
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
                Text(
                    text = stringResource(item.titleResId),
                    style = AnclaTextStyles.toolsTitle.copy(),
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
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

@Preview(showBackground = true, widthDp = 400, heightDp = 120)
@Composable
private fun SettingsMenuCardPreview() {
    AnclaTheme {
        SettingsMenuCard(
            item = SettingsMenuItemUi(
                number = 1,
                titleResId = R.string.settings_profile_title,
                icon = Icons.Outlined.ChevronRight,
                badgeColor = androidx.compose.ui.graphics.Color(0xFFE2D9EE),
                onClick = {}
            )
        )
    }
}

