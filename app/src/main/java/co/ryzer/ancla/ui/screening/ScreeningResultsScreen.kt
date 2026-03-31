package co.ryzer.ancla.ui.screening

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.ScreeningResultsDimens
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
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.screening_results_title),
                style = AnclaTextStyles.toolsTitle,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(resultTitleRes(uiState.totalScore)),
                style = AnclaTextStyles.toolsTitle,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ScreeningResultsDimens.resultTitleTopPadding)
            )

            // Score en tarjeta
            Card(
                modifier = Modifier
                    .fillMaxWidth(ScreeningResultsDimens.scoreCardWidthFraction)
                    .height(ScreeningResultsDimens.scoreCardHeight),
                shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = CardLavender)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(ScreeningResultsDimens.scoreCardContentPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.screening_score_format, uiState.totalScore),
                        style = AnclaTextStyles.toolsTitle,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Descripción del resultado
            Text(
                text = stringResource(resultDescriptionRes(uiState.totalScore)),
                style = AnclaTextStyles.toolCardSubtitle,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(ScreeningResultsDimens.descriptionWidthFraction)
                    .padding(top = ScreeningResultsDimens.descriptionTopPadding)
            )

            // Disclaimer
            Text(
                text = stringResource(R.string.result_disclaimer),
                style = AnclaTextStyles.toolCardSubtitle,
                color = TextSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(ScreeningResultsDimens.disclaimerWidthFraction)
                    .padding(top = ScreeningResultsDimens.disclaimerTopPadding)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = ScreeningResultsDimens.actionsTopPadding,
                    bottom = ToolsScreenDimens.cardContentPadding
                ),
            verticalArrangement = Arrangement.spacedBy(ScreeningResultsDimens.actionsSpacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onRetake,
                modifier = Modifier
                    .fillMaxWidth(ScreeningResultsDimens.actionButtonsWidthFraction)
                    .height(ScreeningResultsDimens.actionButtonHeight),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ScriptReaderButton.copy(alpha = 0.2f),
                    contentColor = ScriptReaderButton
                )
            ) {
                Text(stringResource(R.string.screening_button_retake))
            }

            Button(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth(ScreeningResultsDimens.actionButtonsWidthFraction)
                    .height(ScreeningResultsDimens.actionButtonHeight),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(containerColor = ScriptReaderButton)
            ) {
                Text(stringResource(R.string.screening_button_done), color = SurfaceWhite)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreeningResultsScreenPreview() {
    AnclaTheme {
        ScreeningResultsScreen(
            uiState = ScreeningUiState(
                totalScore = 15
            ),
            onRetake = {},
            onClose = {}
        )
    }
}
