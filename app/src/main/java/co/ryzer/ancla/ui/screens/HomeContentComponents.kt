package co.ryzer.ancla.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.ryzer.ancla.R
import co.ryzer.ancla.data.Task
import co.ryzer.ancla.ui.home.ActivityState
import co.ryzer.ancla.ui.theme.AnclaTextStyles
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.CardPeach
import co.ryzer.ancla.ui.theme.CardRose
import co.ryzer.ancla.ui.theme.HomeScreenDimens
import co.ryzer.ancla.ui.theme.ScriptReaderButton
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import java.time.LocalTime
import kotlinx.coroutines.delay

@Composable
fun HomeGreetingSection(userName: String, isExpanded: Boolean) {
    val greetingPeriodRes = when (LocalTime.now().hour) {
        in 5..11 -> R.string.greeting_morning
        in 12..18 -> R.string.greeting_afternoon
        else -> R.string.greeting_evening
    }

    Text(
        text = stringResource(greetingPeriodRes),
        style = AnclaTextStyles.sectionLabel,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = HomeScreenDimens.periodGreetingBottomPadding)
    )

    Text(
        text = if (userName.isBlank()) stringResource(R.string.greeting) else stringResource(
            R.string.greeting_user,
            userName
        ),
        style = if (isExpanded) AnclaTextStyles.greetingExpanded else AnclaTextStyles.greeting,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = HomeScreenDimens.greetingBottomPadding)
    )
}

@Composable
fun HomeQuickControlsSection(
    isRecoveryMode: Boolean,
    onToggleRecoveryMode: () -> Unit,
    onPostponeRemaining: (Long) -> Unit
) {
    Surface(
        color = SurfaceWhite,
        shape = RoundedCornerShape(HomeScreenDimens.overlapBannerCornerRadius),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(HomeScreenDimens.overlapBannerHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.home_quick_controls_title),
                style = AnclaTextStyles.sectionLabel,
                color = TextSecondary
            )

            OutlinedButton(
                onClick = onToggleRecoveryMode,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isRecoveryMode) {
                        stringResource(R.string.home_btn_disable_recovery)
                    } else {
                        stringResource(R.string.home_btn_enable_recovery)
                    }
                )
            }

            OutlinedButton(
                onClick = { onPostponeRemaining(15L) },
                enabled = !isRecoveryMode,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.home_btn_postpone_15))
            }
        }
    }
}

@Composable
fun HomeMainContentSection(
    currentActivity: Task?,
    activityState: ActivityState,
    hasOverlap: Boolean,
    isRecoveryMode: Boolean,
    isExpanded: Boolean,
    onTaskComplete: (String) -> Unit,
    onStartMeditation: () -> Unit
) {
    when {
        isRecoveryMode || activityState == ActivityState.RECOVERY_MODE -> {
            RestCard(
                isExpanded = isExpanded,
                onStartMeditation = onStartMeditation,
                title = stringResource(R.string.home_recovery_title),
                subtitle = stringResource(R.string.home_recovery_subtitle),
                cta = stringResource(R.string.home_recovery_cta)
            )
        }

        currentActivity != null -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(HomeScreenDimens.overlapBannerSpacing)
            ) {
                if (
                    activityState == ActivityState.PREPARING ||
                    activityState == ActivityState.ACTIVE ||
                    currentActivity.isInProgress
                ) {
                    ActivityStateBanner(
                        activityState = if (currentActivity.isInProgress) {
                            ActivityState.ACTIVE
                        } else {
                            activityState
                        }
                    )
                }

                if (hasOverlap || activityState == ActivityState.OVERLAPPING) {
                    OverlapBanner()
                }

                val cardAlpha = if (
                    activityState == ActivityState.PREPARING && !currentActivity.isInProgress
                ) {
                    HomeScreenDimens.preparingCardAlpha
                } else {
                    1f
                }

                Box(modifier = Modifier.alpha(cardAlpha)) {
                    ActivityCard(
                        task = currentActivity,
                        onComplete = onTaskComplete,
                        isExpanded = isExpanded
                    )
                }
            }
        }

        else -> {
            RestCard(
                isExpanded = isExpanded,
                onStartMeditation = onStartMeditation,
                title = stringResource(R.string.home_rest_title),
                subtitle = stringResource(R.string.home_rest_subtitle),
                cta = stringResource(R.string.home_rest_cta)
            )
        }
    }
}

