package co.ryzer.ancla.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

// TODO: Replace with a custom font that reinforces Ancla's visual identity
private val AnclaFontFamily = FontFamily.Default

/**
 * Material3 typography scale for [AnclaTheme].
 * Defines all variants used in the app so Material components
 * also receive the correct values.
 */
val Typography = Typography(
    displaySmall = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
)

/**
 * Semantic text styles for Ancla.
 *
 * Use these directly in composables instead of MaterialTheme.typography.
 * Each name describes the text role in the UI, not its size.
 *
 * Compact = phone portrait · Expanded = tablet or wide landscape.
 */
object AnclaTextStyles {

    /** Main greeting in compact layout */
    val greeting = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp
    )

    /** Main greeting in expanded layout */
    val greetingExpanded = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp
    )

    /** Section label or secondary category */
    val sectionLabel = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )

    /** Task title in compact layout */
    val taskTitle = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    )

    /** Task title in expanded layout */
    val taskTitleExpanded = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    )

    /** Scheduled task time */
    val taskTime = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

    /** Optional task description */
    val taskDescription = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    /** Primary action button label */
    val primaryButton = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

    /** Empty-state title in compact layout */
    val emptyStateTitle = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp
    )

    /** Empty-state title in expanded layout */
    val emptyStateTitleExpanded = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.sp
    )

    /** Supporting text for the empty state */
    val emptyStateSubtitle = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

    /** Section label for the tools hub */
    val toolsSupportLabel = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )

    /** Main title for the tools hub in compact layout */
    val toolsTitle = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp
    )

    /** Main title for the tools hub in expanded layout */
    val toolsTitleExpanded = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp
    )

    /** Tool card title */
    val toolCardTitle = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

    /** Tool card subtitle */
    val toolCardSubtitle = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    /** Giant headline used in assisted reading mode */
    val scriptReaderHeadline = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = ScriptReaderScreenDimens.mainTextFontSize,
        lineHeight = ScriptReaderScreenDimens.mainTextLineHeight,
        textAlign = TextAlign.Center
    )

    /** Emergency/supporting message used in assisted reading mode */
    val scriptReaderEmergency = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = ScriptReaderScreenDimens.emergencyTextFontSize,
        lineHeight = ScriptReaderScreenDimens.emergencyTextLineHeight,
        textAlign = TextAlign.Center
    )

    /** Close button label used in assisted reading mode */
    val scriptReaderCloseButton = TextStyle(
        fontFamily = AnclaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = ScriptReaderScreenDimens.closeButtonTextFontSize,
        lineHeight = ScriptReaderScreenDimens.closeButtonTextLineHeight,
        textAlign = TextAlign.Center
    )
}
