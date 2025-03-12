package no.nav.familie.pdf.pdf

import com.itextpdf.layout.element.Div
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.pdfElementer.håndterRekursivVerdiliste
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class InnholdsfortegnelseTest {
    @Test
    fun `Dersom label er tom streng skal rekursjonen ikke legge til nytt objekt i seksjonen`() {
        // Arrange
        val verdilisteElement = listOf(VerdilisteElement("", "Ja"), VerdilisteElement("Hei på deg", "Heihei!"))
        val seksjon = Div()
        // Act
        håndterRekursivVerdiliste(verdilisteElement, seksjon)
        // Assert
        assertTrue(seksjon.children.size == 1)
    }
}
