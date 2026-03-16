package co.ryzer.ancla.ui.screens

import android.app.Activity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.tooling.preview.Preview
import androidx.annotation.RawRes
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import co.ryzer.ancla.R
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.CalmaTotalBackground
import co.ryzer.ancla.ui.theme.CalmaTotalScreenDimens
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

private const val DEFAULT_SESSION_DURATION_MILLIS = 5 * 60 * 1_000L
private const val DEFAULT_LONG_PRESS_DURATION_MILLIS = 3_000L
private const val DEFAULT_FADE_OUT_DURATION_MILLIS = 900L
private const val CALMA_TOTAL_TEST_TAG = "calma_total_root"
private const val INITIAL_PLAYER_VOLUME = 0.42f

@Composable
fun CalmaTotalScreen(
    onExit: () -> Unit,
    sessionDurationMillis: Long = DEFAULT_SESSION_DURATION_MILLIS,
    longPressDurationMillis: Long = DEFAULT_LONG_PRESS_DURATION_MILLIS,
    fadeOutDurationMillis: Long = DEFAULT_FADE_OUT_DURATION_MILLIS,
    @RawRes mediaResId: Int = R.raw.resonant_rumble
) {
    val coroutineScope = rememberCoroutineScope()
    val latestOnExit by rememberUpdatedState(onExit)
    val player = rememberCalmaTotalPlayer(mediaResId = mediaResId)
    var remainingMillis by remember(sessionDurationMillis) {
        mutableLongStateOf(sessionDurationMillis)
    }
    var isExiting by remember { mutableStateOf(false) }
    var longPressExitJob by remember { mutableStateOf<Job?>(null) }

    fun requestExit() {
        if (isExiting) return
        isExiting = true
        coroutineScope.launch {
            player?.let {
                fadeOutAndStopPlayer(
                    player = it,
                    totalDurationMillis = fadeOutDurationMillis,
                    stepCount = CalmaTotalScreenDimens.fadeOutStepCount
                )
            }
            latestOnExit()
        }
    }

    CalmaTotalSystemEffect(
        brightnessLevel = CalmaTotalScreenDimens.brightnessLevel
    )

    DisposableEffect(player) {
        onDispose {
            longPressExitJob?.cancel()
            player?.stop()
            player?.release()
        }
    }

    LaunchedEffect(sessionDurationMillis, isExiting) {
        if (isExiting) return@LaunchedEffect

        val startTime = System.currentTimeMillis()
        val endTime = startTime + sessionDurationMillis
        while (!isExiting) {
            val updatedRemaining = max(0L, endTime - System.currentTimeMillis())
            remainingMillis = updatedRemaining
            if (updatedRemaining == 0L) break
            delay(minOf(1_000L, updatedRemaining))
        }

        if (!isExiting) {
            requestExit()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(CALMA_TOTAL_TEST_TAG)
            .semantics {
                stateDescription = "remaining_millis:$remainingMillis"
            }
    ) {
        CalmaTotalVideoLayer(
            player = player,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInteropFilter { motionEvent ->
                if (isExiting) {
                    return@pointerInteropFilter true
                }

                when (motionEvent.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        longPressExitJob?.cancel()
                        longPressExitJob = coroutineScope.launch {
                            delay(longPressDurationMillis)
                            requestExit()
                        }
                    }

                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {
                        longPressExitJob?.cancel()
                        longPressExitJob = null
                    }
                }

                true
            }
        )
    }
}

@Composable
@OptIn(UnstableApi::class)
private fun rememberCalmaTotalPlayer(
    @RawRes mediaResId: Int
): ExoPlayer? {
    val context = LocalContext.current
    val isInspectionMode = LocalInspectionMode.current
    return remember(context, isInspectionMode, mediaResId) {
        if (isInspectionMode) return@remember null

        ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            setMediaItem(
                MediaItem.fromUri(
                    "android.resource://${context.packageName}/$mediaResId".toUri()
                )
            )
            repeatMode = Player.REPEAT_MODE_ONE
            volume = INITIAL_PLAYER_VOLUME
            playWhenReady = true
            prepare()
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun CalmaTotalVideoLayer(
    player: ExoPlayer?,
    modifier: Modifier = Modifier
) {
    if (player == null) {
        Box(modifier = modifier.background(CalmaTotalBackground))
        return
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            PlayerView(context).apply {
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                setKeepContentOnPlayerReset(true)
                this.player = player
            }
        },
        update = { view ->
            view.player = player
        }
    )
}

@Composable
private fun CalmaTotalSystemEffect(
    brightnessLevel: Float
) {
    val view = LocalView.current

    DisposableEffect(view, brightnessLevel) {
        val activity = view.context as? Activity
        if (activity == null) {
            onDispose { }
        } else {
            val window = activity.window
            val initialBrightness = window.attributes.screenBrightness
            val controller = WindowCompat.getInsetsController(window, view)

            view.keepScreenOn = true
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            updateWindowBrightness(window = window, brightness = brightnessLevel)

            onDispose {
                view.keepScreenOn = false
                controller.show(WindowInsetsCompat.Type.systemBars())
                updateWindowBrightness(window = window, brightness = initialBrightness)
            }
        }
    }
}

private suspend fun fadeOutAndStopPlayer(
    player: ExoPlayer,
    totalDurationMillis: Long,
    stepCount: Int
) {
    val safeSteps = max(stepCount, 1)
    val startVolume = player.volume
    val stepDelayMillis = max(1L, (totalDurationMillis / safeSteps.toLong()))

    repeat(safeSteps) { stepIndex ->
        val remainingFraction = 1f - ((stepIndex + 1).toFloat() / safeSteps.toFloat())
        player.volume = startVolume * remainingFraction.coerceIn(0f, 1f)
        delay(stepDelayMillis)
    }

    player.volume = 0f
    player.stop()
}

private fun updateWindowBrightness(
    window: android.view.Window,
    brightness: Float
) {
    val params = WindowManager.LayoutParams().apply {
        copyFrom(window.attributes)
        screenBrightness = brightness
    }
    window.attributes = params
}

@Preview(showBackground = true)
@Composable
fun CalmaTotalScreenPreview() {
    AnclaTheme {
        CalmaTotalScreen(onExit = {})
    }
}
