package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.CardPeach
import co.ryzer.ancla.ui.theme.CardRose
import co.ryzer.ancla.ui.theme.NewScriptScreenDimens
import co.ryzer.ancla.ui.theme.ScriptReaderButton
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens

private data class ScriptCategoryUi(
    val id: String,
    val label: String,
    val color: Color
)

private data class VisualStyleUi(
    val id: String,
    val contentDescription: String,
    val colors: List<Color>
)

@Composable
fun NewScriptScreen(
    windowSizeClass: WindowSizeClass? = null,
    onSaveScript: (String, String, String) -> Unit = { _, _, _ -> },
    onCloseWithoutSaving: () -> Unit = {}
) {
    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded
    val horizontalPadding = if (isExpanded) {
        ToolsScreenDimens.horizontalPaddingExpanded
    } else {
        ToolsScreenDimens.horizontalPaddingCompact
    }
    val actionWidthFraction = if (isExpanded) 0.35f else 0.52f

    var phrase by rememberSaveable { mutableStateOf("") }
    var selectedCategoryId by rememberSaveable { mutableStateOf("social") }
    var selectedStyleId by rememberSaveable { mutableStateOf("lavender") }

    val categories = listOf(
        ScriptCategoryUi("social", stringResource(R.string.new_script_category_social), CardGreen),
        ScriptCategoryUi("needs", stringResource(R.string.new_script_category_needs), CardLavender),
        ScriptCategoryUi("limits", stringResource(R.string.new_script_category_limits), CardRose),
        ScriptCategoryUi("errands", stringResource(R.string.new_script_category_errands), CardPeach)
    )
    val visualStyles = listOf(
        VisualStyleUi(
            id = "mixed",
            contentDescription = stringResource(R.string.new_script_style_mixed),
            colors = listOf(CardLavender, CardGreen, CardRose, CardPeach)
        ),
        VisualStyleUi(
            id = "lavender",
            contentDescription = stringResource(R.string.new_script_style_lavender),
            colors = listOf(CardLavender)
        ),
        VisualStyleUi(
            id = "rose",
            contentDescription = stringResource(R.string.new_script_style_rose),
            colors = listOf(CardRose)
        ),
        VisualStyleUi(
            id = "sand",
            contentDescription = stringResource(R.string.new_script_style_sand),
            colors = listOf(CardPeach)
        )
    )

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedContainerColor = SurfaceWhite,
        unfocusedContainerColor = SurfaceWhite,
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        cursorColor = TextPrimary,
        focusedPlaceholderColor = TextSecondary,
        unfocusedPlaceholderColor = TextSecondary
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(NewScriptScreenDimens.sectionSpacing),
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(
                horizontal = horizontalPadding,
                vertical = ToolsScreenDimens.verticalPadding
            )
    ) {
        item {
            Text(
                text = stringResource(R.string.new_script_screen_title),
                style = AnclaTextStyles.toolsTitle,
                color = TextPrimary
            )
            Text(
                text = stringResource(R.string.new_script_screen_subtitle),
                style = AnclaTextStyles.toolCardSubtitle,
                color = TextSecondary
            )
        }

        item {
            SectionTitle(text = stringResource(R.string.new_script_section_description))
            Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
            OutlinedTextField(
                value = phrase,
                onValueChange = { phrase = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = NewScriptScreenDimens.textFieldMinHeight),
                textStyle = AnclaTextStyles.taskTitle,
                placeholder = {
                    Text(
                        text = stringResource(R.string.new_script_phrase_placeholder),
                        style = AnclaTextStyles.taskTitle,
                        color = TextSecondary
                    )
                },
                colors = fieldColors,
                shape = RoundedCornerShape(NewScriptScreenDimens.inputCornerRadius),
                minLines = 3,
                maxLines = 4
            )
        }

        item {
            SectionTitle(text = stringResource(R.string.new_script_section_category))
            Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
            CategoryRows(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { selectedCategoryId = it }
            )
        }

        item {
            SectionTitle(text = stringResource(R.string.new_script_section_visual_style))
            Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
            Row(horizontalArrangement = Arrangement.spacedBy(NewScriptScreenDimens.swatchSpacing)) {
                visualStyles.forEach { style ->
                    StyleSwatch(
                        style = style,
                        isSelected = style.id == selectedStyleId,
                        onClick = { selectedStyleId = style.id }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(NewScriptScreenDimens.buttonTopSpacing))
            Button(
                onClick = { onSaveScript(phrase.trim(), selectedCategoryId, selectedStyleId) },
                enabled = phrase.isNotBlank(),
                shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
                colors = ButtonDefaults.buttonColors(containerColor = CardGreen),
                modifier = Modifier.fillMaxWidth(actionWidthFraction)
            ) {
                Text(
                    text = stringResource(R.string.new_script_save_button),
                    style = AnclaTextStyles.primaryButton,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(NewScriptScreenDimens.closeActionTopSpacing))
            TextButton(onClick = onCloseWithoutSaving) {
                Text(
                    text = stringResource(R.string.new_script_close_without_saving),
                    style = AnclaTextStyles.toolCardSubtitle,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = AnclaTextStyles.toolCardTitle,
        color = TextPrimary
    )
}

@Composable
private fun CategoryRows(
    categories: List<ScriptCategoryUi>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(NewScriptScreenDimens.chipSpacing)) {
        categories.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(NewScriptScreenDimens.chipSpacing)) {
                rowItems.forEach { category ->
                    FilterChip(
                        selected = category.id == selectedCategoryId,
                        onClick = { onCategorySelected(category.id) },
                        label = {
                            Text(
                                text = category.label,
                                style = AnclaTextStyles.taskTime,
                                color = TextPrimary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = category.color,
                            containerColor = category.color.copy(alpha = 0.85f),
                            labelColor = TextPrimary,
                            selectedLabelColor = TextPrimary
                        ),
                        border = if (category.id == selectedCategoryId) {
                            BorderStroke(NewScriptScreenDimens.swatchBorderWidth, TextPrimary)
                        } else {
                            null
                        },
                        shape = RoundedCornerShape(NewScriptScreenDimens.chipCornerRadius)
                    )
                }
            }
        }
    }
}

@Composable
private fun StyleSwatch(
    style: VisualStyleUi,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(NewScriptScreenDimens.swatchCornerRadius)
    val borderColor = if (isSelected) ScriptReaderButton else Color.Transparent

    Box(
        modifier = Modifier
            .size(NewScriptScreenDimens.swatchSize)
            .clickable(onClick = onClick)
            .clip(shape)
            .background(styleBackground(style.colors), shape)
            .border(
                width = NewScriptScreenDimens.swatchBorderWidth,
                color = borderColor,
                shape = shape
            )
            .semantics { contentDescription = style.contentDescription }
    )
}

private fun styleBackground(colors: List<Color>): Brush {
    return if (colors.size > 1) {
        Brush.linearGradient(colors)
    } else {
        Brush.linearGradient(listOf(colors.first(), colors.first()))
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun NewScriptScreenPreview() {
    AnclaTheme {
        NewScriptScreen()
    }
}




