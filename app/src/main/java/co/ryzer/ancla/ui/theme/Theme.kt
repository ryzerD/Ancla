package co.ryzer.ancla.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Dark Scheme configuration.
 * Note: Even in Dark Mode, we use desaturated tones to prevent high-contrast eye strain.
 */
private fun darkScheme() = darkColorScheme(
    primary = CardGreen,
    secondary = CardLavender,
    tertiary = CardPeach,
    background = Color(0xFF1A1A1A),
    surface = Color(0xFF242424),
    onBackground = Color.White,
    onSurface = Color.White
)

/**
 * Main Light Scheme based on the "Ancla" visual identity.
 * Focuses on low-stimulation cream backgrounds and pastel tools.
 */
private fun lightScheme() = lightColorScheme(
    primary = CardGreen,
    secondary = CardLavender,
    tertiary = CardPeach,
    background = AnclaBackground,
    surface = SurfaceWhite,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = Color(0xFFE57373),
    onError = Color.White
)

@Composable
fun AnclaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set too false to maintain the "Ancla" brand identity and sensory predictability
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme()
        else -> lightScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}