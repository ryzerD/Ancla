
package co.ryzer.ancla.ui.screening.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.screening.SCREENING_QUESTIONS_COUNT
import co.ryzer.ancla.ui.theme.ScriptReaderButton
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens

@Composable
internal fun ScreeningNavigationBar(
	currentQuestion: Int,
	onPreviousQuestion: () -> Unit,
	onNextOrSubmit: () -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = ToolsScreenDimens.cardContentPadding),
		horizontalArrangement = Arrangement.spacedBy(ToolsScreenDimens.cardContentPadding)
	) {
		Button(
			onClick = onPreviousQuestion,
			enabled = currentQuestion > 0,
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
			onClick = onNextOrSubmit,
			modifier = Modifier
				.weight(1f)
				.height(44.dp),
			shape = RoundedCornerShape(percent = 50),
			colors = ButtonDefaults.buttonColors(containerColor = ScriptReaderButton)
		) {
			Text(
				text = if (currentQuestion == SCREENING_QUESTIONS_COUNT - 1) {
					stringResource(R.string.screening_button_finish)
				} else {
					stringResource(R.string.screening_button_next)
				},
				color = SurfaceWhite
			)
			if (currentQuestion < SCREENING_QUESTIONS_COUNT - 1) {
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

