package co.ryzer.ancla.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.SettingsScreenDimens
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary

@Composable
fun SettingsTipCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SettingsScreenDimens.menuCardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = CardLavender.copy(alpha = 0.18f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SettingsScreenDimens.menuCardHorizontalPadding),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(SettingsScreenDimens.iconTextSpacing)
        ) {
            Text(
                text = "✦",
                style = AnclaTextStyles.toolCardTitle,
                color = TextPrimary
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.settings_screen_tip_title),
                    style = AnclaTextStyles.toolCardTitle,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(R.string.settings_screen_tip_body),
                    style = AnclaTextStyles.toolCardSubtitle,
                    color = TextSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 160)
@Composable
private fun SettingsTipCardPreview() {
    AnclaTheme {
        SettingsTipCard()
    }
}


