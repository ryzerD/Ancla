package co.ryzer.ancla.ui.screening

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

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
