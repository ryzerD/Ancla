package co.ryzer.ancla.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class SensoryPaletteTest {

    @Test
    fun `aplicar rose cambia fondo de la app`() {
        applySensoryPalette("rose")

        assertEquals(Color(0xFFFCF1F1), AnclaBackground)
    }

    @Test
    fun `aplicar sage cambia color de accion principal`() {
        applySensoryPalette("sage")

        assertEquals(Color(0xFFA3C1AD), ScriptReaderButton)
    }

    @Test
    fun `color desconocido vuelve a lavanda por defecto`() {
        applySensoryPalette("otro")

        assertEquals(Color(0xFFFAF0E6), AnclaBackground)
    }
}

