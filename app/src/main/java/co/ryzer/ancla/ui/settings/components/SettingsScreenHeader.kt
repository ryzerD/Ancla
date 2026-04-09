package co.ryzer.ancla.ui.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.SettingsScreenDimens
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary

@Composable
fun SettingsScreenHeader(
    modifier: Modifier = Modifier,
    isExpanded: Boolean
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        androidx.compose.material3.Text(
            text = stringResource(R.string.settings_title),
            style = if (isExpanded) {
                AnclaTextStyles.toolsTitleExpanded.copy(fontWeight = FontWeight.Bold)
            } else {
                AnclaTextStyles.toolsTitle.copy(fontWeight = FontWeight.Bold)
            },
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(SettingsScreenDimens.iconTextSpacing))
        androidx.compose.material3.Text(
            text = stringResource(R.string.settings_screen_subtitle),
            style = AnclaTextStyles.toolCardSubtitle,
            color = TextSecondary
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
private fun SettingsScreenHeaderPreview() {
    AnclaTheme {
        SettingsScreenHeader(isExpanded = false)
    }
}

