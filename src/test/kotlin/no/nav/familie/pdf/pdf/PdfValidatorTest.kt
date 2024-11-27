package no.nav.familie.pdf.no.nav.familie.pdf.pdf

import no.nav.familie.pdf.pdf.JsonLeser
import no.nav.familie.pdf.pdf.PdfValidator
import no.nav.familie.pdf.pdf.TestPdfService
import no.nav.familie.pdf.pdf.domain.PdfStandard
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PdfValidatorTest {
    private lateinit var pdfBytes: ByteArray

    @BeforeAll
    fun setup() {
        val feltMap = JsonLeser.lesJson()
        val pdfMedStandarder = TestPdfService().opprettPdfMedStandarder(feltMap)
        pdfBytes = pdfMedStandarder.pdf
    }

    @ParameterizedTest
    @EnumSource(PdfStandard::class)
    fun `validerPdf skal validere for PDFA-standarder`(standardType: PdfStandard) {
        // Arrange
        val standard = "PDFA-${standardType.standard.uppercase()}"
        // Act
        val result = PdfValidator.validerPdf(pdfBytes, standard)
        // Assert
        assertTrue(result.samsvarer, "Pdf-en samsvarer ikke med standarden $standard med feilen ${result.feiletRegel}")
    }
}
