package co.ryzer.ancla.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Common dimension tokens used across all screens.
 * These are the single source of truth for consistent spacing throughout the app.
 */
object CommonDimens {
    // ── Responsive Padding ──────────────────────────────────────────────────
    val horizontalPaddingCompact = 24.dp
    val horizontalPaddingExpanded = 48.dp
    val verticalPadding = 24.dp

    // ── Common Spacing ──────────────────────────────────────────────────────
    val spacingSmall = 8.dp     // Small spacing
    val spacingMedium = 12.dp   // Medium spacing
    val spacingBase = 16.dp     // Base spacing unit
    val spacingLarge = 20.dp    // Large spacing
    val spacingXLarge = 24.dp   // Extra large spacing
    val spacingXXLarge = 32.dp  // 2x large spacing

    // ── Common Radii ────────────────────────────────────────────────────────
    val radiusSmall = 10.dp
    val radiusMedium = 12.dp
    val radiusLarge = 16.dp
    val radiusXLarge = 20.dp
    val radiusXXLarge = 28.dp
    val radiusFull = 999.dp

    // ── Common Sizes ────────────────────────────────────────────────────────
    val iconSize = 18.dp
    val iconSizeLarge = 28.dp
    val badgeSize = 42.dp

    // ── Common Elevation ────────────────────────────────────────────────────
    val elevationSmall = 2.dp
    val elevationNone = 0.dp
}

/**
 * Dimension tokens for HomeScreen.
 * Compact = phone portrait · Expanded = tablet or wide landscape.
 */
object HomeScreenDimens {

    // ── Layout ──────────────────────────────────────────────────────────────
    val horizontalPaddingCompact  = CommonDimens.horizontalPaddingCompact
    val horizontalPaddingExpanded = 80.dp  // Home-specific wider padding

    val topSpacerCompact  = CommonDimens.spacingXXLarge
    val topSpacerExpanded = 64.dp

    val periodGreetingBottomPadding = CommonDimens.spacingSmall
    val greetingBottomPadding = CommonDimens.spacingBase
    val bottomSpacer          = CommonDimens.spacingXXLarge

    // ── TaskCard ─────────────────────────────────────────────────────────────
    val cardMaxWidthCompact  = 500.dp
    val cardMaxWidthExpanded = 600.dp
    val cardCornerRadius     = CommonDimens.radiusXXLarge
    val cardElevation        = CommonDimens.elevationSmall
    val cardPaddingCompact   = 32.dp
    val cardPaddingExpanded  = CommonDimens.spacingXXLarge

    val taskIconSizeCompact  = 72.dp
    val taskIconSizeExpanded = 96.dp

    val iconToContentSpacerCompact  = CommonDimens.spacingXXLarge
    val iconToContentSpacerExpanded = CommonDimens.spacingXXLarge

    val taskTitleVerticalPadding = CommonDimens.spacingSmall
    val descriptionTopSpacer     = CommonDimens.spacingBase

    val contentToButtonSpacerCompact  = 40.dp
    val contentToButtonSpacerExpanded = 56.dp

    val buttonMinHeight    = 60.dp
    val buttonCornerRadius = CommonDimens.radiusXLarge

    // ── RestCard ─────────────────────────────────────────────────────────────
    val restCardCornerRadius = 44.dp
    val restCardElevation = CommonDimens.elevationNone
    const val restCardAspectRatioCompact = 1f
    const val restCardAspectRatioExpanded = 1.1f
    val restCardPadding = 30.dp
    val restIconContainerSize = 88.dp
    val restIconSize = 40.dp
    val restIconBottomSpacing = 22.dp
    val restTitleBottomSpacing = 10.dp
    val restSubtitleHorizontalPadding = 10.dp
    val restContentToButtonSpacing = 26.dp
    const val restButtonWidthFraction = 0.82f

