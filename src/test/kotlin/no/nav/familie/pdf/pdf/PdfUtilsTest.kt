package no.nav.familie.pdf.pdf

import com.itextpdf.layout.element.Div
import no.nav.familie.pdf.pdf.PdfUtils.håndterRekursivVerdiliste
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class PdfUtilsTest {
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
