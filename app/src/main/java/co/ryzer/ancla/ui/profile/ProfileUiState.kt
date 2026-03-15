package co.ryzer.ancla.ui.profile

data class ProfileUiState(
    val name: String = "",
    val emergencyContact: String = "",
    val selectedColorId: String = "lavender",
    val effectiveSelectedColorId: String = selectedColorId,
    val hasPendingPaletteChanges: Boolean = false,
    val isLoaded: Boolean = false
) {
    val requiresOnboarding: Boolean = isLoaded && name.isBlank()
}