    // ── Home state wiring visuals ───────────────────────────────────────────
    const val preparingCardAlpha = 0.78f
    val overlapBannerSpacing = CommonDimens.spacingSmall
    val overlapBannerCornerRadius = CommonDimens.radiusMedium
    val overlapBannerHorizontalPadding = CommonDimens.spacingSmall
    val overlapBannerVerticalPadding = CommonDimens.spacingSmall

}

/**
 * Dimension tokens for ToolsScreen.
 * Compact = phone portrait · Expanded = tablet or wide landscape.
 */
object ToolsScreenDimens {

    // -- Responsive rules (use CommonDimens) --
    val horizontalPaddingCompact = CommonDimens.horizontalPaddingCompact
    val horizontalPaddingExpanded = CommonDimens.horizontalPaddingExpanded
    val verticalPadding = CommonDimens.verticalPadding
    val headerBottomSpacer = CommonDimens.spacingXLarge

    // -- Grid --
    const val columnsCompact = 2
    const val columnsExpanded = 3
    val gridSpacing = CommonDimens.spacingBase

    // -- Tool card --
    const val cardAspectRatio = 0.9f
    val cardCornerRadius = CommonDimens.radiusXLarge
    val cardContentPadding = CommonDimens.spacingBase
    val iconPlaceholderSize = 48.dp
    val iconPlaceholderCornerRadius = CommonDimens.radiusMedium
    const val iconPlaceholderAlpha = 0.05f
    val iconToTextSpacer = CommonDimens.spacingMedium
    val orderControlsSpacing = CommonDimens.spacingSmall
}

/**
 * Dimension tokens for ScreeningResultsScreen.
 */
object ScreeningResultsDimens {
    const val scoreCardWidthFraction = 0.85f
    const val descriptionWidthFraction = 0.95f
    const val disclaimerWidthFraction = 0.92f
    const val actionButtonsWidthFraction = 0.85f

    val resultTitleTopPadding = CommonDimens.spacingBase
    val scoreCardHeight = 70.dp
    val scoreCardContentPadding = CommonDimens.spacingBase
    val descriptionTopPadding = CommonDimens.spacingSmall
    val disclaimerTopPadding = CommonDimens.spacingBase
    val actionsTopPadding = CommonDimens.spacingMedium
    val actionsSpacing = CommonDimens.spacingMedium
    val actionButtonHeight = 48.dp
}

/**
 * Dimension tokens for ScriptReaderScreen.
 * Compact = phone portrait · Expanded = tablet or wide landscape.
 */
object ScriptReaderScreenDimens {

    // -- Layout --
    val horizontalPadding = 28.dp
    val verticalPadding = CommonDimens.verticalPadding
    val emergencyTextTopSpacer = 28.dp

    // -- Button --
    val closeButtonCornerRadius = CommonDimens.radiusXLarge

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
    val sectionSpacing = CommonDimens.spacingLarge
    val chipSpacing = CommonDimens.spacingMedium
    val swatchSpacing = CommonDimens.spacingMedium
    val buttonTopSpacing = 28.dp
    val closeActionTopSpacing = CommonDimens.spacingSmall

    // -- Shapes --
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
    val screenPadding = CommonDimens.horizontalPaddingCompact
    val topPadding = CommonDimens.spacingXXLarge
    val contentBottomPadding = 140.dp
    val sectionSpacing = CommonDimens.horizontalPaddingCompact
    val titleToSubtitleSpacing = CommonDimens.spacingSmall
    val sectionTitleToFieldSpacing = 10.dp
    val sectionBadgeSize = CommonDimens.badgeSize
    val sectionTitleTextSpacing = 14.dp

    val textFieldCornerRadius = CommonDimens.radiusMedium
    val textFieldMinHeight = 58.dp
    val contactCardCornerRadius = CommonDimens.radiusXXLarge
    val contactCardPadding = CommonDimens.spacingLarge
    val contactFieldSpacing = 14.dp

