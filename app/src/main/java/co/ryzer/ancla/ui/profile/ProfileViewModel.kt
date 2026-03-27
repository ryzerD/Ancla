package co.ryzer.ancla.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ryzer.ancla.data.repository.SensoryProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: SensoryProfileRepository
) : ViewModel() {

    private val palettePreviewColorId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProfileUiState> = combine(
        repository.observeProfile(),
        palettePreviewColorId
    ) { profile, previewColorId ->
        val effectiveColorId = previewColorId ?: profile.selectedColorId
        val hasPendingChanges = previewColorId != null && previewColorId != profile.selectedColorId

        ProfileUiState(
            name = profile.name,
            emergencyContactName = profile.emergencyContactName,
            emergencyContact = profile.emergencyContact,
            selectedColorId = profile.selectedColorId,
            effectiveSelectedColorId = effectiveColorId,
            hasPendingPaletteChanges = hasPendingChanges,
            isLoaded = true
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState(isLoaded = false)
        )

    fun onPalettePreviewSelected(colorId: String) {
        palettePreviewColorId.value = colorId
    }

    fun discardPalettePreview() {
        palettePreviewColorId.value = null
    }

    fun savePaletteSelection() {
        val currentState = uiState.value
        if (!currentState.isLoaded) return
        if (!currentState.hasPendingPaletteChanges) return

        viewModelScope.launch {
            val currentProfile = repository.getProfile()
            repository.saveProfile(
                currentProfile.copy(selectedColorId = currentState.effectiveSelectedColorId)
            )
            palettePreviewColorId.value = null
        }
    }
}


