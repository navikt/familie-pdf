package no.nav.familie.pdf.pdf

import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedBarneTabell
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedFlereArbeidsforhold
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedUtenlandsopphold
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
import no.nav.familie.pdf.pdf.pdfElementer.lagSpørsmålOgSvar
import no.nav.familie.pdf.pdf.visningsvarianter.lagTabell
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertNotNull

class PdfElementUtilsTest {
    private companion object {
        @JvmStatic
        private fun tabell(): Stream<FeltMap> =
            Stream.of(
                lagMedBarneTabell(),
                lagMedFlereArbeidsforhold(),
                lagMedUtenlandsopphold(),
            )
    }

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
            assertTrue(
                tekstInnhold.contains(forventetVerdi),
                "For input '$inputVerdi', forventet '$forventetVerdi', men fikk '$tekstInnhold'",
            )
        }
    }
    //endregion

    // region tabell
    @ParameterizedTest
    @MethodSource("tabell")
    fun `Utenlandsopphold sine objekter vises som Tabeller`(feltMap: FeltMap) {
        // Arrange
        val verdilisteElement = fåFørsteElementMedTabellVariant(feltMap)

        // Act
        val resultat =
            if (verdilisteElement?.verdiliste != null) verdilisteElement.verdiliste?.map { lagTabell(it) } else null

        // Assert
        assertNotNull(resultat, "Resultatet er null")
        resultat.forEach {
            assertNotNull(it, "Tabell er null")
            assertTrue(it is Table, "Elementet er ikke en tabell")
        }
    }

    fun fåFørsteElementMedTabellVariant(feltMap: FeltMap): VerdilisteElement? {
        fun traverse(verdiliste: List<VerdilisteElement>?): VerdilisteElement? {
            verdiliste?.forEach { element ->
                if (element.visningsVariant == "TABELL") {
                    return element
                }
                traverse(element.verdiliste)?.let { return it }
            }
            return null
        }
        return traverse(feltMap.verdiliste)
    }
    // endregion
}
