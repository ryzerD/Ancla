package co.ryzer.ancla.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ryzer.ancla.R
import co.ryzer.ancla.data.SensoryProfile
import co.ryzer.ancla.data.repository.SensoryProfileRepository
import co.ryzer.ancla.ui.components.AnclaTextField
import co.ryzer.ancla.ui.contacts.resolvePickedContact
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.AnclaTheme
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.CardPeach
import co.ryzer.ancla.ui.theme.CardRose
import co.ryzer.ancla.ui.theme.OnboardingSensorialDimens
import co.ryzer.ancla.ui.theme.ScriptReaderButton
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary
import co.ryzer.ancla.ui.theme.applySensoryPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    val titleStyle = MaterialTheme.typography.headlineLarge.copy(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        color = TextPrimary
    )
    val subtitleStyle = MaterialTheme.typography.bodyLarge.copy(
        fontFamily = FontFamily.SansSerif,
        color = TextSecondary
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
    ) {
        val showMissingNameFeedback = state.name.isBlank()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = OnboardingSensorialDimens.screenPadding,
                    top = OnboardingSensorialDimens.topPadding,
                    end = OnboardingSensorialDimens.screenPadding,
                    bottom = OnboardingSensorialDimens.contentBottomPadding
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(OnboardingSensorialDimens.sectionSpacing)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(OnboardingSensorialDimens.titleToSubtitleSpacing)) {
                Text(
                    text = stringResource(R.string.onboarding_sensorial_welcome_title),
                    style = titleStyle
                )
                Text(
                    text = stringResource(R.string.onboarding_sensorial_welcome_subtitle),
                    style = subtitleStyle
                )
            }

            OnboardingSectionHeader(
                number = 1,
                title = stringResource(R.string.onboarding_sensorial_identity_title)
            )
            Column(verticalArrangement = Arrangement.spacedBy(OnboardingSensorialDimens.sectionTitleToFieldSpacing)) {
                Text(
                    text = stringResource(R.string.onboarding_sensorial_name_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                CalmTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    placeholder = stringResource(R.string.onboarding_sensorial_name_placeholder)
                )
            }

            OnboardingSectionHeader(
                number = 2,
                title = stringResource(R.string.onboarding_sensorial_safety_title)
            )
            OnboardingContactCard(
                contactName = state.emergencyContactName,
                onContactNameChange = onEmergencyContactNameChange,
                contactPhone = state.emergencyContact,
                onContactPhoneChange = onEmergencyContactChange,
                onImportEmergencyContact = onImportEmergencyContact
            )

            OnboardingSectionHeader(
                number = 3,
                title = stringResource(R.string.onboarding_sensorial_preference_title)
            )
            Text(
                text = stringResource(R.string.onboarding_sensorial_palette_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            OnboardingPaletteGrid(
                selectedColorId = state.selectedColorId,
                onColorSelected = onColorSelected
            )
        }

        OnboardingPrimaryActionButton(
            enabled = state.canContinue,
            text = stringResource(R.string.onboarding_sensorial_cta_start),
            onClick = onContinue,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = OnboardingSensorialDimens.ctaButtonHorizontalPadding,
                    end = OnboardingSensorialDimens.ctaButtonHorizontalPadding,
                    bottom = OnboardingSensorialDimens.ctaButtonVerticalPadding
                )
        )

        if (showMissingNameFeedback) {
            Text(
                text = stringResource(R.string.onboarding_sensorial_name_required_feedback),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = OnboardingSensorialDimens.ctaButtonHorizontalPadding,
                        end = OnboardingSensorialDimens.ctaButtonHorizontalPadding,
                        bottom = OnboardingSensorialDimens.ctaButtonVerticalPadding +
                                OnboardingSensorialDimens.ctaMinHeight + 12.dp
                    )
            )
        }
    }
}

