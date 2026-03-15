package co.ryzer.ancla.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.BreathingScreenDimens
import co.ryzer.ancla.ui.theme.TextPrimary
import kotlinx.coroutines.delay

private const val BREATHING_CYCLE_SECONDS = 16f
private const val PHASE_DURATION_SECONDS = 4f
private const val MIN_SCALE = 0.78f
private const val MAX_SCALE = 1.08f
private const val PRE_START_COUNTDOWN_SECONDS = 3
private const val BREATHING_SESSION_SECONDS = 120

private val ColorSageGreen = Color(0xFFD4E4D8)
private val ColorLavender = Color(0xFFE2E2F0)

enum class BreathingPhase {
    INHALE,
    HOLD_LARGE,
    EXHALE,
    HOLD_SMALL
}

@Composable
fun BreathingScreen(
    onExit: () -> Unit
) {
    val context = LocalContext.current
    var preStartRemaining by remember { mutableIntStateOf(PRE_START_COUNTDOWN_SECONDS) }
    var sessionRemaining by remember { mutableIntStateOf(BREATHING_SESSION_SECONDS) }
    var hasSessionStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (preStartRemaining > 0) {
            delay(1_000)
            preStartRemaining -= 1
        }

        hasSessionStarted = true
        while (sessionRemaining > 0) {
            delay(1_000)
            sessionRemaining -= 1
        }
    }

    val cycleProgress = rememberBreathingCycleProgress()
    val phase = if (hasSessionStarted && sessionRemaining > 0) {
        breathingPhaseForProgress(cycleProgress)
    } else {
        BreathingPhase.HOLD_SMALL
    }

    val targetScale = when (phase) {
        BreathingPhase.INHALE,
        BreathingPhase.HOLD_LARGE -> MAX_SCALE

        BreathingPhase.EXHALE,
        BreathingPhase.HOLD_SMALL -> MIN_SCALE
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(
            durationMillis = 4_000,
            easing = FastOutSlowInEasing
        ),
        label = "breathing_scale"
    )

    val scaleFraction = ((animatedScale - MIN_SCALE) / (MAX_SCALE - MIN_SCALE)).coerceIn(0f, 1f)
    val circleColor = lerp(start = ColorSageGreen, stop = ColorLavender, fraction = scaleFraction)

    LaunchedEffect(phase) {
        if (hasSessionStarted && sessionRemaining > 0 &&
            (phase == BreathingPhase.INHALE || phase == BreathingPhase.EXHALE)
        ) {
            triggerSubtleBreathingHaptic(context)
        }
    }

    BreathingSystemBarsEffect()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(BreathingScreenDimens.screenPadding)
    ) {
        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = "Cerrar",
            tint = TextPrimary.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(BreathingScreenDimens.exitIconSize)
                .clickable(onClick = onExit)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = phaseTitle(
                    hasSessionStarted = hasSessionStarted,
                    preStartRemaining = preStartRemaining,
                    sessionRemaining = sessionRemaining,
                    phase = phase
                ),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.size(BreathingScreenDimens.topTextBottomSpacing))
            Text(
                text = remainingLabel(
                    hasSessionStarted = hasSessionStarted,
                    preStartRemaining = preStartRemaining,
                    sessionRemaining = sessionRemaining
                ),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light,
                    color = TextPrimary.copy(alpha = 0.8f)
                )
            )
            Spacer(modifier = Modifier.size(BreathingScreenDimens.topTextBottomSpacing))
            Box(
                modifier = Modifier
                    .size(BreathingScreenDimens.circleSize)
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                    }
                    .clip(CircleShape)
                    .background(circleColor)
            )
        }
    }
}

private fun phaseTitle(
    hasSessionStarted: Boolean,
    preStartRemaining: Int,
    sessionRemaining: Int,
    phase: BreathingPhase
): String {
    if (!hasSessionStarted) {
        return "Empieza en $preStartRemaining"
    }
    if (sessionRemaining == 0) {
        return "Sesion completada"
    }
    return phase.toLabel()
}

private fun remainingLabel(
    hasSessionStarted: Boolean,
    preStartRemaining: Int,
    sessionRemaining: Int
): String {
    return if (!hasSessionStarted) {
        "Inicio en ${preStartRemaining}s"
    } else {
        "Tiempo restante ${formatSecondsAsClock(sessionRemaining)}"
    }
}

private fun formatSecondsAsClock(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Composable
private fun rememberBreathingCycleProgress(): Float {
    val transition = rememberInfiniteTransition(label = "breathing_cycle")
    val cycle by transition.animateFloat(
        initialValue = 0f,
        targetValue = BREATHING_CYCLE_SECONDS,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 16_000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "breathing_cycle_seconds"
    )
    return cycle
}

private fun breathingPhaseForProgress(progress: Float): BreathingPhase {
    return when {
        progress < PHASE_DURATION_SECONDS -> BreathingPhase.INHALE
        progress < PHASE_DURATION_SECONDS * 2 -> BreathingPhase.HOLD_LARGE
        progress < PHASE_DURATION_SECONDS * 3 -> BreathingPhase.EXHALE
        else -> BreathingPhase.HOLD_SMALL
    }
}

private fun BreathingPhase.toLabel(): String {
    return when (this) {
        BreathingPhase.INHALE -> "Inhala"
        BreathingPhase.HOLD_LARGE,
        BreathingPhase.HOLD_SMALL -> "Mantén"

        BreathingPhase.EXHALE -> "Exhala"
    }
}

@RequiresPermission(Manifest.permission.VIBRATE)
private fun triggerSubtleBreathingHaptic(context: Context) {
    if (!hasVibratePermission(context)) return

    val durationMs = 22L

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(VibratorManager::class.java)
        val vibrator = vibratorManager?.defaultVibrator
        try {
            vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, 30))
        } catch (_: SecurityException) {
            return
        }
        return
    }

    @Suppress("DEPRECATION")
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
            vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, 30))
        } catch (_: SecurityException) {
            return
        }
    } else {
        @Suppress("DEPRECATION")
        try {
            vibrator?.vibrate(durationMs)
        } catch (_: SecurityException) {
            return
        }
    }
}

private fun hasVibratePermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.VIBRATE
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
private fun BreathingSystemBarsEffect() {
    val view = LocalView.current
    DisposableEffect(view) {
        val activity = view.context as? Activity
        if (activity == null) {
            onDispose { }
        } else {
            val window = activity.window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            onDispose {
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun BreathingScreenPreview() {
    AnclaTheme {
        BreathingScreen(onExit = {})
    }
}

