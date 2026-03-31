package co.ryzer.ancla.ui.screening

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ryzer.ancla.data.UserAssessmentResult
import co.ryzer.ancla.data.repository.UserAssessmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScreeningUiState(
    val currentQuestion: Int = 0,
    val answers: List<Int> = List(10) { 0 }, // 0-3 score per question
    val totalScore: Int = 0,
    val primaryTrait: String = "",
    val isLoading: Boolean = false,
    val hasSubmitted: Boolean = false,
    val previousAssessment: UserAssessmentResult? = null
)

@HiltViewModel
class ScreeningViewModel @Inject constructor(
    private val repository: UserAssessmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScreeningUiState())
    val uiState: StateFlow<ScreeningUiState> = _uiState

    init {
        viewModelScope.launch {
            val previousAssessment = repository.getAssessment()
            _uiState.value = _uiState.value.copy(previousAssessment = previousAssessment)
        }
    }

    fun answerQuestion(questionIndex: Int, score: Int) {
        val updatedAnswers = _uiState.value.answers.toMutableList()
        updatedAnswers[questionIndex] = score.coerceIn(0, 3)
        _uiState.value = _uiState.value.copy(answers = updatedAnswers)
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState.currentQuestion < 9) {
            _uiState.value = currentState.copy(currentQuestion = currentState.currentQuestion + 1)
        }
    }

    fun previousQuestion() {
        val currentState = _uiState.value
        if (currentState.currentQuestion > 0) {
            _uiState.value = currentState.copy(currentQuestion = currentState.currentQuestion - 1)
        }
    }

    fun submitAssessment(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val answers = _uiState.value.answers
                val totalScore = answers.sum()
                val primaryTrait = calculatePrimaryTrait(totalScore)

                val assessment = UserAssessmentResult(
                    totalScore = totalScore,
                    primaryTrait = primaryTrait,
                    completedAt = System.currentTimeMillis(),
                    assessmentData = answers.joinToString(",")
                )

                repository.saveAssessment(assessment)

                _uiState.value = _uiState.value.copy(
                    totalScore = totalScore,
                    primaryTrait = primaryTrait,
                    hasSubmitted = true,
                    isLoading = false
                )

                onComplete()
            } catch (e: Exception) {
                Log.e("ScreeningViewModel", "Error submitting assessment", e)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun retakeAssessment() {
        _uiState.value = ScreeningUiState(
            previousAssessment = _uiState.value.previousAssessment
        )
    }

    private fun calculatePrimaryTrait(totalScore: Int): String {
        return when {
            totalScore <= 6 -> "Baja Sensibilidad"
            totalScore <= 12 -> "Sensibilidad Moderada"
            totalScore <= 18 -> "Sensibilidad Elevada"
            totalScore <= 24 -> "Alta Sensibilidad"
            else -> "Hipersensibilidad"
        }
    }
}

