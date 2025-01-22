package no.nav.familie.pdf.no.nav.familie.pdf.pdf

import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.pdfa.PdfADocument
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedBarneTabell
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedFlereArbeidsforhold
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedForskjelligLabelIVerdiliste
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedInnholdsfortegnelse
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedTomVerdiliste
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedVerdiliste
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagToSiderInnholdsfortegnelse
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagUteninnholdsfortegnelse
import no.nav.familie.pdf.pdf.PdfService
import no.nav.familie.pdf.pdf.domain.FeltMap
import org.junit.jupiter.api.Assertions.assertFalse
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
        fun innholdsfortegnelseMedEnOgToSider(): Stream<FeltMap> =
            Stream.of(
                lagMedVerdiliste(),
                lagToSiderInnholdsfortegnelse(),
            )
    }

    //region Pdf
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
    //endregion

    //region Innholdsfortegnelse
    @Test
    fun `Pdf har søknadstype i overskrift`() {
        // Arrange
        val feltMap = lagMedVerdiliste()

        // Act
        val pdfDoc = opprettPdf(feltMap)
        val førsteSidePdf = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1))

        // Assert
        assertTrue(førsteSidePdf.contains("Søknad om overgangsstønad"))
        assertTrue(førsteSidePdf.contains("Brevkode: NAV 15-00.01"))
    }

    @Test
    fun `Overskrift dukker ikke opp som søknadstype`() {
        // Arrange
        val feltMap = lagMedFlereArbeidsforhold()

        // Act
        val pdfDoc = opprettPdf(feltMap)
        val førsteSidePdf = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1))

        // Assert
        val antallForekomster = Regex("Arbeid, utdanning og andre aktiviteter").findAll(førsteSidePdf).count()
        assertTrue(1 == antallForekomster, "Overskriften dukker opp to ganger")
    }

    @Test
    fun `Pdf har ikke parenteser i overskrift`() {
        // Arrange
        val feltMap = lagMedVerdiliste()

        // Act
        val pdfDoc = opprettPdf(feltMap)
        val førsteSidePdf = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1))

        // Assert
        assertFalse(førsteSidePdf.contains("("), "Overskriften inneholder '('")
        assertFalse(førsteSidePdf.contains(")"), "Overskriften inneholder ')'")
    }

    @ParameterizedTest
    @MethodSource("innholdsfortegnelseMedEnOgToSider")
    fun `Pdf legger forside med innholdsfortegnelse først`(feltMap: FeltMap) {
        // Act
        val pdfDoc = opprettPdf(feltMap)
        val førsteSideTekst = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1))

        // Assert
        assertTrue(førsteSideTekst.contains("Søknad om overgangsstønad"))
    }

    @ParameterizedTest
    @MethodSource("innholdsfortegnelseMedEnOgToSiderOgForventetSide")
    fun `Pdf har riktig sideantall i innholdsfortegnelsen`(
        feltMap: FeltMap,
        forventetSide: Int,
    ) {
        // Act
        val pdfDoc = opprettPdf(feltMap)
        val førsteSideTekst = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1))

        // Assert
        val forventetInnholdsfortegnelse =
            listOf(
                "Innsendingsdetaljer" to forventetSide,
            )
        for ((label, forventetSide) in forventetInnholdsfortegnelse) {
            assertTrue(førsteSideTekst.contains("$label $forventetSide"))
            val faktiskSideTekst = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(forventetSide))
            assertTrue(faktiskSideTekst.contains(label))
        }
    }
    //endregion

    //region Tabeller
    @Test
    fun `Pdf lager tabell dersom du har flere arbeidsforhold`() {
        // Arrange
        val feltMap = lagMedFlereArbeidsforhold()

        // Act
        val pdfDoc = opprettPdf(feltMap)
        val tekstIPdf = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(2))

        // Assert
        val arbeidsforhold1Index = tekstIPdf.indexOf("Arbeidsforhold 1")
        val navnIndex = tekstIPdf.indexOf("Nav", arbeidsforhold1Index)
        val arbeidsforhold2Index = tekstIPdf.indexOf("Arbeidsforhold 2", navnIndex)
        val termindatoIndex = tekstIPdf.indexOf("Bekk", arbeidsforhold2Index)

        assertTrue(arbeidsforhold1Index != -1)
        assertTrue(navnIndex != -1)
        assertTrue(arbeidsforhold2Index != -1)
        assertTrue(termindatoIndex != -1)
        assertTrue(arbeidsforhold1Index < navnIndex)
        assertTrue(navnIndex < arbeidsforhold2Index)
        assertTrue(arbeidsforhold2Index < termindatoIndex)
    }

    @Test
    fun `Tabeller får inn en liste av objekter som tegnes som tabeller`() {
        // Act
        val feltMap = lagMedBarneTabell()

        // Assert
        val pdfDoc = opprettPdf(feltMap)
        val tekstIPdf = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(2))

        // Assert
        val barn1Index = tekstIPdf.indexOf("Barn 1")
        val navnIndex = tekstIPdf.indexOf("Navn", barn1Index)
        val barn2Index = tekstIPdf.indexOf("Barn 2", navnIndex)
        val termindatoIndex = tekstIPdf.indexOf("Termindato", barn2Index)

        assertTrue(barn1Index != -1)
        assertTrue(navnIndex != -1)
        assertTrue(barn2Index != -1)
        assertTrue(termindatoIndex != -1)
        assertTrue(barn1Index < navnIndex)
        assertTrue(navnIndex < barn2Index)
        assertTrue(barn2Index < termindatoIndex)
    }

    @Test
    fun `Tom verdi i tabell skal ikke krasje pdf-opprettelsen`() {
        // Arrange
        val feltMap = lagMedBarneTabell()

        // Act
        val pdfDoc = pdfOppretterService.opprettPdf(feltMap)

        // Assert
        assertTrue(pdfDoc.isNotEmpty(), "Pdf-opprettelsen feilet, tom byteArray")
    }
    //endregion

    @Test
    fun `Pdf lager forside uten innholdsfortegnelse`() {
        // Arrange
        val feltMap = lagUteninnholdsfortegnelse()

        // Act
        val pdfDoc = opprettPdf(feltMap)
        val førsteSideTekst = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1))

        // Assert
        assertTrue(førsteSideTekst.contains("Søknad om overgangsstønad"))
        assertFalse(førsteSideTekst.contains("Innholdsfortegnelse"))
    }

    @Test
    fun `Pdf lager forside med innholdsfortegnelse`() {
        // Arrange
        val feltMap = lagMedInnholdsfortegnelse()

        // Act
        val pdfDoc = opprettPdf(feltMap)
        val førsteSideTekst = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1))

        // Assert
        assertTrue(førsteSideTekst.contains("Søknad om overgangsstønad"))
        assertTrue(førsteSideTekst.contains("Innholdsfortegnelse"))
    }

    private fun opprettPdf(feltMap: FeltMap): PdfADocument {
        val result = pdfOppretterService.opprettPdf(feltMap)
        val pdfReader = PdfReader(ByteArrayInputStream(result))
        val pdfWriter = PdfWriter(ByteArrayOutputStream())
        val pdfDoc = PdfADocument(pdfReader, pdfWriter)
        return pdfDoc
    }
}
