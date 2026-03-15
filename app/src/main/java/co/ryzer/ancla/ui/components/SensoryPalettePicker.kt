package co.ryzer.ancla.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import co.ryzer.ancla.ui.theme.OnboardingSensorialDimens
import co.ryzer.ancla.ui.theme.TextPrimary

data class SensoryPaletteOption(
    val id: String,
    val label: String,
    val color: Color
)

val DefaultSensoryPaletteOptions = listOf(
    SensoryPaletteOption(id = "lavender", label = "Lavanda", color = Color(0xFFE2E2F0)),
    SensoryPaletteOption(id = "rose", label = "Rosa", color = Color(0xFFF2D7D7)),
    SensoryPaletteOption(id = "sage", label = "Salvia", color = Color(0xFFD4E4D8)),
    SensoryPaletteOption(id = "peach", label = "Durazno", color = Color(0xFFF9E6D6))
)

@Composable
fun SensoryPalettePicker(
    selectedColorId: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    options: List<SensoryPaletteOption> = DefaultSensoryPaletteOptions
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OnboardingSensorialDimens.pickerGridSpacing)
    ) {
        options.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(OnboardingSensorialDimens.pickerGridSpacing)
            ) {
                rowItems.forEach { option ->
                    val isSelected = option.id == selectedColorId
                    Box(
                        modifier = Modifier
                            .size(OnboardingSensorialDimens.pickerItemSize)
                            .clip(RoundedCornerShape(OnboardingSensorialDimens.pickerItemCornerRadius))
                            .background(option.color)
                            .border(
                                width = if (isSelected) {
                                    OnboardingSensorialDimens.pickerSelectedBorderWidth
                                } else {
                                    OnboardingSensorialDimens.pickerUnselectedBorderWidth
                                },
                                color = if (isSelected) TextPrimary else Color.Transparent,
                                shape = RoundedCornerShape(OnboardingSensorialDimens.pickerItemCornerRadius)
                            )
                            .clickable { onColorSelected(option.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ChatBubble,
                            contentDescription = option.label,
                            tint = TextPrimary,
                            modifier = Modifier.size(OnboardingSensorialDimens.pickerIconSize)
                        )
                    }
                }
            }
        }
    }
}

