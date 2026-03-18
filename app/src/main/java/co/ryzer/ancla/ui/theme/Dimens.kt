package co.ryzer.ancla.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Dimension tokens for HomeScreen.
 * Compact = phone portrait · Expanded = tablet or wide landscape.
 */
object HomeScreenDimens {

    // ── Layout ──────────────────────────────────────────────────────────────
    val horizontalPaddingCompact  = 24.dp
    val horizontalPaddingExpanded = 80.dp

    val topSpacerCompact  = 48.dp
    val topSpacerExpanded = 64.dp

    val greetingBottomPadding = 16.dp
    val bottomSpacer          = 48.dp

    // ── TaskCard ─────────────────────────────────────────────────────────────
    val cardMaxWidthCompact  = 500.dp
    val cardMaxWidthExpanded = 600.dp
    val cardCornerRadius     = 28.dp
    val cardElevation        = 2.dp
    val cardPaddingCompact   = 32.dp
    val cardPaddingExpanded  = 48.dp

    val taskIconSizeCompact  = 72.dp
    val taskIconSizeExpanded = 96.dp

    val iconToContentSpacerCompact  = 24.dp
    val iconToContentSpacerExpanded = 32.dp

    val taskTitleVerticalPadding = 8.dp
    val descriptionTopSpacer     = 16.dp

    val contentToButtonSpacerCompact  = 40.dp
    val contentToButtonSpacerExpanded = 56.dp

    val buttonMinHeight    = 60.dp
    val buttonCornerRadius = 20.dp

    // ── EmptyTasksState ──────────────────────────────────────────────────────
    val emptyStatePadding        = 32.dp
    val emptyIconSizeCompact     = 80.dp
    val emptyIconSizeExpanded    = 120.dp
    val emptyIconToTitleSpacer   = 24.dp
    val emptyTitleToSubtitleSpacer = 12.dp
}

/**
 * Dimension tokens for ToolsScreen.
 * Compact = phone portrait · Expanded = tablet or wide landscape.
 */
object ToolsScreenDimens {

    // -- Responsive rules --
    val expandedBreakpoint = 600.dp
    val horizontalPaddingCompact = 24.dp
    val horizontalPaddingExpanded = 48.dp
    val verticalPadding = 24.dp
    val headerBottomSpacer = 24.dp

    // -- Grid --
    const val columnsCompact = 2
    const val columnsExpanded = 3
    val gridSpacing = 16.dp

    // -- Tool card --
    const val cardAspectRatio = 0.9f
    val cardCornerRadius = 20.dp
    val cardContentPadding = 16.dp
    val iconPlaceholderSize = 48.dp
    val iconPlaceholderCornerRadius = 12.dp
    const val iconPlaceholderAlpha = 0.05f
    val iconToTextSpacer = 12.dp
    val orderControlsSpacing = 8.dp
}

/**
 * Dimension tokens for ScriptReaderScreen.
 * Compact = phone portrait · Expanded = tablet or wide landscape.
 */
object ScriptReaderScreenDimens {

    // -- Layout --
    val horizontalPadding = 28.dp
    val verticalPadding = 24.dp
    val emergencyTextTopSpacer = 28.dp

    // -- Button --
    val closeButtonCornerRadius = 20.dp

    // -- Typography --
    val mainTextFontSize = 60.sp
    val mainTextLineHeight = 72.sp
    val emergencyTextFontSize = 48.sp
    val emergencyTextLineHeight = 54.sp
    val closeButtonTextFontSize = 20.sp
    val closeButtonTextLineHeight = 28.sp
}

/**
 * Dimension tokens for NewScriptScreen.
 * Compact = phone portrait · Expanded = tablet or wide landscape.
 */
object NewScriptScreenDimens {

    // -- Layout --
    val textFieldMinHeight = 120.dp
    val sectionSpacing = 20.dp
    val chipSpacing = 12.dp
    val swatchSpacing = 12.dp
    val buttonTopSpacing = 28.dp
    val closeActionTopSpacing = 8.dp

    // -- Shapes --
    val inputCornerRadius = 24.dp
    val chipCornerRadius = 18.dp
    val swatchCornerRadius = 18.dp
    val swatchBorderWidth = 3.dp

    // -- Elements --
    val swatchSize = 72.dp
}

/**
 * Dimension tokens for OnboardingSensorialScreen.
 */
object OnboardingSensorialDimens {
    val screenPadding = 24.dp
    val sectionSpacing = 24.dp
    val titleToSubtitleSpacing = 8.dp
    val sectionTitleToFieldSpacing = 10.dp
    val fieldToHintSpacing = 8.dp
    val hintToSectionSpacing = 18.dp

    val textFieldCornerRadius = 12.dp
    val textFieldMinHeight = 58.dp

    val pickerItemSize = 92.dp
    val pickerItemCornerRadius = 16.dp
    val pickerGridSpacing = 12.dp
    val pickerSelectedBorderWidth = 3.dp
    val pickerUnselectedBorderWidth = 1.dp
    val pickerIconSize = 30.dp

    val logoSize = 56.dp
    val logoCornerRadius = 14.dp
    val logoInnerSpacing = 6.dp

    val ctaTopSpacing = 24.dp
    val ctaBottomSpacing = 8.dp
    val ctaMinHeight = 56.dp
}

/**
 * Dimension tokens for BreathingScreen.
 */
object BreathingScreenDimens {
    val screenPadding = 24.dp
    val topTextBottomSpacing = 24.dp
    val phaseTextBottomSpacing = 8.dp
    val phaseTimerBottomSpacing = 12.dp
    val circleSize = 220.dp
    val circleBottomSpacing = 20.dp
    val exitIconSize = 22.dp
}

/**
 * Dimension tokens for SettingsScreen.
 */
object SettingsScreenDimens {
    val horizontalPaddingCompact = 24.dp
    val horizontalPaddingExpanded = 48.dp
    val verticalPadding = 24.dp

    val logoSize = 24.dp
    val logoBottomSpacing = 20.dp
    val titleBottomSpacing = 24.dp

    val menuItemSpacing = 16.dp
    val badgeToCardSpacing = 10.dp
    val badgeSize = 42.dp
    val badgeCornerRadius = 10.dp

    val menuCardCornerRadius = 16.dp
    val menuCardElevation = 2.dp
    val menuCardHorizontalPadding = 16.dp
    val menuCardVerticalPadding = 14.dp
    val menuIconSize = 28.dp
    val iconTextSpacing = 12.dp
    val chevronSize = 30.dp

    val saveButtonTopSpacing = 20.dp
    val saveButtonMinHeight = 60.dp
    val saveButtonCornerRadius = 50.dp
}

/**
 * Dimension tokens for CalmaTotalScreen.
 */
object CalmaTotalScreenDimens {
    val brightnessLevel = 0.08f
    val fadeOutStepCount = 12
}

