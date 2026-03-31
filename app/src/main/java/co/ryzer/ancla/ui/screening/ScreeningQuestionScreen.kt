package co.ryzer.ancla.ui.screening

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import co.ryzer.ancla.ui.screening.components.ResponseOptionCard
import co.ryzer.ancla.ui.screening.components.ScreeningHeader
import co.ryzer.ancla.ui.screening.components.ScreeningNavigationBar
import co.ryzer.ancla.ui.screening.components.ScreeningProgress
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens
import androidx.compose.material3.Text

@Composable
internal fun ScreeningQuestionScreen(
    uiState: ScreeningUiState,
    onAnswerSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onPreviousQuestion: () -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit
) {
    val questions = screeningQuestions()
    val options = screeningResponseOptions()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(
                horizontal = ToolsScreenDimens.horizontalPaddingCompact,
                vertical = ToolsScreenDimens.verticalPadding
            )
    ) {
        ScreeningHeader(onClose = onClose)
        ScreeningProgress(currentQuestion = uiState.currentQuestion)

        Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.headerBottomSpacer)
        ) {
            Text(
                text = questions[uiState.currentQuestion],
                style = AnclaTextStyles.toolsTitle,
                color = TextPrimary,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))

            Column(
                verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.cardContentPadding),
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, option ->
                    ResponseOptionCard(
                        text = option,
                        isSelected = uiState.answers[uiState.currentQuestion] == index,
                        onClick = { onAnswerSelected(index) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))

        ScreeningNavigationBar(
            currentQuestion = uiState.currentQuestion,
            onPreviousQuestion = onPreviousQuestion,
            onNextOrSubmit = {
                if (uiState.currentQuestion == SCREENING_QUESTIONS_COUNT - 1) {
                    onSubmit()
                } else {
                    onNextQuestion()
                }
            }
        )
    }
}

