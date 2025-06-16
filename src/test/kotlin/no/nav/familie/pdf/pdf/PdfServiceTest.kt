package no.nav.familie.pdf.no.nav.familie.pdf.pdf

import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.pdfa.PdfADocument
import io.mockk.mockk
import no.nav.familie.pdf.infrastruktur.UnleashNextService
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedBarneTabell
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedFlereArbeidsforhold
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedForskjelligLabelIVerdiliste
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedHtmlVerditype
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedInnholdsfortegnelse
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedPunktliste
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedTomPunktliste
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedTomVerdiliste
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedTomtSkjemanummer
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedUtenlandsopphold
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedVerdiliste
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagMedekstraBunntekst
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagToSiderInnholdsfortegnelse
import no.nav.familie.pdf.no.nav.familie.pdf.pdf.utils.lagUtenSkjemanummer
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
import java.io.FileOutputStream
import java.util.stream.Stream

class PdfServiceTest {
    private val unleashNextService: UnleashNextService = mockk(relaxed = true)
    private val pdfOppretterService = PdfService(unleashNextService)

    private val skrivTilFile = false

    companion object {
        @JvmStatic
        fun innholdsfortegnelseMedEnOgToSiderOgForventetSide(): Stream<Arguments> =
            Stream.of(
                Arguments.of(lagMedVerdiliste(), 2),
                Arguments.of(lagToSiderInnholdsfortegnelse(), 4),
            )

        @JvmStatic
        fun innholdsfortegnelseMedEnOgToSider(): Stream<FeltMap> =
            Stream.of(
                lagMedVerdiliste(),
                lagToSiderInnholdsfortegnelse(),
            )

        @JvmStatic
        fun tomPunktliste(): Stream<FeltMap> =
            Stream.of(
                lagMedTomPunktliste(),
                lagMedTomPunktliste(listOf()),
            )

        @JvmStatic
        fun underOverskriftUtenSkjemanummer(): Stream<FeltMap> =
            Stream.of(
                lagMedTomtSkjemanummer(),
                lagUtenSkjemanummer(),
            )

        @JvmStatic
        fun tabell(): Stream<FeltMap> =
            Stream.of(
                lagMedBarneTabell(),
                lagMedFlereArbeidsforhold(),
                lagMedUtenlandsopphold(),
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

    @Test
    fun `Pdf har med ekstraBunntekst hvis spesifisert`() {
        // Arrange
        val feltMap = lagMedekstraBunntekst()

        // Act
        val pdfDoc = opprettPdf(feltMap)

        // Assert
        for (pageNumber in 1..pdfDoc.numberOfPages) {
            val pageText = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(pageNumber))
            assertTrue(pageText.contains("Side $pageNumber av ${pdfDoc.numberOfPages}"))
            assertTrue(pageText.contains("Øvre venstre tekst"))
            assertTrue(pageText.contains("Øvre midtre tekst"))
            assertTrue(pageText.contains("Øvre høyre tekst"))
            assertTrue(pageText.contains("Nedre venstre tekst"))
            assertTrue(pageText.contains("Nedre midtre tekst"))
        }
    }

    @Test
    fun `Pdf har med Html label`() {
        // Arrange
        val feltMap = lagMedHtmlVerditype()

        // Act
        val pdfDoc = opprettPdf(feltMap)
        val førsteSidePdf = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1))

        // Assert
        assertTrue(førsteSidePdf.contains("Brukerinformasjon"))
        assertTrue(førsteSidePdf.contains("Med Ytterligere informasjon"))
        assertTrue(førsteSidePdf.contains("Informasjon kan bli funnet i NAV 95-15.36 Generell fullmakt (åpnes i ny fane)."))
        assertTrue(førsteSidePdf.contains("Merk dette"))
    }

    //endregion

    //region Innholdsfortegnelse
    @Test
    fun `Pdf har skjemanummer i under-overskriften`() {
        // Arrange
        val feltMap = lagMedVerdiliste()

        // Act
        val pdfDoc = opprettPdf(feltMap)
        val førsteSidePdf = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1))

        // Assert
        assertTrue(førsteSidePdf.contains("Søknad om overgangsstønad"))
        assertTrue(førsteSidePdf.contains("Skjemanummer: NAV 15-00.01"))
    }

    @ParameterizedTest
    @MethodSource("underOverskriftUtenSkjemanummer")
    fun `Pdf har ikke skjemanummer i overskrift`(feltMap: FeltMap) {
        // Act
        val pdfDoc = opprettPdf(feltMap)
        val førsteSidePdf = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1))

        // Assert
        assertFalse(førsteSidePdf.contains("Skjemanummer: NAV 15-00.01"))
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
    //endregion

    //region Tabeller
    @Test
    fun `Tom verdi i tabell skal ikke krasje pdf-opprettelsen`() {
        // Arrange
        val feltMap = lagMedBarneTabell()

        // Act
        val pdfDoc = pdfOppretterService.opprettPdf(feltMap, true)

        // Assert
        assertTrue(pdfDoc.isNotEmpty(), "Pdf-opprettelsen feilet, tom byteArray")
    }
    //endregion

    // region Punktliste
    @Test
    fun `Pdf lager en punktliste når visningsvarianten har PUNKTLISTE valgt`() {
        // Arrange
        val feltMap = lagMedPunktliste()

        // Act
        val pdfDoc = opprettPdf(feltMap)
        val tekstIPdf = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(2))

        // Assert
        val faktiskPunkter = tekstIPdf.count { it == '\u2022' }
        val forventetPunkter = 5
        assertTrue(
            faktiskPunkter == forventetPunkter,
            "Forventet $forventetPunkter punkter men fikk $faktiskPunkter",
        )
    }

    @ParameterizedTest
    @MethodSource("tomPunktliste")
    fun `Pdf lager ikke en punktliste når verdiliste er tom`() {
        // Arrange
        val feltMap = lagMedTomPunktliste()

        // Act
        val pdfDoc = opprettPdf(feltMap)
        val tekstIPdf = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(2))

        // Assert
        assertFalse(tekstIPdf.contains("Gjelder noe av dette deg?"))
    }
    // endregion

    private fun opprettPdf(feltMap: FeltMap): PdfADocument {
        val result = pdfOppretterService.opprettPdf(feltMap)

        writeBytesToFile(result, "delme.pdf")

        val pdfReader = PdfReader(ByteArrayInputStream(result))
        val pdfWriter = PdfWriter(ByteArrayOutputStream())
        val pdfDoc = PdfADocument(pdfReader, pdfWriter)
        return pdfDoc
    }

    fun writeBytesToFile(
        byteArray: ByteArray,
        filePath: String,
    ) {
        if (skrivTilFile) {
            val outputStream = FileOutputStream(filePath)
            outputStream.write(byteArray)
            outputStream.close()
        }
    }
}
