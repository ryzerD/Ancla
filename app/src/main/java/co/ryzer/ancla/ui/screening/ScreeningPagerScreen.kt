package co.ryzer.ancla.ui.screening

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
private fun getScreeningQuestions(): List<String> {
    return listOf(
        stringResource(R.string.screening_question_1),
        stringResource(R.string.screening_question_2),
        stringResource(R.string.screening_question_3),
        stringResource(R.string.screening_question_4),
        stringResource(R.string.screening_question_5),
        stringResource(R.string.screening_question_6),
        stringResource(R.string.screening_question_7),
        stringResource(R.string.screening_question_8),
        stringResource(R.string.screening_question_9),
        stringResource(R.string.screening_question_10)
    )
}

@Composable
private fun getResponseOptions(): List<String> {
    return listOf(
        stringResource(R.string.screening_response_option_1),
        stringResource(R.string.screening_response_option_2),
        stringResource(R.string.screening_response_option_3),
        stringResource(R.string.screening_response_option_4)
    )
}

private const val SCREENING_QUESTIONS_COUNT = 10

@Composable
fun ScreeningPagerScreen(
    onClose: () -> Unit,
    onComplete: () -> Unit,
    viewModel: ScreeningViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.hasSubmitted) {
        ScreeningResultsScreen(
            uiState = uiState,
            onRetake = { viewModel.retakeAssessment() },
            onClose = onClose
        )
    } else {
        ScreeningQuestionScreen(
            uiState = uiState,
            onAnswerSelected = { score -> viewModel.answerQuestion(uiState.currentQuestion, score) },
            onNextQuestion = { viewModel.nextQuestion() },
            onPreviousQuestion = { viewModel.previousQuestion() },
            onSubmit = { viewModel.submitAssessment(onComplete) },
            onClose = onClose
        )
    }
}

@Composable
private fun ScreeningQuestionScreen(
    uiState: ScreeningUiState,
    onAnswerSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onPreviousQuestion: () -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit
) {
    val screeningQuestions = getScreeningQuestions()
    val responseOptions = getResponseOptions()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(
                horizontal = ToolsScreenDimens.horizontalPaddingCompact,
                vertical = ToolsScreenDimens.verticalPadding
            )
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ToolsScreenDimens.headerBottomSpacer),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.tool_calm_map_title),
                style = AnclaTextStyles.toolsTitle,
                color = TextPrimary
            )
            IconButton(onClick = onClose, modifier = Modifier.size(ToolsScreenDimens.cardContentPadding)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(android.R.string.cancel),
                    tint = TextPrimary
                )
            }
        }

        // Progress Bar
        LinearProgressIndicator(
            progress = { (uiState.currentQuestion + 1) / SCREENING_QUESTIONS_COUNT.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = ScriptReaderButton,
            trackColor = TextSecondary.copy(alpha = 0.2f),
            strokeCap = StrokeCap.Round
        )

        Text(
            text = stringResource(
                R.string.screening_progress_format,
                uiState.currentQuestion + 1
            ),
            style = AnclaTextStyles.toolCardSubtitle,
            color = TextSecondary,
            modifier = Modifier.padding(top = ToolsScreenDimens.cardContentPadding)
        )

        Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))

        // Question Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.headerBottomSpacer)
        ) {
            Text(
                text = screeningQuestions[uiState.currentQuestion],
                style = AnclaTextStyles.toolsTitle,
                color = TextPrimary,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))

            // Response Options
            Column(
                verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.cardContentPadding),
                modifier = Modifier.fillMaxWidth()
            ) {
                responseOptions.forEachIndexed { index, option ->
                    ResponseOptionCard(
                        text = option,
                        isSelected = uiState.answers[uiState.currentQuestion] == index,
                        onClick = { onAnswerSelected(index) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer))

        // Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ToolsScreenDimens.cardContentPadding),
            horizontalArrangement = Arrangement.spacedBy(ToolsScreenDimens.cardContentPadding)
        ) {
            Button(
                onClick = onPreviousQuestion,
                enabled = uiState.currentQuestion > 0,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ScriptReaderButton.copy(alpha = 0.3f),
                    disabledContainerColor = TextSecondary.copy(alpha = 0.2f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.screening_button_previous),
                    modifier = Modifier.size(20.dp)
                )
            }

            Button(
                onClick = if (uiState.currentQuestion == 9) onSubmit else onNextQuestion,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(containerColor = ScriptReaderButton)
            ) {
                Text(
                    text = if (uiState.currentQuestion == 9) stringResource(R.string.screening_button_finish) 
                           else stringResource(R.string.screening_button_next),
                    color = SurfaceWhite
                )
                if (uiState.currentQuestion < 9) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = stringResource(R.string.screening_button_next),
                        modifier = Modifier.size(20.dp),
                        tint = SurfaceWhite
                    )
                }
            }
        }
    }
}

@Composable
private fun ResponseOptionCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(56.dp),
        shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ScriptReaderButton.copy(alpha = 0.2f) else SurfaceWhite,
            contentColor = if (isSelected) ScriptReaderButton else TextPrimary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ToolsScreenDimens.cardContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = if (isSelected) ScriptReaderButton else TextSecondary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Text(
                        text = "✓",
                        color = SurfaceWhite,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.size(ToolsScreenDimens.cardContentPadding))
            Text(
                text = text,
                style = AnclaTextStyles.toolCardTitle,
                color = if (isSelected) ScriptReaderButton else TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ScreeningResultsScreen(
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
                    text = uiState.primaryTrait,
                    style = AnclaTextStyles.toolsTitle,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(ToolsScreenDimens.cardContentPadding))
                Text(
                    text = stringResource(
                        R.string.screening_score_format,
                        uiState.totalScore
                    ),
                    style = AnclaTextStyles.toolCardSubtitle,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(ToolsScreenDimens.headerBottomSpacer.times(2)))

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