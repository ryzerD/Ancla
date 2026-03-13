package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.ScriptReaderButton
import co.ryzer.ancla.ui.theme.ScriptReaderScreenDimens
import co.ryzer.ancla.ui.theme.TextPrimary

private data class ScriptReaderContent(
    val mainTextResId: Int,
    val emergencyLineResId: Int?
)

@Composable
fun ScriptReaderScreen(
    scriptId: String,
    emergencyContact: String?,
    showEmergencyInfo: Boolean = true,
    onClose: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val content = contentForScript(scriptId = scriptId)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(
                horizontal = ScriptReaderScreenDimens.horizontalPadding,
                vertical = ScriptReaderScreenDimens.verticalPadding
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(content.mainTextResId),
                style = AnclaTextStyles.scriptReaderHeadline,
                color = TextPrimary
            )

            if (showEmergencyInfo && !emergencyContact.isNullOrBlank() && content.emergencyLineResId != null) {
                Spacer(modifier = Modifier.height(ScriptReaderScreenDimens.emergencyTextTopSpacer))
                Text(
                    text = stringResource(content.emergencyLineResId, emergencyContact),
                    style = AnclaTextStyles.scriptReaderEmergency,
                    color = TextPrimary
                )
            }
        }

        Button(
            onClick = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClose()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(ScriptReaderScreenDimens.closeButtonCornerRadius),
            colors = ButtonDefaults.buttonColors(containerColor = ScriptReaderButton)
        ) {
            Text(
                text = stringResource(R.string.scripts_reader_close),
                color = TextPrimary,
                style = AnclaTextStyles.scriptReaderCloseButton
            )
        }
    }
}

private fun contentForScript(scriptId: String): ScriptReaderContent {
    return when (scriptId) {
        "ask_help" -> ScriptReaderContent(
            mainTextResId = R.string.scripts_reader_ask_help_main,
            emergencyLineResId = R.string.scripts_reader_ask_help_emergency
        )

        "noise" -> ScriptReaderContent(
            mainTextResId = R.string.scripts_reader_noise_main,
            emergencyLineResId = null
        )

        "shopping" -> ScriptReaderContent(
            mainTextResId = R.string.scripts_reader_shopping_main,
            emergencyLineResId = null
        )

        "cannot_talk" -> ScriptReaderContent(
            mainTextResId = R.string.scripts_reader_cannot_talk_main,
            emergencyLineResId = R.string.scripts_reader_cannot_talk_emergency
        )

        else -> ScriptReaderContent(
            mainTextResId = R.string.scripts_reader_default_main,
            emergencyLineResId = null
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ScriptReaderScreenPreview() {
    AnclaTheme {
        ScriptReaderScreen(
            scriptId = "ask_help",
            emergencyContact = "123-456-789",
            onClose = {}
        )
    }
}

