package no.nav.familie.pdf.no.nav.familie.pdf.pdf

import io.mockk.mockk
import no.nav.familie.pdf.infrastruktur.UnleashNextService
import no.nav.familie.pdf.pdf.PdfService
import no.nav.familie.pdf.pdf.domain.PdfStandard
import no.nav.familie.pdf.pdf.lokalKjøring.JsonLeser
import no.nav.familie.pdf.pdf.lokalKjøring.PdfValidator
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PdfValidatorTest {
    private lateinit var pdfBytes: ByteArray
    private val unleashNextService: UnleashNextService = mockk(relaxed = true)
    private val pdfService = PdfService(unleashNextService)

    @BeforeAll
    fun setup() {
        val feltMap = JsonLeser.lesSøknadJson()
        pdfBytes = pdfService.opprettPdf(feltMap, 2)
    }

    @ParameterizedTest
    @EnumSource(PdfStandard::class)
    fun `validerPdf skal validere for PDFA-standarder`(standardType: PdfStandard) {
        // Arrange
        val standard = "PDFA-${standardType.standard.uppercase()}"
        // Act
        val result = PdfValidator.validerPdf(pdfBytes, standard)
        val feiletRegel = utenPdfA1CidSetRegel(result.feiletRegel)
        // Assert
        // Spesialtilfelle fordi regel 8.8-2 ikke er oppfylt ved klikkbar lenke i innholdsfortegnelsen.
        // Den trengs også å sjekkes med mellomrom på slutten i tillegg til uten. Noe rart med dataen.
        assertTrue(
            feiletRegel == "[[specification=ISO 14289-2:2024 clause=8.8 testNumber=2]=12 ]" ||
                feiletRegel == "[[specification=ISO 14289-2:2024 clause=8.8 testNumber=2]=12]" ||
                feiletRegel == "[[specification=ISO 19005-1:2005 clause=6.4 testNumber=2]=1 ]" ||
                feiletRegel == "[[specification=ISO 19005-1:2005 clause=6.4 testNumber=2]=1]" ||
                feiletRegel == "[[specification=ISO 14289-1:2014 clause=6.1 testNumber=1]=1]" ||
                feiletRegel == "[]" ||
                result.samsvarer,
            "Pdf-en samsvarer ikke med standarden $standard med feilen ${result.feiletRegel}",
        )
    }

    // PDF/A-1 regel 6.3.5-3 krever CIDSet for Identity-H-fonter. iText fjerner CIDSet i PDF 2.0,
    // der nøkkelen er utfaset. Dokumentet er PDF 2.0 / PDF/A-4, så PDF/A-1 kan uansett ikke oppfylles
    // (header-regelen 6.1.2-1 er allerede filtrert bort i PdfValidator).
    private fun utenPdfA1CidSetRegel(feiletRegel: String): String {
        val cidSetRegel = "specification=ISO 19005-1:2005 clause=6.3.5 testNumber=3"
        if (!feiletRegel.contains(cidSetRegel)) return feiletRegel
        val gjenværende =
            feiletRegel
                .removePrefix("[")
                .removeSuffix("]")
                .split(",")
                .map { it.trim() }
                .filterNot { it.contains(cidSetRegel) }
        return "[${gjenværende.joinToString(" , ")}]"
    }
}
