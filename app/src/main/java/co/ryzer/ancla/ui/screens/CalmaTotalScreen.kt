package co.ryzer.ancla.ui.screens

import android.app.Activity
import android.media.MediaPlayer
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.VideoView
import androidx.annotation.RawRes
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
    @RawRes mediaResId: Int = R.raw.song1
) {
    val context = LocalContext.current
    val isInspectionMode = LocalInspectionMode.current
    val coroutineScope = rememberCoroutineScope()
    val latestOnExit by rememberUpdatedState(onExit)

    var remainingMillis by remember(sessionDurationMillis) {
        mutableLongStateOf(
            sessionDurationMillis
        )
    }
    var isExiting by remember { mutableStateOf(false) }
    var longPressExitJob by remember { mutableStateOf<Job?>(null) }
    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    fun requestExit() {
        if (isExiting) return
        isExiting = true
        coroutineScope.launch {
            mediaPlayerRef?.let {
                fadeOutAndStopMediaPlayer(
                    mediaPlayer = it,
                    totalDurationMillis = fadeOutDurationMillis,
                    stepCount = CalmaTotalScreenDimens.fadeOutStepCount
                )
            } ?: videoViewRef?.stopPlayback()
            latestOnExit()
        }
    }

    CalmaTotalSystemEffect(
        brightnessLevel = CalmaTotalScreenDimens.brightnessLevel
    )

    DisposableEffect(Unit) {
        onDispose {
            longPressExitJob?.cancel()
            videoViewRef?.stopPlayback()
            mediaPlayerRef?.release()
            mediaPlayerRef = null
            videoViewRef = null
        }
    }

    LaunchedEffect(sessionDurationMillis, isExiting) {
        if (isExiting) return@LaunchedEffect
        val endTime = System.currentTimeMillis() + sessionDurationMillis
        while (!isExiting) {
            val updated = max(0L, endTime - System.currentTimeMillis())
            remainingMillis = updated
            if (updated == 0L) break
            delay(minOf(1_000L, updated))
        }
        if (!isExiting) requestExit()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(CALMA_TOTAL_TEST_TAG)
            .semantics { stateDescription = "remaining_millis:$remainingMillis" }
    ) {
        if (isInspectionMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CalmaTotalBackground)
            )
        } else {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    VideoView(it).apply {
                        videoViewRef = this
                        val uri =
                            "android.resource://${context.packageName}/$mediaResId".toUri()
                        setVideoURI(uri)
                        setOnPreparedListener { mp ->
                            mediaPlayerRef = mp
                            mp.isLooping = true
                            mp.setVolume(INITIAL_PLAYER_VOLUME, INITIAL_PLAYER_VOLUME)
                            start()
                        }
                    }
                },
                update = { view ->
                    if (!view.isPlaying && !isExiting) view.start()
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { motionEvent ->
                    if (isExiting) return@pointerInteropFilter true
                    when (motionEvent.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            longPressExitJob?.cancel()
                            longPressExitJob = coroutineScope.launch {
                                delay(longPressDurationMillis)
                                requestExit()
                            }
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
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
            updateWindowBrightness(window, brightnessLevel)

            onDispose {
                view.keepScreenOn = false
                controller.show(WindowInsetsCompat.Type.systemBars())
                updateWindowBrightness(window, initialBrightness)
            }
        }
    }
}

private suspend fun fadeOutAndStopMediaPlayer(
    mediaPlayer: MediaPlayer,
    totalDurationMillis: Long,
    stepCount: Int
) {
    val safeSteps = max(stepCount, 1)
    val delayMs = max(1L, totalDurationMillis / safeSteps.toLong())

    repeat(safeSteps) { index ->
        val frac = 1f - ((index + 1).toFloat() / safeSteps.toFloat())
        val v = (INITIAL_PLAYER_VOLUME * frac).coerceIn(0f, INITIAL_PLAYER_VOLUME)
        mediaPlayer.setVolume(v, v)
        delay(delayMs)
    }

    mediaPlayer.setVolume(0f, 0f)
    mediaPlayer.stop()
}

private fun updateWindowBrightness(window: android.view.Window, brightness: Float) {
    val params = WindowManager.LayoutParams().apply {
        copyFrom(window.attributes)
        screenBrightness = brightness
    }
    window.attributes = params
}

@Preview(showBackground = true)
@Composable
private fun CalmaTotalScreenPreview() {
    AnclaTheme {
        CalmaTotalScreen(onExit = {})
    }
}