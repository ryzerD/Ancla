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
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.ScriptReaderButton
import co.ryzer.ancla.ui.theme.ScriptReaderScreenDimens
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary

@Composable
fun ScriptReaderScreen(
    mainText: String,
    emergencyContact: String?,
    showEmergencyInfo: Boolean,
    onClose: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite)
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
                text = mainText,
                style = AnclaTextStyles.scriptReaderHeadline,
                color = TextPrimary
            )

            if (showEmergencyInfo && !emergencyContact.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(ScriptReaderScreenDimens.emergencyTextTopSpacer))
                Text(
                    text = stringResource(R.string.scripts_reader_contact_line, emergencyContact),
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

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ScriptReaderScreenPreview() {
    AnclaTheme {
        ScriptReaderScreen(
            mainText = "NECESITO AYUDA",
            emergencyContact = "123-456-789",
            showEmergencyInfo = true,
            onClose = {}
        )
    }
}