    val pickerItemSize = 92.dp
    val pickerLargeItemSize = 170.dp
    val pickerLargeGridSpacing = CommonDimens.spacingMedium
    val pickerLargeLabelBottomSpacing = CommonDimens.spacingBase
    val pickerItemCornerRadius = CommonDimens.radiusLarge
    val pickerGridSpacing = CommonDimens.spacingMedium
    val pickerSelectedBorderWidth = 3.dp
    val pickerUnselectedBorderWidth = 1.dp
    val pickerIconSize = 30.dp
    val pickerSelectedIconSize = 28.dp
    val ctaMinHeight = 56.dp
    val ctaButtonCornerRadius = CommonDimens.radiusFull
    val ctaButtonHorizontalPadding = CommonDimens.horizontalPaddingCompact
    val ctaButtonVerticalPadding = CommonDimens.horizontalPaddingCompact
}

/**
 * Dimension tokens for BreathingScreen.
 */
object BreathingScreenDimens {
    val screenPadding = CommonDimens.horizontalPaddingCompact
    val topTextBottomSpacing = CommonDimens.horizontalPaddingCompact
    val phaseTextBottomSpacing = CommonDimens.spacingSmall
    val phaseTimerBottomSpacing = CommonDimens.spacingMedium
    val circleSize = 220.dp
    val circleBottomSpacing = CommonDimens.spacingLarge
    val exitIconSize = 22.dp
}

/**
 * Dimension tokens for SettingsScreen.
 */
object SettingsScreenDimens {
    val horizontalPaddingCompact = CommonDimens.horizontalPaddingCompact
    val horizontalPaddingExpanded = CommonDimens.horizontalPaddingExpanded
    val verticalPadding = CommonDimens.verticalPadding

    val titleBottomSpacing = CommonDimens.horizontalPaddingCompact

    val menuItemSpacing = CommonDimens.horizontalPaddingCompact
    val badgeToCardSpacing = 10.dp
    val badgeSize = CommonDimens.badgeSize
    val badgeCornerRadius = CommonDimens.radiusSmall

    val menuCardCornerRadius = CommonDimens.radiusLarge
    val menuCardElevation = CommonDimens.elevationSmall
    val menuCardHorizontalPadding = CommonDimens.spacingBase
    val menuCardVerticalPadding = 14.dp
    val menuIconSize = CommonDimens.iconSizeLarge
    val iconTextSpacing = CommonDimens.spacingMedium
    val chevronSize = 30.dp

}

/**
 * Dimension tokens for CalmaTotalScreen.
 */
object CalmaTotalScreenDimens {
    val brightnessLevel = 0.08f
    val fadeOutStepCount = 12
}

/**
 * Dimension tokens for shared button components.
 */
object AnclaButtonDimens {
    val minHeight = 48.dp
    val iconSize = CommonDimens.iconSize
    val contentSpacing = CommonDimens.spacingSmall
    val pillCornerRadius = CommonDimens.radiusFull
    val outlinedCornerRadius = CommonDimens.radiusLarge
    val borderWidth = 1.dp
}

/**
 * Dimension tokens for AnclaTimePickerField.
 */
object AnclaTimePickerDimens {
    val fieldCornerRadius = CommonDimens.radiusFull
    val fieldHorizontalPadding = CommonDimens.spacingMedium
    val fieldVerticalPadding = 10.dp
    val fieldIconSize = CommonDimens.iconSize

    val dialogCornerRadius = CommonDimens.radiusXXLarge
    val dialogTonalElevation = CommonDimens.elevationNone
    val dialogShadowElevation = CommonDimens.elevationNone
    val dialogMinWidth = 320.dp
    val dialogMaxWidth = 360.dp
    val dialogPadding = CommonDimens.horizontalPaddingCompact
    val titleBottomSpacing = CommonDimens.spacingLarge

    val actionsTopSpacing = CommonDimens.spacingLarge
    val actionsSpacing = 10.dp
    val actionButtonCornerRadius = CommonDimens.radiusSmall
}

