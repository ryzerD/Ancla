package co.ryzer.ancla.ui.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.CommonDimens
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens

@Composable
fun SettingsOrderHeader(
    isExpanded: Boolean,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = if (isExpanded) {
                AnclaTextStyles.toolsTitleExpanded.copy(fontWeight = FontWeight.Bold)
            } else {
                AnclaTextStyles.toolsTitle.copy(fontWeight = FontWeight.Bold)
            },
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
        Text(
            text = description,
            style = AnclaTextStyles.toolCardSubtitle,
            color = TextSecondary
        )
    }
}

@Composable
fun SettingsOrderCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    moveUpContentDescription: String,
    moveDownContentDescription: String,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
        border = BorderStroke(
            width = CommonDimens.spacingSmall / 2,
            color = accentColor.copy(alpha = 0.65f)
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ToolsScreenDimens.cardContentPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ToolsScreenDimens.iconToTextSpacer)
            ) {
                Box(
                    modifier = Modifier
                        .size(ToolsScreenDimens.iconPlaceholderSize)
                        .background(
                            color = accentColor.copy(alpha = 0.25f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TextPrimary
                    )
                }
                Text(
                    text = title,
                    style = AnclaTextStyles.toolCardTitle,
                    color = TextPrimary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(ToolsScreenDimens.orderControlsSpacing)) {
                MoveCircleButton(
                    enabled = canMoveUp,
                    contentDescription = moveUpContentDescription,
                    icon = Icons.Default.KeyboardArrowUp,
                    onClick = onMoveUp
                )
                MoveCircleButton(
                    enabled = canMoveDown,
                    contentDescription = moveDownContentDescription,
                    icon = Icons.Default.KeyboardArrowDown,
                    onClick = onMoveDown
                )
            }
        }
    }
}

@Composable
private fun MoveCircleButton(
    enabled: Boolean,
    contentDescription: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(ToolsScreenDimens.iconPlaceholderSize)
            .background(
                color = if (enabled) {
                    CardGreen.copy(alpha = 0.25f)
                } else {
                    CardLavender.copy(alpha = 0.14f)
                },
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) TextPrimary else TextSecondary
        )
    }
}

