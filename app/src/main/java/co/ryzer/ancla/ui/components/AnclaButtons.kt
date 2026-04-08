package co.ryzer.ancla.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import co.ryzer.ancla.ui.theme.AnclaButtonDimens
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.ScriptReaderButton
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary

@Composable
fun AnclaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    containerColor: Color = ScriptReaderButton,
    contentColor: Color = SurfaceWhite,
    textStyle: TextStyle = AnclaTextStyles.primaryButton,
    shape: RoundedCornerShape = RoundedCornerShape(AnclaButtonDimens.pillCornerRadius)
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.defaultMinSize(minHeight = AnclaButtonDimens.minHeight),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(AnclaButtonDimens.contentSpacing)) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(AnclaButtonDimens.iconSize)
                )
            }
            Text(
                text = text,
                style = textStyle,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AnclaOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentColor: Color = TextPrimary,
    borderColor: Color = CardLavender,
    textStyle: TextStyle = AnclaTextStyles.primaryButton,
    shape: RoundedCornerShape = RoundedCornerShape(AnclaButtonDimens.outlinedCornerRadius)
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.defaultMinSize(minHeight = AnclaButtonDimens.minHeight),
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
        border = BorderStroke(width = AnclaButtonDimens.borderWidth, color = borderColor)
    ) {
        Text(text = text, style = textStyle)
    }
}

