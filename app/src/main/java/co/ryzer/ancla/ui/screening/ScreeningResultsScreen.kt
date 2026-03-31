package co.ryzer.ancla.ui.screening

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.ScriptReaderButton
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens

@Composable
internal fun ScreeningResultsScreen(
    uiState: ScreeningUiState,
    onRetake: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(ToolsScreenDimens.horizontalPaddingCompact),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.screening_results_title),
            style = AnclaTextStyles.toolsTitle,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))

        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(ToolsScreenDimens.headerBottomSpacer),
            shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = CardLavender)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(primaryTraitLabelRes(uiState.totalScore)),
                    style = AnclaTextStyles.toolsTitle,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(ToolsScreenDimens.cardContentPadding))
                Text(
                    text = stringResource(R.string.screening_score_format, uiState.totalScore),
                    style = AnclaTextStyles.toolCardSubtitle,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer.times(2)))

        Button(
            onClick = onRetake,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp),
            shape = RoundedCornerShape(percent = 50),
            colors = ButtonDefaults.buttonColors(
                containerColor = ScriptReaderButton.copy(alpha = 0.2f),
                contentColor = ScriptReaderButton
            )
        ) {
            Text(stringResource(R.string.screening_button_retake))
        }

        Spacer(modifier = Modifier.height(ToolsScreenDimens.cardContentPadding))

        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp),
            shape = RoundedCornerShape(percent = 50),
            colors = ButtonDefaults.buttonColors(containerColor = ScriptReaderButton)
        ) {
            Text(stringResource(R.string.screening_button_done), color = SurfaceWhite)
        }
    }
}

