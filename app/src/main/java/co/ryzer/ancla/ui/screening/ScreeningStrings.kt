package co.ryzer.ancla.ui.screening

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import co.ryzer.ancla.R

internal const val SCREENING_QUESTIONS_COUNT = 10

@Composable
internal fun screeningQuestions(): List<String> {
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
internal fun screeningResponseOptions(): List<String> {
    return listOf(
        stringResource(R.string.screening_response_option_1),
        stringResource(R.string.screening_response_option_2),
        stringResource(R.string.screening_response_option_3),
        stringResource(R.string.screening_response_option_4)
    )
}

@StringRes
internal fun resultTitleRes(totalScore: Int): Int {
    return when (totalScore) {
        in Int.MIN_VALUE..6 -> R.string.result_title_low
        in 7..18 -> R.string.result_title_medium
        in 19..Int.MAX_VALUE -> R.string.result_title_high
        else -> R.string.result_title_low
    }
}

@StringRes
internal fun resultDescriptionRes(totalScore: Int): Int {
    return when (totalScore) {
        in Int.MIN_VALUE..6 -> R.string.result_desc_low
        in 7..18 -> R.string.result_desc_medium
        in 19..Int.MAX_VALUE -> R.string.result_desc_high
        else -> R.string.result_desc_low
    }
}

