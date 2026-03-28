package co.ryzer.ancla.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTimePickerDimens
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.CardPeach
import co.ryzer.ancla.ui.theme.ScriptReaderButton
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

private const val STORAGE_TIME_PATTERN = "HH:mm"
private const val SINGLE_DIGIT_STORAGE_PATTERN = "H:mm"
private const val DISPLAY_TIME_PATTERN = "hh:mm a"
private const val DISPLAY_TIME_PATTERN_ALT = "h:mm a"

private val defaultInitialTime: LocalTime = LocalTime.of(8, 0)
private val storageTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(STORAGE_TIME_PATTERN)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnclaTimePickerField(
    value: String,
    timeLabel: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    is24Hour: Boolean = false,
    fieldContainerColor: Color = CardPeach,
    dialogContainerColor: Color = AnclaBackground,
    pickerColors: TimePickerColors? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val locale = remember(configuration) {
        configuration.locales.get(0) ?: Locale.getDefault()
    }
    val initialTime = remember(value, locale) { parseTime(value, locale) ?: defaultInitialTime }
    val resolvedPickerColors = pickerColors ?: defaultAnclaTimePickerColors()

    val pickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = is24Hour
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(AnclaTimePickerDimens.fieldCornerRadius))
            .clickable { showDialog = true },
        shape = RoundedCornerShape(AnclaTimePickerDimens.fieldCornerRadius),
        color = fieldContainerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AnclaTimePickerDimens.fieldHorizontalPadding,
                    vertical = AnclaTimePickerDimens.fieldVerticalPadding
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = stringResource(R.string.tasks_time_picker_description),
                tint = TextSecondary,
                modifier = Modifier.size(AnclaTimePickerDimens.fieldIconSize)
            )
            Text(
                text = formatForDisplay(value, locale),
                style = AnclaTextStyles.taskTime,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(AnclaTimePickerDimens.dialogCornerRadius),
                color = dialogContainerColor,
                tonalElevation = AnclaTimePickerDimens.dialogTonalElevation,
                shadowElevation = AnclaTimePickerDimens.dialogShadowElevation,
                modifier = Modifier.widthIn(
                    min = AnclaTimePickerDimens.dialogMinWidth,
                    max = AnclaTimePickerDimens.dialogMaxWidth
                )
            ) {
                Column(
                    modifier = Modifier.padding(AnclaTimePickerDimens.dialogPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.tasks_time_picker_title, timeLabel),
                        style = AnclaTextStyles.sectionLabel,
                        color = TextPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = AnclaTimePickerDimens.titleBottomSpacing)
                    )

                    TimePicker(
                        state = pickerState,
                        colors = resolvedPickerColors
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = AnclaTimePickerDimens.actionsTopSpacing),
                        horizontalArrangement = Arrangement.End
                    ) {
                        AnclaOutlinedButton(
                            text = stringResource(R.string.dialog_cancel),
                            onClick = { showDialog = false },
                            textStyle = MaterialTheme.typography.labelLarge,
                            shape = RoundedCornerShape(AnclaTimePickerDimens.actionButtonCornerRadius)
                        )
                        Spacer(modifier = Modifier.width(AnclaTimePickerDimens.actionsSpacing))
                        AnclaPrimaryButton(
                            text = stringResource(R.string.dialog_confirm),
                            onClick = {
                                val selectedTime = LocalTime.of(
                                    pickerState.hour,
                                    pickerState.minute
                                )
                                onValueChange(selectedTime.format(storageTimeFormatter))
                                showDialog = false
                            },
                            textStyle = MaterialTheme.typography.labelLarge,
                            shape = RoundedCornerShape(AnclaTimePickerDimens.actionButtonCornerRadius)
                        )
                    }
                }
            }
        }
    }
}

private fun formatForDisplay(rawValue: String, locale: Locale): String {
    val parsed = parseTime(rawValue, locale) ?: return rawValue
    val formatter = DateTimeFormatter.ofPattern(DISPLAY_TIME_PATTERN, locale)
    return parsed.format(formatter)
}

private fun parseTime(rawValue: String, locale: Locale): LocalTime? {
    val normalized = rawValue.trim()
    if (normalized.isEmpty()) return null

    val formatters = listOf(
        storageTimeFormatter,
        DateTimeFormatter.ofPattern(SINGLE_DIGIT_STORAGE_PATTERN),
        DateTimeFormatter.ofPattern(DISPLAY_TIME_PATTERN, locale),
        DateTimeFormatter.ofPattern(DISPLAY_TIME_PATTERN_ALT, locale)
    )

    formatters.forEach { formatter ->
        try {
            return LocalTime.parse(normalized, formatter)
        } catch (_: DateTimeParseException) {
            // Try next formatter.
        }
    }

    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun defaultAnclaTimePickerColors(): TimePickerColors {
    return TimePickerDefaults.colors(
        clockDialColor = CardPeach,
        clockDialSelectedContentColor = SurfaceWhite,
        clockDialUnselectedContentColor = TextSecondary,
        selectorColor = ScriptReaderButton,
        periodSelectorSelectedContainerColor = ScriptReaderButton,
        periodSelectorSelectedContentColor = SurfaceWhite,
        periodSelectorUnselectedContainerColor = CardPeach,
        periodSelectorUnselectedContentColor = TextPrimary,
        timeSelectorSelectedContainerColor = ScriptReaderButton,
        timeSelectorSelectedContentColor = SurfaceWhite,
        timeSelectorUnselectedContainerColor = CardPeach,
        timeSelectorUnselectedContentColor = TextPrimary
    )
}