@Composable
private fun ActivityStateBanner(activityState: ActivityState) {
    val (text, color) = when (activityState) {
        ActivityState.PREPARING -> stringResource(R.string.home_state_preparing) to CardPeach
        ActivityState.ACTIVE -> stringResource(R.string.home_state_active) to CardGreen
        else -> return
    }

    Surface(
        color = color.copy(alpha = 0.65f),
        shape = RoundedCornerShape(HomeScreenDimens.overlapBannerCornerRadius)
    ) {
        Text(
            text = text,
            style = AnclaTextStyles.taskDescription,
            color = TextPrimary,
            modifier = Modifier.padding(
                horizontal = HomeScreenDimens.overlapBannerHorizontalPadding,
                vertical = HomeScreenDimens.overlapBannerVerticalPadding
            )
        )
    }
}

@Composable
private fun OverlapBanner() {
    Surface(
        color = CardRose.copy(alpha = 0.55f),
        shape = RoundedCornerShape(HomeScreenDimens.overlapBannerCornerRadius)
    ) {
        Text(
            text = stringResource(R.string.home_overlap_banner),
            style = AnclaTextStyles.taskDescription,
            color = TextPrimary,
            modifier = Modifier.padding(
                horizontal = HomeScreenDimens.overlapBannerHorizontalPadding,
                vertical = HomeScreenDimens.overlapBannerVerticalPadding
            )
        )
    }
}

