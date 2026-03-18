package co.ryzer.ancla.ui.screens

import co.ryzer.ancla.ui.profile.ProfileUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MainScreenNavigationTest {

    @Test
    fun `primer arranque navega a onboarding cuando nombre esta vacio`() {
        val state = ProfileUiState(
            name = "",
            emergencyContact = "",
            selectedColorId = "lavender",
            isLoaded = true
        )

        val route = resolveStartDestination(state)

        assertEquals("onboarding", route)
    }

    @Test
    fun `arranque posterior navega a home cuando perfil ya existe`() {
        val state = ProfileUiState(
            name = "Alex",
            emergencyContact = "123-456",
            selectedColorId = "sage",
            isLoaded = true
        )

        val route = resolveStartDestination(state)

        assertEquals("home", route)
    }

    @Test
    fun `mientras perfil no esta cargado no se decide ruta`() {
        val state = ProfileUiState(isLoaded = false)

        val route = resolveStartDestination(state)

        assertNull(route)
    }
}

