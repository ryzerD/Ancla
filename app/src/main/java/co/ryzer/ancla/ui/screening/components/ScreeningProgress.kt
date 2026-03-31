package co.ryzer.ancla.ui.screening.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.screening.SCREENING_QUESTIONS_COUNT
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.ScriptReaderButton
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens

@Composable
internal fun ScreeningProgress(currentQuestion: Int) {
    LinearProgressIndicator(
        progress = { (currentQuestion + 1) / SCREENING_QUESTIONS_COUNT.toFloat() },
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp),
        color = ScriptReaderButton,
        trackColor = TextSecondary.copy(alpha = 0.2f),
        strokeCap = StrokeCap.Round
    )

    Text(
        text = stringResource(R.string.screening_progress_format, currentQuestion + 1),
        style = AnclaTextStyles.toolCardSubtitle,
        color = TextSecondary,
        modifier = Modifier.padding(top = ToolsScreenDimens.cardContentPadding)
    )
}

