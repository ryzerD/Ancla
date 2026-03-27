package co.ryzer.ancla.ui.screens

import android.app.Activity
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.core.content.ContextCompat
import co.ryzer.ancla.R
import co.ryzer.ancla.data.SensoryProfile
import co.ryzer.ancla.data.repository.SensoryProfileRepository
import co.ryzer.ancla.ui.components.AnclaTextField
import co.ryzer.ancla.ui.components.SensoryPalettePicker
import co.ryzer.ancla.ui.contacts.resolvePickedContact
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.OnboardingSensorialDimens
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.applySensoryPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val SoftText = Color(0xFF2D2D2D)
private val SoftAccent = Color(0xFFA3C1AD)
private const val ONBOARDING_CONTACT_PICKER_TAG = "OnboardingContactPicker"

data class OnboardingSensorialUiState(
    val name: String = "",
    val emergencyContactName: String = "",
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
                    emergencyContactName = profile.emergencyContactName,
                    emergencyContact = profile.emergencyContact,
                    selectedColorId = profile.selectedColorId
                )
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onEmergencyContactNameChange(value: String) {
        _uiState.update { it.copy(emergencyContactName = value) }
    }

    fun onEmergencyContactChange(value: String) {
        _uiState.update { it.copy(emergencyContact = value) }
    }

    fun onEmergencyContactImported(contactName: String, contactPhone: String) {
        _uiState.update {
            it.copy(
                emergencyContactName = contactName,
                emergencyContact = contactPhone
            )
        }
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
                    emergencyContactName = snapshot.emergencyContactName.trim(),
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
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val pickContactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { contactUri ->
        if (contactUri == null) return@rememberLauncherForActivityResult
        val pickedContact = resolvePickedContact(context, contactUri) ?: return@rememberLauncherForActivityResult
        viewModel.onEmergencyContactImported(
            contactName = pickedContact.displayName,
            contactPhone = pickedContact.phoneNumber
        )
    }

    val requestContactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickContactLauncher.launch(null)
        } else {
            Log.w(
                ONBOARDING_CONTACT_PICKER_TAG,
                "READ_CONTACTS denied. Contact number import may be unavailable on this device."
            )
        }
    }


    // Immediate sensory preview while selecting a color in onboarding.
    LaunchedEffect(uiState.selectedColorId) {
        applySensoryPalette(uiState.selectedColorId)
    }

    OnboardingSystemBarsEffect()

    OnboardingSensorialContent(
        state = uiState,
        onNameChange = viewModel::onNameChange,
        onEmergencyContactNameChange = viewModel::onEmergencyContactNameChange,
        onEmergencyContactChange = viewModel::onEmergencyContactChange,
        onColorSelected = viewModel::onColorSelected,
        onImportEmergencyContact = {
            val hasContactsPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

            if (hasContactsPermission) {
                pickContactLauncher.launch(null)
            } else {
                requestContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        },
        onContinue = { viewModel.onContinueRequested(onContinue) }
    )
}

@Composable
fun OnboardingSensorialContent(
    state: OnboardingSensorialUiState,
    onNameChange: (String) -> Unit,
    onEmergencyContactNameChange: (String) -> Unit,
    onEmergencyContactChange: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    onImportEmergencyContact: () -> Unit,
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
        color = SoftText
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
                Text(
                    text = stringResource(R.string.onboarding_sensorial_welcome_title),
                    style = titleStyle
                )
                Spacer(modifier = Modifier.height(OnboardingSensorialDimens.titleToSubtitleSpacing))
                Text(
                    text = stringResource(R.string.onboarding_sensorial_welcome_subtitle),
                    style = subtitleStyle
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
        }

        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.sectionSpacing))

        Text(
            text = stringResource(R.string.onboarding_sensorial_identity_title),
            style = sectionTitleStyle
        )
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.sectionTitleToFieldSpacing))
        CalmTextField(
            value = state.name,
            onValueChange = onNameChange,
            placeholder = stringResource(R.string.onboarding_sensorial_name_placeholder)
        )
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.fieldToHintSpacing))
        Text(
            text = stringResource(R.string.onboarding_sensorial_name_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.hintToSectionSpacing))

        Text(
            text = stringResource(R.string.onboarding_sensorial_safety_title),
            style = sectionTitleStyle
        )
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.sectionTitleToFieldSpacing))
        CalmTextField(
            value = state.emergencyContactName,
            onValueChange = onEmergencyContactNameChange,
            placeholder = stringResource(R.string.onboarding_sensorial_contact_name_label)
        )
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.sectionTitleToFieldSpacing))
        CalmTextField(
            value = state.emergencyContact,
            onValueChange = onEmergencyContactChange,
            placeholder = stringResource(R.string.onboarding_sensorial_contact_number_label),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.sectionTitleToFieldSpacing))
        Button(
            onClick = onImportEmergencyContact,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SoftAccent,
                contentColor = SoftText
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.onboarding_sensorial_import_contact_cta))
        }
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.fieldToHintSpacing))
        Text(
            text = stringResource(R.string.onboarding_sensorial_contact_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.hintToSectionSpacing))

        Text(
            text = stringResource(R.string.onboarding_sensorial_preference_title),
            style = sectionTitleStyle
        )
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.sectionTitleToFieldSpacing))
        SensoryPalettePicker(
            selectedColorId = state.selectedColorId,
            onColorSelected = onColorSelected
        )
        Spacer(modifier = Modifier.height(OnboardingSensorialDimens.fieldToHintSpacing))
        Text(
            text = stringResource(R.string.onboarding_sensorial_palette_hint),
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
                containerColor = SoftAccent,
                contentColor = SoftText,
                disabledContainerColor = SoftAccent.copy(alpha = 0.6f),
                disabledContentColor = SoftText.copy(alpha = 0.7f)
            )
        ) {
            Text(
                text = stringResource(R.string.onboarding_sensorial_cta_start),
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
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    AnclaTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        minHeight = OnboardingSensorialDimens.textFieldMinHeight,
        singleLine = true,
        keyboardOptions = keyboardOptions,
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
            onEmergencyContactNameChange = {},
            onEmergencyContactChange = {},
            onColorSelected = {},
            onImportEmergencyContact = {},
            onContinue = {}
        )
    }
}