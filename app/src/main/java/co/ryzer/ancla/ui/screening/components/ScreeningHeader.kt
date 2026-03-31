package co.ryzer.ancla.ui.screening.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens

@Composable
internal fun ScreeningHeader(onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = ToolsScreenDimens.headerBottomSpacer),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier.size(ToolsScreenDimens.cardContentPadding)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = stringResource(android.R.string.cancel),
                tint = TextPrimary
            )
        }
        Text(
            text = stringResource(R.string.tool_calm_map_title),
            style = AnclaTextStyles.toolsTitle,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        IconButton(
            onClick = {},
            enabled = false,
            modifier = Modifier.size(ToolsScreenDimens.cardContentPadding)
        ) {}

    }
}

@Preview(showBackground = true)
@Composable
private fun ScreeningHeaderPreview() {
    AnclaTheme {
        ScreeningHeader(onClose = {})
    }
}
