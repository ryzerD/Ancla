package co.ryzer.ancla.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

private data class AnclaPalette(
	val background: Color,
	val cardGreen: Color,
	val cardLavender: Color,
	val cardPeach: Color,
	val cardRose: Color,
	val scriptReaderButton: Color
)

private val LavenderPalette = AnclaPalette(
	background = Color(0xFFFAF0E6),
	cardGreen = Color(0xFFD4E4D8),
	cardLavender = Color(0xFFE2E2F0),
	cardPeach = Color(0xFFF9E6D6),
	cardRose = Color(0xFFF2D7D7),
	scriptReaderButton = Color(0xFFA4C4AE)
)

private val RosePalette = AnclaPalette(
	background = Color(0xFFFCF1F1),
	cardGreen = Color(0xFFE7DEDE),
	cardLavender = Color(0xFFF2D7D7),
	cardPeach = Color(0xFFF7E4DA),
	cardRose = Color(0xFFF1D0D0),
	scriptReaderButton = Color(0xFFD4B0B0)
)

private val SagePalette = AnclaPalette(
	background = Color(0xFFF1F7F2),
	cardGreen = Color(0xFFD4E4D8),
	cardLavender = Color(0xFFDDE7E0),
	cardPeach = Color(0xFFE8E6DA),
	cardRose = Color(0xFFE6DDDD),
	scriptReaderButton = Color(0xFFA3C1AD)
)

private val PeachPalette = AnclaPalette(
	background = Color(0xFFFFF4EC),
	cardGreen = Color(0xFFEADFD1),
	cardLavender = Color(0xFFF2E5D9),
	cardPeach = Color(0xFFF9E6D6),
	cardRose = Color(0xFFF3DED2),
	scriptReaderButton = Color(0xFFD2B79E)
)

private val currentPaletteState = mutableStateOf(LavenderPalette)

fun applySensoryPalette(selectedColorId: String) {
	currentPaletteState.value = when (selectedColorId) {
		"rose" -> RosePalette
		"sage" -> SagePalette
		"peach" -> PeachPalette
		else -> LavenderPalette
	}
}

/**
 * Main Background Color.
 * Comes from the user-selected sensory palette.
 */
val AnclaBackground: Color
	get() = currentPaletteState.value.background

/**
 * Card Component Colors (Muted Palette)
 * These tones are desaturated to avoid sensory overload.
 */
val CardGreen: Color
	get() = currentPaletteState.value.cardGreen
val CardLavender: Color
	get() = currentPaletteState.value.cardLavender
val CardPeach: Color
	get() = currentPaletteState.value.cardPeach
val CardRose: Color
	get() = currentPaletteState.value.cardRose
val ScriptReaderButton: Color
	get() = currentPaletteState.value.scriptReaderButton

/**
 * Typography and Iconography
 * Using dark charcoal instead of pure black to prevent "vibration" effect on bright screens.
 */
val TextPrimary = Color(0xFF2D2D2D)   // High emphasis text
val TextSecondary = Color(0xFF757575) // Medium emphasis / descriptions
val IconActive = Color(0xFF1F1F1F)    // Selected navigation items
val SurfaceWhite = Color(0xFFFFFFFF)  // Inner card surfaces or overlaysar