@Composable
fun ActivityCard(
    task: Task,
    onComplete: (String) -> Unit,
    isExpanded: Boolean = false
) {
    val isPressed = remember { mutableStateOf(false) }
    val buttonScale = animateFloatAsState(
        targetValue = if (isPressed.value) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "buttonScale"
    )

    LaunchedEffect(isPressed.value) {
        if (isPressed.value) {
            delay(100)
            isPressed.value = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(
                max = if (isExpanded) {
                    HomeScreenDimens.cardMaxWidthExpanded
                } else {
                    HomeScreenDimens.cardMaxWidthCompact
                }
            ),
        shape = RoundedCornerShape(HomeScreenDimens.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = HomeScreenDimens.cardElevation)
    ) {
        Column(
            modifier = Modifier.padding(
                if (isExpanded) {
                    HomeScreenDimens.cardPaddingExpanded
                } else {
                    HomeScreenDimens.cardPaddingCompact
                }
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (task.isInProgress) Icons.Default.CheckCircle else Icons.Default.Schedule,
                contentDescription = null,
                tint = if (task.isInProgress) ScriptReaderButton else TextSecondary,
                modifier = Modifier.size(
                    if (isExpanded) {
                        HomeScreenDimens.taskIconSizeExpanded
                    } else {
                        HomeScreenDimens.taskIconSizeCompact
                    }
                )
            )
            Spacer(
                modifier = Modifier.height(
                    if (isExpanded) {
                        HomeScreenDimens.iconToContentSpacerExpanded
                    } else {
                        HomeScreenDimens.iconToContentSpacerCompact
                    }
                )
            )
            Text(
                text = task.category.uppercase(),
                style = AnclaTextStyles.sectionLabel,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Text(
                text = task.title,
                style = if (isExpanded) AnclaTextStyles.taskTitleExpanded else AnclaTextStyles.taskTitle,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = HomeScreenDimens.taskTitleVerticalPadding)
            )
            Text(
                text = "${task.startTime} - ${task.endTime}",
                style = AnclaTextStyles.taskTime,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(HomeScreenDimens.descriptionTopSpacer))
                Text(
                    text = task.description,
                    style = AnclaTextStyles.taskDescription,
                    color = TextSecondary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            if (task.isInProgress) {
                Spacer(modifier = Modifier.height(HomeScreenDimens.descriptionTopSpacer))
                Text(
                    text = stringResource(R.string.home_task_in_progress),
                    style = AnclaTextStyles.taskDescription,
                    color = ScriptReaderButton,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(
                modifier = Modifier.height(
                    if (isExpanded) {
                        HomeScreenDimens.contentToButtonSpacerExpanded
                    } else {
                        HomeScreenDimens.contentToButtonSpacerCompact
                    }
                )
            )

            Button(
                onClick = {
                    isPressed.value = true
                    onComplete(task.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = HomeScreenDimens.buttonMinHeight)
                    .scale(buttonScale.value),
                colors = ButtonDefaults.buttonColors(containerColor = CardGreen),
                shape = RoundedCornerShape(HomeScreenDimens.buttonCornerRadius)
            ) {
                Text(
                    text = if (task.isInProgress) {
                        stringResource(R.string.home_btn_finish_task)
                    } else {
                        stringResource(R.string.home_btn_start_task)
                    },
                    color = TextPrimary,
                    style = AnclaTextStyles.primaryButton
                )
            }
        }
    }
}

@Composable
fun RestCard(
    isExpanded: Boolean = false,
    onStartMeditation: () -> Unit = {},
    title: String? = null,
    subtitle: String? = null,
    cta: String? = null
) {
    val resolvedTitle = title ?: stringResource(R.string.home_rest_title)
    val resolvedSubtitle = subtitle ?: stringResource(R.string.home_rest_subtitle)
    val resolvedCta = cta ?: stringResource(R.string.home_rest_cta)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(
                max = if (isExpanded) {
                    HomeScreenDimens.cardMaxWidthExpanded
                } else {
                    HomeScreenDimens.cardMaxWidthCompact
                }
            )
            .aspectRatio(
                if (isExpanded) {
                    HomeScreenDimens.restCardAspectRatioExpanded
                } else {
                    HomeScreenDimens.restCardAspectRatioCompact
                }
            )
            .clip(RoundedCornerShape(HomeScreenDimens.restCardCornerRadius)),
        shape = RoundedCornerShape(HomeScreenDimens.restCardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = CardPeach.copy(alpha = 0.22f)),
        elevation = CardDefaults.cardElevation(defaultElevation = HomeScreenDimens.restCardElevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(HomeScreenDimens.restCardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(HomeScreenDimens.restIconContainerSize)
                    .background(CardGreen.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = ScriptReaderButton,
                    modifier = Modifier.size(HomeScreenDimens.restIconSize)
                )
            }
            Spacer(modifier = Modifier.height(HomeScreenDimens.restIconBottomSpacing))
            Text(
                text = resolvedTitle,
                style = if (isExpanded) AnclaTextStyles.taskTitleExpanded else AnclaTextStyles.taskTitle,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(HomeScreenDimens.restTitleBottomSpacing))
            Text(
                text = resolvedSubtitle,
                style = AnclaTextStyles.taskDescription,
                color = TextPrimary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = HomeScreenDimens.restSubtitleHorizontalPadding)
            )

            Spacer(modifier = Modifier.height(HomeScreenDimens.restContentToButtonSpacing))

            Button(
                onClick = onStartMeditation,
                modifier = Modifier
                    .fillMaxWidth(HomeScreenDimens.restButtonWidthFraction)
                    .heightIn(min = HomeScreenDimens.buttonMinHeight),
                colors = ButtonDefaults.buttonColors(containerColor = ScriptReaderButton),
                shape = RoundedCornerShape(HomeScreenDimens.buttonCornerRadius)
            ) {
                Text(
                    text = resolvedCta,
                    color = SurfaceWhite,
                    style = AnclaTextStyles.primaryButton
                )
            }
        }
    }
}

