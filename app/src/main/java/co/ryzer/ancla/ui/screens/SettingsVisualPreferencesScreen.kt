package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.components.AnclaTopBar
import co.ryzer.ancla.ui.components.SensoryPalettePicker
import co.ryzer.ancla.ui.components.SensoryPalettePickerStyle
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.ToolsScreenDimens

@Composable
fun SettingsVisualPreferencesScreen(
    windowSizeClass: WindowSizeClass? = null,
    selectedColorId: String = "lavender",
    hasPendingPaletteChanges: Boolean = false,
    onPalettePreviewChanged: (String) -> Unit = {},
    onSavePalette: () -> Unit = {},
    onDiscardPalettePreview: () -> Unit = {},
    onNavigationClick: () -> Unit = {}
) {
    DisposableEffect(Unit) {
        onDispose { onDiscardPalettePreview() }
    }

    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded
    val horizontalPadding = if (isExpanded) {
        ToolsScreenDimens.horizontalPaddingExpanded
    } else {
        ToolsScreenDimens.horizontalPaddingCompact
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
    ) {
        AnclaTopBar(
            title = stringResource(R.string.settings_palette_preview_title),
            onNavigationClick = onNavigationClick,
            navigationContentDescription = stringResource(R.string.tasks_back_content_description)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = ToolsScreenDimens.verticalPadding),
            verticalArrangement = Arrangement.spacedBy(ToolsScreenDimens.gridSpacing)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.settings_palette_preview_title),
                        style = if (isExpanded) {
                            AnclaTextStyles.toolsTitleExpanded
                        } else {
                            AnclaTextStyles.toolsTitle
                        },
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(ToolsScreenDimens.iconToTextSpacer))
                    Text(
                        text = stringResource(R.string.settings_palette_preview_description),
                        style = AnclaTextStyles.toolCardSubtitle,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(ToolsScreenDimens.gridSpacing))

                    SensoryPalettePicker(
                        selectedColorId = selectedColorId,
                        onColorSelected = onPalettePreviewChanged,
                        style = SensoryPalettePickerStyle.LargeCircle
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.weight(1f))
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (hasPendingPaletteChanges) {
                            stringResource(R.string.settings_palette_unsaved_changes)
                        } else {
                            stringResource(R.string.settings_palette_saved_state)
                        },
                        style = AnclaTextStyles.toolCardSubtitle,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(ToolsScreenDimens.gridSpacing))
                    Button(
                        onClick = onSavePalette,
                        enabled = hasPendingPaletteChanges,
                        shape = RoundedCornerShape(ToolsScreenDimens.cardCornerRadius),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CardGreen,
                            contentColor = TextPrimary
                        )
                    ) {
                        Text(text = stringResource(R.string.settings_palette_save_button))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
private fun SettingsVisualPreferencesScreenPreview() {
    AnclaTheme {
        SettingsVisualPreferencesScreen(hasPendingPaletteChanges = true)
    }
}

