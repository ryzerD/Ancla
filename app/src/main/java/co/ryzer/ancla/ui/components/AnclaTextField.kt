package co.ryzer.ancla.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.Dp
import co.ryzer.ancla.ui.theme.OnboardingSensorialDimens
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary

@Composable
fun AnclaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    minHeight: Dp? = null,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    ),
    containerAlpha: Float = 0.8f
) {
    val fieldModifier = if (minHeight != null) {
        modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
    } else {
        modifier.fillMaxWidth()
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = fieldModifier,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        supportingText = supportingText,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(OnboardingSensorialDimens.textFieldCornerRadius),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceWhite.copy(alpha = containerAlpha),
            unfocusedContainerColor = SurfaceWhite.copy(alpha = containerAlpha),
            disabledContainerColor = SurfaceWhite.copy(alpha = 0.6f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedLabelColor = TextPrimary,
            unfocusedLabelColor = TextSecondary,
            focusedSupportingTextColor = TextSecondary,
            unfocusedSupportingTextColor = TextSecondary,
            focusedPlaceholderColor = TextSecondary,
            unfocusedPlaceholderColor = TextSecondary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = TextPrimary
        )
    )
}

