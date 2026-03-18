package co.ryzer.ancla.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.BreathingScreenDimens
import co.ryzer.ancla.ui.theme.TextPrimary
import kotlin.math.ceil

private const val BREATHING_CYCLE_SECONDS = 16f
private const val PHASE_DURATION_SECONDS = 4f
private const val MIN_SCALE = 0.78f
private const val MAX_SCALE = 1.08f
private const val DEFAULT_TOTAL_BREATHING_SECONDS = 180

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
    totalBreathingSeconds: Int = DEFAULT_TOTAL_BREATHING_SECONDS,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val cycleProgress = rememberBreathingCycleProgress()
    val phase = breathingPhaseForProgress(cycleProgress)
    var remainingTotalSeconds by remember(totalBreathingSeconds) {
        mutableFloatStateOf(totalBreathingSeconds.toFloat().coerceAtLeast(0f))
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
    val phaseRemainingSeconds = phaseRemainingSeconds(cycleProgress)

    LaunchedEffect(totalBreathingSeconds) {
        remainingTotalSeconds = totalBreathingSeconds.toFloat().coerceAtLeast(0f)
    }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            if (remainingTotalSeconds > 0f) {
                remainingTotalSeconds = (remainingTotalSeconds - 1f).coerceAtLeast(0f)
            }
        }
    }

    LaunchedEffect(phase) {
        if (phase == BreathingPhase.INHALE || phase == BreathingPhase.EXHALE) {
            triggerSubtleBreathingHaptic(context)
        }
    }

    BreathingSystemBarsEffect()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .pointerInput(onExit) {
                detectTapGestures(onLongPress = { onExit() })
            }
            .padding(BreathingScreenDimens.screenPadding)
    ) {
        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = "Cerrar",
            tint = TextPrimary.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(BreathingScreenDimens.exitIconSize)
                .pointerInput(onExit) {
                    detectTapGestures(onTap = { onExit() })
                }
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = phase.toLabel(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.size(BreathingScreenDimens.phaseTextBottomSpacing))
            Text(
                text = stringResource(
                    id = R.string.breathing_phase_remaining,
                    phaseRemainingSeconds
                ),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    color = TextPrimary.copy(alpha = 0.92f)
                )
            )
            Spacer(modifier = Modifier.size(BreathingScreenDimens.phaseTimerBottomSpacing))
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
            Spacer(modifier = Modifier.size(BreathingScreenDimens.circleBottomSpacing))
            Text(
                text = stringResource(
                    id = R.string.breathing_total_remaining,
                    formatAsMinutesSeconds(remainingTotalSeconds)
                ),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    color = TextPrimary.copy(alpha = 0.86f)
                )
            )
        }
    }
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

private fun triggerSubtleBreathingHaptic(context: Context) {
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.VIBRATE
    ) == PackageManager.PERMISSION_GRANTED
    if (!hasPermission) {
        return
    }

    val durationMs = 22L

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(VibratorManager::class.java)
            val vibrator = vibratorManager?.defaultVibrator
            if (vibrator?.hasVibrator() == true) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, 30))
            }
            return
        }

        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibrator?.hasVibrator() != true) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, 30))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    } catch (_: SecurityException) {
        // Safeguard for devices/ROMs that still reject vibration calls.
    }
}

private fun phaseRemainingSeconds(progress: Float): Int {
    val normalizedProgress = progress.coerceIn(0f, BREATHING_CYCLE_SECONDS)
    val currentPhaseElapsed = normalizedProgress % PHASE_DURATION_SECONDS
    return ceil(PHASE_DURATION_SECONDS - currentPhaseElapsed).toInt().coerceIn(1, 4)
}

private fun formatAsMinutesSeconds(totalSeconds: Float): String {
    val safeSeconds = totalSeconds.toInt().coerceAtLeast(0)
    val minutes = safeSeconds / 60
    val seconds = safeSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
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

