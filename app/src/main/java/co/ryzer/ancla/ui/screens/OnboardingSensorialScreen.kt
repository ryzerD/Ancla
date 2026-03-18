package co.ryzer.ancla.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ryzer.ancla.data.SensoryProfile
import co.ryzer.ancla.data.repository.SensoryProfileRepository
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.OnboardingSensorialDimens
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.applySensoryPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import co.ryzer.ancla.ui.components.AnclaTextField
import co.ryzer.ancla.ui.components.SensoryPalettePicker

private val ColorSageAction = Color(0xFFA3C1AD)

data class OnboardingSensorialUiState(
    val name: String = "",
    val emergencyContact: String = "",
    val selectedColorId: String = "lavender"
) {
    val canContinue: Boolean = name.isNotBlank()
}

@HiltViewModel
class OnboardingSensorialViewModel @Inject constructor(
    private val sensoryProfileRepository: SensoryProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingSensorialUiState())
    val uiState: StateFlow<OnboardingSensorialUiState> = _uiState

    init {
        viewModelScope.launch {
            val profile = sensoryProfileRepository.getProfile()
            _uiState.update {
                it.copy(
                    name = profile.name,
                    emergencyContact = profile.emergencyContact,
                    selectedColorId = profile.selectedColorId
                )
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onEmergencyContactChange(value: String) {
        _uiState.update { it.copy(emergencyContact = value) }
    }

    fun onColorSelected(colorId: String) {
        _uiState.update { it.copy(selectedColorId = colorId) }
    }

    fun onContinueRequested(onContinue: (OnboardingSensorialUiState) -> Unit) {
        val snapshot = uiState.value
        if (!snapshot.canContinue) return

        viewModelScope.launch {
            sensoryProfileRepository.saveProfile(
                SensoryProfile(
                    name = snapshot.name.trim(),
                    emergencyContact = snapshot.emergencyContact.trim(),
                    selectedColorId = snapshot.selectedColorId
                )
            )
            onContinue(snapshot)
        }
    }
}

@Composable
fun OnboardingSensorialScreen(
    viewModel: OnboardingSensorialViewModel = hiltViewModel(),
    onContinue: (OnboardingSensorialUiState) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Immediate sensory preview while selecting a color in onboarding.
    LaunchedEffect(uiState.selectedColorId) {
        applySensoryPalette(uiState.selectedColorId)
    }

    OnboardingSystemBarsEffect()

    OnboardingSensorialContent(
        state = uiState,
        onNameChange = viewModel::onNameChange,
        onEmergencyContactChange = viewModel::onEmergencyContactChange,
        onColorSelected = viewModel::onColorSelected,
        onContinue = { viewModel.onContinueRequested(onContinue) }
    )
}

@Composable
fun OnboardingSensorialContent(
    state: OnboardingSensorialUiState,
    onNameChange: (String) -> Unit,
    onEmergencyContactChange: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    onContinue: () -> Unit
) {
    val titleStyle = MaterialTheme.typography.headlineMedium.copy(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    )
    val subtitleStyle = MaterialTheme.typography.bodyLarge.copy(
        fontFamily = FontFamily.SansSerif,
        color = TextSecondary
    )
    val sectionTitleStyle = MaterialTheme.typography.titleMedium.copy(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(OnboardingSensorialDimens.screenPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "¡Bienvenido a Ancla!", style = titleStyle)
                Spacer(modifier = Modifier.height(OnboardingSensorialDimens.titleToSubtitleSpacing))
                Text(
                    text = "Vamos a personalizar tu espacio seguro.",
                    style = subtitleStyle
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
        }

        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.sectionSpacing))

        Text(text = "1. Tu Identidad", style = sectionTitleStyle)
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.sectionTitleToFieldSpacing))
        CalmTextField(
            value = state.name,
            onValueChange = onNameChange,
            placeholder = "Escribe tu nombre (ej: Alex)"
        )
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.fieldToHintSpacing))
        Text(
            text = "Lo usaremos para saludarte en la app.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.hintToSectionSpacing))

        Text(text = "2. Tu Seguridad", style = sectionTitleStyle)
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.sectionTitleToFieldSpacing))
        CalmTextField(
            value = state.emergencyContact,
            onValueChange = onEmergencyContactChange,
            placeholder = "Contacto de emergencia (ej: 123-456)"
        )
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.fieldToHintSpacing))
        Text(
            text = "Lo usaremos para el guion de SOS.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.hintToSectionSpacing))

        Text(text = "3. Tu Preferencia Sensorial", style = sectionTitleStyle)
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.sectionTitleToFieldSpacing))
        SensoryPalettePicker(
            selectedColorId = state.selectedColorId,
            onColorSelected = onColorSelected
        )
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.fieldToHintSpacing))
        Text(
            text = "Elige un color para tu app.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.ctaTopSpacing))

        Button(
            onClick = onContinue,
            enabled = state.canContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(OnboardingSensorialDimens.ctaMinHeight)
                .align(Alignment.Start),
            shape = RoundedCornerShape(percent = 50),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorSageAction,
                contentColor = TextPrimary,
                disabledContainerColor = ColorSageAction.copy(alpha = 0.6f),
                disabledContentColor = TextPrimary.copy(alpha = 0.7f)
            )
        ) {
            Text(
                text = "Empezar ahora",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.ctaBottomSpacing))
    }
}

@Composable
fun CalmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    AnclaTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        minHeight = OnboardingSensorialDimens.textFieldMinHeight,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        ),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary.copy(alpha = 0.78f)
                )
            )
        }
    )
}


@Composable
private fun OnboardingSystemBarsEffect() {
    val view = LocalView.current
    DisposableEffect(view) {
        val activity = view.context as? Activity
        if (activity == null) {
            onDispose { }
        } else {
            val window = activity.window
            val insetsController = WindowCompat.getInsetsController(window, view)

            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insetsController.hide(WindowInsetsCompat.Type.statusBars())

            onDispose {
                insetsController.show(WindowInsetsCompat.Type.statusBars())
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun OnboardingSensorialScreenPreview() {
    AnclaTheme {
        OnboardingSensorialContent(
            state = OnboardingSensorialUiState(),
            onNameChange = {},
            onEmergencyContactChange = {},
            onColorSelected = {},
            onContinue = {}
        )
    }
}







