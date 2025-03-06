package no.nav.familie.pdf.pdf

import com.itextpdf.layout.element.Text
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.pdfElementer.lagSpørsmålOgSvar
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PdfElementUtilsTest {
    // Region lagVerdiElement
    @Test
    fun `leggTilKolon legger til kolon dersom label ikke har et tegn på slutten av spørsmål`() {
        // Arrange
        val testScenarioer =
            listOf(
                "Bor du på denne adressen" to "Bor du på denne adressen:",
                "Bor du på denne adressen:" to "Bor du på denne adressen:",
                "Bor du på denne adressen?" to "Bor du på denne adressen?",
                "Bor du på denne adressen." to "Bor du på denne adressen.",
                "Bor du på denne adressen!" to "Bor du på denne adressen!",
                "Bor du på denne adressen;" to "Bor du på denne adressen;",
            )

        for ((inputVerdi, forventetVerdi) in testScenarioer) {
            // Act
            val verdilisteElement = VerdilisteElement(label = inputVerdi, verdi = "Ja")
            val verdiElement = lagSpørsmålOgSvar(verdilisteElement)
            val tekstInnhold =
                verdiElement.children
                    .filterIsInstance<Text>()
                    .joinToString("") { it.text }

            // Assert
            assertTrue(tekstInnhold.contains(forventetVerdi), "For input '$inputVerdi', forventet '$forventetVerdi', men fikk '$tekstInnhold'")
        }
    }

    //endregion
}
