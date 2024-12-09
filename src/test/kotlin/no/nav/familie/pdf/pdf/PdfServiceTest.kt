package no.nav.familie.pdf.no.nav.familie.pdf.pdf

import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.pdfa.PdfADocument
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagAdresseMedBareLinjeskift
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagAdresseMedFlereLinjeskift
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedForskjelligLabelIVerdiliste
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedTomAdresse
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedTomVerdiliste
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedVerdiliste
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagNullInnhold
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagToSiderInnholdsfortegnelse
import no.nav.familie.pdf.pdf.PdfService
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.ByteArrayInputStream
import java.util.stream.Stream

class PdfServiceTest {
    private val pdfOppretterService = PdfService()

    companion object {
        @JvmStatic
        fun innholdsfortegnelseMedEnOgToSiderOgForventetSide(): Stream<Arguments> =
            Stream.of(
                Arguments.of(lagMedVerdiliste(), 2),
                Arguments.of(lagToSiderInnholdsfortegnelse(), 3),
            )

        @JvmStatic
        fun innholdsfortegnelseMedEnOgToSider(): Stream<Map<String, Any>> =
            Stream.of(
                lagMedVerdiliste(),
                lagToSiderInnholdsfortegnelse(),
            )

        @JvmStatic
        fun tomAdresse(): Stream<Map<String, Any>> =
            Stream.of(
                lagAdresseMedBareLinjeskift(),
                lagMedTomAdresse(),
            )
    }

    @Test
    fun `Pdf med tom verdiliste returnerer ikke en tom bytearray`() {
        // Arrange
        val feltMap = lagMedTomVerdiliste()
        // Act
        val result = pdfOppretterService.opprettPdf(feltMap)
        // Assert
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `Pdf med verdiliste returnerer ikke en tom bytearray`() {
        // Arrange
        val feltMap = lagMedVerdiliste()
        // Act
        val result = pdfOppretterService.opprettPdf(feltMap)
        // Assert
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `Pdf med forskjellige labels`() {
        // Arrange
        val feltMap = lagMedForskjelligLabelIVerdiliste()
        // Act
        val result = pdfOppretterService.opprettPdf(feltMap)
        // Assert
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `Pdf kaster exception hvis label eller verdiliste er null`() {
        // Arrange
        val feltMap = lagNullInnhold()
        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            pdfOppretterService.opprettPdf(feltMap as Map<String, Any>)
        }
    }

    @Test
    fun `Pdf har riktig sideantall for nåværende side`() {
        // Arrange
        val feltMap = lagMedVerdiliste()

        // Act
        val pdfDoc = opprettPdf(feltMap)

        // Assert
        for (pageNumber in 1..pdfDoc.numberOfPages) {
            val pageText = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(pageNumber))
            assertTrue(pageText.contains("Side $pageNumber av ${pdfDoc.numberOfPages}"))
        }
    }

    @ParameterizedTest
    @MethodSource("innholdsfortegnelseMedEnOgToSider")
    fun `Pdf legger forside med innholdsfortegnelse først`(feltMap: Map<String, Any>) {
        // Act
        val pdfDoc = opprettPdf(feltMap)
        val førsteSideTekst = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1))

        // Assert
        assertTrue(førsteSideTekst.contains("Søknad om overgangsstønad"))
    }

    @ParameterizedTest
    @MethodSource("innholdsfortegnelseMedEnOgToSiderOgForventetSide")
    fun `Pdf har riktig sideantall i innholdsfortegnelsen`(
        feltMap: Map<String, Any>,
        forventetSide: Int,
    ) {
        // Act
        val pdfDoc = opprettPdf(feltMap)
        val firstPageText = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1))

        // Assert
        val forventetInnholdsfortegnelse =
            listOf(
                "Innsendingsdetaljer" to forventetSide,
            )
        for ((label, forventetSide) in forventetInnholdsfortegnelse) {
            assertTrue(firstPageText.contains("$label $forventetSide"))
            val actualPageText = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(forventetSide))
            assertTrue(actualPageText.contains(label))
        }
    }

    @ParameterizedTest
    @MethodSource("tomAdresse")
    fun `Pdf med innhold i Adresse blir renset for tom og flere linjeskift`(feltMap: Map<String, Any>) {
        // Act
        val pdfDoc = opprettPdf(feltMap)
        val andreSideTekst = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(2))
        // Assert
        assertTrue(andreSideTekst.contains("Ingen registrert adresse"))
    }

    @Test
    fun `Pdf med en adresse og flere linjeskift blir redusert til ett linjeskift`() {
        // Arrange
        val feltMap = lagAdresseMedFlereLinjeskift()
        // Act
        val pdfDoc = opprettPdf(feltMap)
        val andreSideTekst = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(2))
        // Assert
        // Tror ekstra mellomrommet etter 12 er fordi pdf genereringen legger til en ekstra linje. Debuget og ser aldri hvor den blir lagt til.
        assertTrue(andreSideTekst.contains("Adresse 12 \n0999 Oslo"))
    }

    private fun opprettPdf(feltMap: Map<String, Any>): PdfADocument {
        val result = pdfOppretterService.opprettPdf(feltMap)
        val pdfReader = PdfReader(ByteArrayInputStream(result))
        val pdfWriter = PdfWriter(ByteArrayOutputStream())
        val pdfDoc = PdfADocument(pdfReader, pdfWriter)
        return pdfDoc
    }
}
