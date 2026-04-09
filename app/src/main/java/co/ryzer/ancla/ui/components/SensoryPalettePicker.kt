package co.ryzer.ancla.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.CardPeach
import co.ryzer.ancla.ui.theme.CardRose
import co.ryzer.ancla.ui.theme.OnboardingSensorialDimens
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary

data class SensoryPaletteOption(
    val id: String,
    @param:StringRes val labelRes: Int,
    val color: Color
)

val DefaultSensoryPaletteOptions = listOf(
    SensoryPaletteOption(id = "sage", labelRes = R.string.palette_sage, color = CardGreen),
    SensoryPaletteOption(id = "lavender", labelRes = R.string.palette_lavender, color = CardLavender),
    SensoryPaletteOption(id = "rose", labelRes = R.string.palette_rose, color = CardRose),
    SensoryPaletteOption(id = "peach", labelRes = R.string.palette_peach, color = CardPeach)
)

enum class SensoryPalettePickerStyle {
    Compact,
    LargeCircle
}

@Composable
fun SensoryPalettePicker(
    selectedColorId: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    options: List<SensoryPaletteOption> = DefaultSensoryPaletteOptions,
    style: SensoryPalettePickerStyle = SensoryPalettePickerStyle.Compact
) {
    if (style == SensoryPalettePickerStyle.LargeCircle) {
        LargeCirclePaletteGrid(
            selectedColorId = selectedColorId,
            onColorSelected = onColorSelected,
            modifier = modifier,
            options = options
        )
        return
    }

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
                            contentDescription = stringResource(option.labelRes),
                            tint = TextPrimary,
                            modifier = Modifier.size(OnboardingSensorialDimens.pickerIconSize)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LargeCirclePaletteGrid(
    selectedColorId: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier,
    options: List<SensoryPaletteOption>
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OnboardingSensorialDimens.pickerLargeGridSpacing)
    ) {
        options.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { option ->
                    val isSelected = option.id == selectedColorId
                    Box(
                        modifier = Modifier
                            .size(OnboardingSensorialDimens.pickerLargeItemSize)
                            .clip(CircleShape)
                            .background(option.color)
                            .clickable { onColorSelected(option.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(option.labelRes).uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) SurfaceWhite else TextPrimary.copy(alpha = 0.45f),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = OnboardingSensorialDimens.pickerLargeLabelBottomSpacing)
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = stringResource(option.labelRes),
                                tint = SurfaceWhite,
                                modifier = Modifier.size(OnboardingSensorialDimens.pickerSelectedIconSize)
                            )
                        }
                    }
                }
            }
        }
    }
}