@Composable
private fun OnboardingSectionHeader(
    number: Int,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OnboardingSensorialDimens.sectionTitleTextSpacing)
    ) {
        Box(
            modifier = Modifier
                .size(OnboardingSensorialDimens.sectionBadgeSize)
                .clip(CircleShape)
                .background(SurfaceWhite.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
    }
}

@Composable
private fun OnboardingContactCard(
    contactName: String,
    onContactNameChange: (String) -> Unit,
    contactPhone: String,
    onContactPhoneChange: (String) -> Unit,
    onImportEmergencyContact: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(OnboardingSensorialDimens.contactCardCornerRadius))
            .background(CardPeach.copy(alpha = 0.35f))
            .padding(OnboardingSensorialDimens.contactCardPadding),
        verticalArrangement = Arrangement.spacedBy(OnboardingSensorialDimens.contactFieldSpacing)
    ) {
        Text(
            text = stringResource(R.string.onboarding_sensorial_contact_name_label),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        )
        CalmTextField(
            value = contactName,
            onValueChange = onContactNameChange,
            placeholder = stringResource(R.string.onboarding_sensorial_contact_name_label),
            containerAlpha = 0.95f
        )
        CalmTextField(
            value = contactPhone,
            onValueChange = onContactPhoneChange,
            placeholder = stringResource(R.string.onboarding_sensorial_contact_number_label),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            containerAlpha = 0.95f
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onImportEmergencyContact)
                .padding(top = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PersonAdd,
                    contentDescription = null,
                    tint = TextPrimary
                )
                Text(
                    text = stringResource(R.string.onboarding_sensorial_import_contact_cta),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                }
            }
        }
    }

@Composable
private fun OnboardingPaletteGrid(
    selectedColorId: String,
    onColorSelected: (String) -> Unit
) {
    val paletteOptions = listOf(
        OnboardingPaletteOption(
            id = "sage",
            labelRes = R.string.palette_sage,
            color = CardGreen
        ),
        OnboardingPaletteOption(
            id = "lavender",
            labelRes = R.string.palette_lavender,
            color = CardLavender
        ),
        OnboardingPaletteOption(
            id = "rose",
            labelRes = R.string.palette_rose,
            color = CardRose
        ),
        OnboardingPaletteOption(
            id = "peach",
            labelRes = R.string.palette_peach,
            color = CardPeach
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(OnboardingSensorialDimens.pickerLargeGridSpacing)
    ) {
        paletteOptions.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { option ->
                    val isSelected = option.id == selectedColorId
                    Box(
                        modifier = Modifier
                            .size(OnboardingSensorialDimens.pickerLargeItemSize)
                            .clip(CircleShape)
                            .background(option.color)
                            .clickable { onColorSelected(option.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(option.labelRes).uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) SurfaceWhite else TextPrimary.copy(alpha = 0.45f),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = OnboardingSensorialDimens.pickerLargeLabelBottomSpacing)
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = stringResource(option.labelRes),
                                tint = SurfaceWhite,
                                modifier = Modifier.size(OnboardingSensorialDimens.pickerSelectedIconSize)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPrimaryActionButton(
    enabled: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(OnboardingSensorialDimens.ctaMinHeight)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = RoundedCornerShape(OnboardingSensorialDimens.ctaButtonCornerRadius)
            )
            .clip(RoundedCornerShape(OnboardingSensorialDimens.ctaButtonCornerRadius))
            .background(
                brush = if (enabled) {
                    Brush.horizontalGradient(colors = listOf(ScriptReaderButton, CardGreen))
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(CardPeach.copy(alpha = 0.8f), CardPeach.copy(alpha = 0.8f))
                    )
                }
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    color = if (enabled) TextPrimary else TextPrimary.copy(alpha = 0.55f)
                )
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = if (enabled) TextPrimary else TextPrimary.copy(alpha = 0.55f)
            )
        }
    }
}

private data class OnboardingPaletteOption(
    val id: String,
    val labelRes: Int,
    val color: Color
)

@Composable
fun CalmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    containerAlpha: Float = 0.8f
) {
    AnclaTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        minHeight = OnboardingSensorialDimens.textFieldMinHeight,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        containerAlpha = containerAlpha,
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