package no.nav.familie.pdf.pdf

import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedBarneTabell
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedFlereArbeidsforhold
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedUtenlandsopphold
import no.nav.familie.pdf.pdf.PdfElementUtils.lagTabell
import no.nav.familie.pdf.pdf.PdfElementUtils.lagVerdiElement
import no.nav.familie.pdf.pdf.domain.FeltMap
import no.nav.familie.pdf.pdf.domain.VerdilisteElement
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
            val verdiElement = lagVerdiElement(verdilisteElement)
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
    fun `Utenlandsopphold, arbeidsforhold og barna dine sine objekter vises som Tabell`(feltMap: FeltMap) {
        // Arrange
        val verdilisteElement = finnFørsteElementMedTabellVariant(feltMap.verdiliste)
        // Act
        val resultat =
            if (verdilisteElement?.verdiliste != null) lagTabell(verdilisteElement) else null

        // Assert
        assertNotNull(resultat, "Tabell er null")
        assertTrue(resultat is Table, "Elementet er ikke en tabell")
    }

    @ParameterizedTest
    @MethodSource("tabell")
    fun `Tabell har riktig antall kolonner og er en tabell`(feltMap: FeltMap) {
        // Arrange
        val verdilisteElement = finnFørsteElementMedTabellVariant(feltMap.verdiliste)
        // Act
        val resultat =
            if (verdilisteElement?.verdiliste != null) lagTabell(verdilisteElement) else null

        // Assert
        assertNotNull(resultat, "Tabell er null")
        assertTrue(resultat is Table, "Elementet er ikke en tabell")

        // Additional checks
        val table = resultat as Table
        val numberOfColumns = table.numberOfColumns
        val numberOfRows = table.numberOfRows

        // Check the number of columns
        assertTrue(numberOfColumns == 2, "Tabellen skal ha 2 kolonner, men har $numberOfColumns")

        // Check the number of rows
        assertTrue(numberOfRows > 0, "Tabellen skal ha minst 1 rad, men har $numberOfRows")
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

    fun finnFørsteElementMedTabellVariant(verdiliste: List<VerdilisteElement>): VerdilisteElement? =
        verdiliste.firstOrNull { it.visningsVariant == "TABELL" }
            ?: verdiliste
                .asSequence()
                .mapNotNull { it.verdiliste?.let(::finnFørsteElementMedTabellVariant) }
                .firstOrNull()

    // endregion
}
